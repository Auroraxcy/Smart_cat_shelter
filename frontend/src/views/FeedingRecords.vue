<template>
  <div class="page">
    <div class="page-header">
      <h2>喂食记录</h2>
      <el-button v-if="userStore.isKeeper" type="primary" @click="showAddDialog">+ 添加记录</el-button>
    </div>

    <div class="card">
      <el-table :data="records" style="width:100%">
        <el-table-column prop="cat.name" label="猫咪" width="100" />
        <el-table-column label="喂食时间" width="180">
          <template #default="{ row }">{{ formatDateTime(row.feedingTime) }}</template>
        </el-table-column>
        <el-table-column prop="foodType" label="食物类型" width="120" />
        <el-table-column prop="amount" label="喂食量(g)" width="100" />
        <el-table-column prop="keeper.username" label="饲养员" width="110" />
        <el-table-column prop="notes" label="备注" />
        <el-table-column label="操作" width="90" fixed="right">
          <template #default="{ row }">
            <el-button v-if="userStore.isAdmin" size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" title="添加喂食记录" width="480px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="猫咪">
          <el-select v-model="form.catId" placeholder="选择猫咪" style="width:100%">
            <el-option v-for="cat in cats" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="食物类型"><el-input v-model="form.foodType" /></el-form-item>
        <el-form-item label="喂食量(g)"><el-input-number v-model="form.amount" :min="0" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.notes" type="textarea" /></el-form-item>
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
import { getAllRecords, createRecord, deleteRecord } from '../api/feeding'
import { getAllCats } from '../api/cat'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()
const records = ref([])
const cats = ref([])
const dialogVisible = ref(false)
const loading = ref(false)
const form = reactive({ catId: null, foodType: '', amount: 0, notes: '' })

const loadRecords = async () => { try { records.value = (await getAllRecords()).data } catch (e) { console.error(e) } }
const loadCats = async () => { try { cats.value = (await getAllCats()).data } catch (e) { console.error(e) } }

const showAddDialog = () => {
  Object.assign(form, { catId: null, foodType: '', amount: 0, notes: '' })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  loading.value = true
  try {
    await createRecord({ cat: { id: form.catId }, foodType: form.foodType, amount: form.amount, notes: form.notes })
    ElMessage.success('添加成功'); dialogVisible.value = false; loadRecords()
  } catch (e) { console.error(e) } finally { loading.value = false }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除吗？', '提示', { type: 'warning' })
    await deleteRecord(id); ElMessage.success('删除成功'); loadRecords()
  } catch (e) { if (e !== 'cancel') console.error(e) }
}

const formatDateTime = (dt) => dt ? new Date(dt).toLocaleString('zh-CN') : ''

onMounted(() => { loadRecords(); loadCats() })
</script>

<style scoped>
.page { display: flex; flex-direction: column; gap: 16px; }
.page-header { display: flex; justify-content: space-between; align-items: center; }
.page-header h2 { margin: 0; font-size: 16px; font-weight: 600; color: var(--text-primary); }
.card { background: var(--bg-card); border: 1px solid var(--border-color); border-radius: 12px; overflow: hidden; }
</style>
