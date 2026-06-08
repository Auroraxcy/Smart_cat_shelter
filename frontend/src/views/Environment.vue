<template>
  <div class="page">
    <div class="page-header">
      <h2>环境监控</h2>
      <el-button v-if="userStore.isKeeper" type="primary" @click="showAddDialog">+ 录入数据</el-button>
    </div>

    <div class="env-cards" v-if="latestData">
      <div class="env-card" v-for="item in envItems" :key="item.key">
        <div class="env-icon" :style="{ color: item.color }">
          <el-icon :size="20"><component :is="item.icon" /></el-icon>
        </div>
        <div class="env-value" :style="{ color: item.color }">{{ latestData[item.key] }}{{ item.unit }}</div>
        <div class="env-label">{{ item.label }}</div>
      </div>
      <div class="env-card">
        <div class="env-icon" style="color:#8892a4"><el-icon :size="20"><Location /></el-icon></div>
        <div class="env-value">{{ latestData.area }}</div>
        <div class="env-label">区域</div>
      </div>
    </div>
    <div v-else class="card empty-card">
      <el-empty description="暂无环境数据" />
    </div>

    <div class="card">
      <div class="query-bar">
        <span class="section-title">历史数据</span>
        <div class="query-form">
          <el-input v-model="queryArea" placeholder="输入区域查询" style="width:200px" />
          <el-button type="primary" @click="loadHistory">查询</el-button>
        </div>
      </div>
      <el-table :data="historyData" style="width:100%">
        <el-table-column prop="area" label="区域" width="120" />
        <el-table-column prop="temperature" label="温度(°C)" width="110" />
        <el-table-column prop="humidity" label="湿度(%)" width="100" />
        <el-table-column prop="airQuality" label="空气质量" width="110" />
        <el-table-column label="记录时间">
          <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" title="录入环境数据" width="480px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="区域"><el-input v-model="form.area" /></el-form-item>
        <el-form-item label="温度(°C)"><el-input-number v-model="form.temperature" :precision="1" /></el-form-item>
        <el-form-item label="湿度(%)"><el-input-number v-model="form.humidity" :precision="1" /></el-form-item>
        <el-form-item label="空气质量"><el-input-number v-model="form.airQuality" :precision="1" /></el-form-item>
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
import { ElMessage } from 'element-plus'
import { getLatestData, getHistory, createRecord } from '../api/environment'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()
const latestData = ref(null)
const historyData = ref([])
const queryArea = ref('')
const dialogVisible = ref(false)
const loading = ref(false)
const form = reactive({ area: '', temperature: 0, humidity: 0, airQuality: 0 })

const envItems = [
  { key: 'temperature', label: '温度', unit: '°C', icon: 'Sunny', color: '#e6a23c' },
  { key: 'humidity', label: '湿度', unit: '%', icon: 'Cloudy', color: '#409eff' },
  { key: 'airQuality', label: '空气质量', unit: '', icon: 'Wind', color: '#67c23a' },
]

const loadLatestData = async () => { try { latestData.value = (await getLatestData()).data } catch (e) { console.error(e) } }

const loadHistory = async () => {
  if (!queryArea.value) { ElMessage.warning('请输入区域'); return }
  try { historyData.value = (await getHistory({ area: queryArea.value })).data } catch (e) { console.error(e) }
}

const showAddDialog = () => {
  Object.assign(form, { area: '', temperature: 0, humidity: 0, airQuality: 0 })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  loading.value = true
  try {
    await createRecord(form); ElMessage.success('录入成功'); dialogVisible.value = false; loadLatestData()
  } catch (e) { console.error(e) } finally { loading.value = false }
}

const formatDateTime = (dt) => dt ? new Date(dt).toLocaleString('zh-CN') : ''

onMounted(loadLatestData)
</script>

<style scoped>
.page { display: flex; flex-direction: column; gap: 16px; }
.page-header { display: flex; justify-content: space-between; align-items: center; }
.page-header h2 { margin: 0; font-size: 16px; font-weight: 600; color: var(--text-primary); }
.card { background: var(--bg-card); border: 1px solid var(--border-color); border-radius: 12px; overflow: hidden; }
.empty-card { padding: 40px; }

.env-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
}

.env-card {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  transition: border-color 0.2s;
}
.env-card:hover { border-color: rgba(64,158,255,0.4); }

.env-icon { margin-bottom: 8px; }
.env-value { font-size: 24px; font-weight: 700; color: var(--text-primary); }
.env-label { font-size: 12px; color: var(--text-secondary); margin-top: 4px; }

.query-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-color);
}
.section-title { font-size: 14px; font-weight: 600; color: var(--text-primary); }
.query-form { display: flex; gap: 8px; }
</style>
