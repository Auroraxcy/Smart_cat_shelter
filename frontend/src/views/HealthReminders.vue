<template>
  <div class="page">
    <div class="page-header">
      <h2>健康提醒</h2>
      <el-button v-if="userStore.isAdmin || userStore.isKeeper" type="primary" @click="showAddDialog">+ 添加提醒</el-button>
    </div>

    <div class="card">
      <el-table :data="reminders" style="width:100%">
        <el-table-column prop="title" label="标题" width="150" />
        <el-table-column prop="cat.name" label="猫咪" width="90" />
        <el-table-column label="提醒时间" width="180">
          <template #default="{ row }">{{ formatDateTime(row.remindTime) }}</template>
        </el-table-column>
        <el-table-column label="优先级" width="90">
          <template #default="{ row }">
            <span class="priority-badge" :class="row.priority?.toLowerCase()">{{ row.priority }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <span class="status-badge" :class="row.completed ? 'done' : 'pending'">
              {{ row.completed ? '已完成' : '待完成' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button v-if="userStore.isKeeper && !row.completed" size="small" type="success" @click="handleComplete(row.id)">完成</el-button>
            <el-button v-if="userStore.isAdmin" size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" title="添加提醒" width="480px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="猫咪">
          <el-select v-model="form.catId" placeholder="选择猫咪" style="width:100%">
            <el-option v-for="cat in cats" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="提醒时间">
          <el-date-picker v-model="form.remindTime" type="datetime" placeholder="选择日期时间" style="width:100%" />
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="form.priority" style="width:100%">
            <el-option label="高" value="HIGH" /><el-option label="中" value="MEDIUM" /><el-option label="低" value="LOW" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllReminders, createReminder, markCompleted, deleteReminder } from '../api/reminder'
import { getAllCats } from '../api/cat'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()
const reminders = ref([])
const cats = ref([])
const dialogVisible = ref(false)
const loading = ref(false)
const form = reactive({ title: '', catId: null, remindTime: '', priority: 'MEDIUM', description: '' })

const loadReminders = async () => { try { reminders.value = (await getAllReminders()).data } catch (e) { console.error(e) } }
const loadCats = async () => { try { cats.value = (await getAllCats()).data } catch (e) { console.error(e) } }

const showAddDialog = () => {
  Object.assign(form, { title: '', catId: null, remindTime: '', priority: 'MEDIUM', description: '' })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  loading.value = true
  try {
    await createReminder({ title: form.title, cat: { id: form.catId }, remindTime: form.remindTime, priority: form.priority, description: form.description })
    ElMessage.success('添加成功'); dialogVisible.value = false; loadReminders()
  } catch (e) { console.error(e) } finally { loading.value = false }
}

const handleComplete = async (id) => {
  try { await markCompleted(id); ElMessage.success('已标记完成'); loadReminders() } catch (e) { console.error(e) }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除吗？', '提示', { type: 'warning' })
    await deleteReminder(id); ElMessage.success('删除成功'); loadReminders()
  } catch (e) { if (e !== 'cancel') console.error(e) }
}

const formatDateTime = (dt) => dt ? new Date(dt).toLocaleString('zh-CN') : ''

onMounted(() => { loadReminders(); loadCats() })
</script>

<style scoped>
.page { display: flex; flex-direction: column; gap: 16px; }
.page-header { display: flex; justify-content: space-between; align-items: center; }
.page-header h2 { margin: 0; font-size: 16px; font-weight: 600; color: var(--text-primary); }
.card { background: var(--bg-card); border: 1px solid var(--border-color); border-radius: 12px; overflow: hidden; }

.priority-badge, .status-badge {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
}
.priority-badge.high { background: rgba(245,108,108,0.2); color: #f56c6c; }
.priority-badge.medium { background: rgba(230,162,60,0.2); color: #e6a23c; }
.priority-badge.low { background: rgba(64,158,255,0.2); color: #409eff; }
.status-badge.done { background: rgba(103,194,58,0.2); color: #67c23a; }
.status-badge.pending { background: rgba(230,162,60,0.2); color: #e6a23c; }
</style>
