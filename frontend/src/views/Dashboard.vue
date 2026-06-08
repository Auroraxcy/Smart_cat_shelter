<template>
  <div class="dashboard">
    <div class="stat-grid">
      <div class="stat-card" v-for="stat in statCards" :key="stat.key">
        <div class="stat-icon" :style="{ background: stat.gradient }">
          <el-icon :size="22"><component :is="stat.icon" /></el-icon>
        </div>
        <div class="stat-body">
          <div class="stat-value">{{ stats[stat.key] || 0 }}{{ stat.unit }}</div>
          <div class="stat-label">{{ stat.label }}</div>
        </div>
        <div class="stat-glow" :style="{ background: stat.glow }"></div>
      </div>
    </div>

    <div class="welcome-card">
      <div class="welcome-left">
        <div class="welcome-avatar">{{ userStore.userInfo.username?.[0]?.toUpperCase() }}</div>
        <div>
          <div class="welcome-title">欢迎回来，{{ userStore.userInfo.username }}</div>
          <div class="welcome-sub">
            <span class="role-badge" :class="userStore.userInfo.role?.toLowerCase()">
              {{ userStore.userInfo.role }}
            </span>
            <span class="welcome-desc">智能猫舍管理系统</span>
          </div>
        </div>
      </div>
      <div class="welcome-modules">
        <span v-for="m in modules" :key="m" class="module-tag">{{ m }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '../stores/user'
import { getDashboardStats } from '../api/dashboard'

const userStore = useUserStore()
const stats = ref({ totalCats: 0, pendingReminders: 0, todayFeedingAmount: 0 })

const statCards = [
  { key: 'totalCats', label: '猫咪总数', icon: 'Van', unit: '', gradient: 'linear-gradient(135deg,#409eff,#764ba2)', glow: 'rgba(64,158,255,0.15)' },
  { key: 'pendingReminders', label: '待办提醒', icon: 'Bell', unit: '', gradient: 'linear-gradient(135deg,#e6a23c,#f56c6c)', glow: 'rgba(230,162,60,0.15)' },
  { key: 'todayFeedingAmount', label: '今日喂食', icon: 'Food', unit: 'g', gradient: 'linear-gradient(135deg,#67c23a,#409eff)', glow: 'rgba(103,194,58,0.15)' },
]

const modules = ['猫咪管理', '喂食记录', '健康提醒', '环境监控', 'AI 助手']

onMounted(async () => {
  try {
    const res = await getDashboardStats()
    stats.value = res.data
  } catch (e) { console.error(e) }
})
</script>

<style scoped>
.dashboard { display: flex; flex-direction: column; gap: 20px; }

.stat-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.stat-card {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 14px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 18px;
  position: relative;
  overflow: hidden;
  transition: border-color 0.2s, transform 0.2s;
}

.stat-card:hover {
  border-color: rgba(64,158,255,0.4);
  transform: translateY(-2px);
}

.stat-glow {
  position: absolute;
  top: -20px;
  right: -20px;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  filter: blur(30px);
  pointer-events: none;
}

.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.stat-value {
  font-size: 30px;
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1;
}

.stat-label {
  font-size: 13px;
  color: var(--text-secondary);
  margin-top: 6px;
}

.welcome-card {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 14px;
  padding: 24px 28px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}

.welcome-left { display: flex; align-items: center; gap: 16px; }

.welcome-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--accent), #764ba2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 700;
  color: #fff;
  flex-shrink: 0;
}

.welcome-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.welcome-sub {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 4px;
}

.role-badge {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 500;
}
.role-badge.admin { background: rgba(245,108,108,0.2); color: #f56c6c; }
.role-badge.keeper { background: rgba(230,162,60,0.2); color: #e6a23c; }
.role-badge.user { background: rgba(64,158,255,0.2); color: #409eff; }

.welcome-desc { font-size: 13px; color: var(--text-secondary); }

.welcome-modules { display: flex; gap: 8px; flex-wrap: wrap; justify-content: flex-end; }

.module-tag {
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 20px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  color: var(--text-secondary);
}
</style>
