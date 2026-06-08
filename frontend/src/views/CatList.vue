<template>
  <div class="page">
    <div class="page-header">
      <h2>猫咪列表</h2>
      <el-button
        v-if="userStore.isAdmin || userStore.isKeeper"
        type="primary" @click="showAddDialog"
      >+ 添加猫咪</el-button>
    </div>

    <div class="card">
      <el-table :data="cats" style="width:100%">
        <el-table-column prop="name" label="名字" width="100" />
        <el-table-column prop="breed" label="品种" width="110" />
        <el-table-column prop="color" label="颜色" width="90" />
        <el-table-column prop="age" label="年龄" width="70" />
        <el-table-column prop="gender" label="性别" width="70" />
        <el-table-column prop="weight" label="体重(kg)" width="90" />
        <el-table-column prop="healthStatus" label="健康状态" width="120">
          <template #default="{ row }">
            <span class="status-badge" :class="getStatusClass(row.healthStatus)">
              {{ row.healthStatus }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button v-if="userStore.isAdmin || userStore.isKeeper" size="small" @click="showEditDialog(row)">编辑</el-button>
            <el-button v-if="userStore.isAdmin" size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑猫咪' : '添加猫咪'" width="480px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名字"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="品种"><el-input v-model="form.breed" /></el-form-item>
        <el-form-item label="颜色"><el-input v-model="form.color" /></el-form-item>
        <el-form-item label="年龄"><el-input-number v-model="form.age" :min="0" /></el-form-item>
        <el-form-item label="性别">
          <el-select v-model="form.gender">
            <el-option label="公" value="公" /><el-option label="母" value="母" />
          </el-select>
        </el-form-item>
        <el-form-item label="体重(kg)"><el-input-number v-model="form.weight" :min="0" :step="0.1" /></el-form-item>
        <el-form-item label="健康状态"><el-input v-model="form.healthStatus" /></el-form-item>
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
import { getAllCats, createCat, updateCat, deleteCat } from '../api/cat'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()
const cats = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const loading = ref(false)

const form = reactive({ id: null, name: '', breed: '', color: '', age: 0, gender: '', weight: 0, healthStatus: '', description: '' })

const getStatusClass = (status) => {
  if (!status) return ''
  if (status.includes('健康') || status.includes('良好')) return 'good'
  if (status.includes('异常') || status.includes('病')) return 'bad'
  return 'normal'
}

const loadCats = async () => {
  try { const res = await getAllCats(); cats.value = res.data } catch (e) { console.error(e) }
}

const showAddDialog = () => {
  isEdit.value = false
  Object.assign(form, { id: null, name: '', breed: '', color: '', age: 0, gender: '', weight: 0, healthStatus: '', description: '' })
  dialogVisible.value = true
}

const showEditDialog = (row) => { isEdit.value = true; Object.assign(form, row); dialogVisible.value = true }

const handleSubmit = async () => {
  loading.value = true
  try {
    isEdit.value ? await updateCat(form.id, form) : await createCat(form)
    ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
    dialogVisible.value = false
    loadCats()
  } catch (e) { console.error(e) } finally { loading.value = false }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除吗？', '提示', { type: 'warning' })
    await deleteCat(id); ElMessage.success('删除成功'); loadCats()
  } catch (e) { if (e !== 'cancel') console.error(e) }
}

onMounted(loadCats)
</script>

<style scoped>
.page { display: flex; flex-direction: column; gap: 16px; }
.page-header { display: flex; justify-content: space-between; align-items: center; }
.page-header h2 { margin: 0; font-size: 16px; font-weight: 600; color: var(--text-primary); }
.card { background: var(--bg-card); border: 1px solid var(--border-color); border-radius: 12px; overflow: hidden; }
.status-badge { font-size: 12px; padding: 2px 8px; border-radius: 4px; }
.status-badge.good { background: rgba(103,194,58,0.2); color: #67c23a; }
.status-badge.bad { background: rgba(245,108,108,0.2); color: #f56c6c; }
.status-badge.normal { background: rgba(64,158,255,0.2); color: #409eff; }
</style>
