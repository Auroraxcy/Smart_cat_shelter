<template>
  <div class="layout">
    <aside class="sidebar">
      <div class="logo">
        <span class="logo-icon">🐱</span>
        <span class="logo-text">智能猫舍</span>
      </div>
      <nav class="nav">
        <router-link
          v-for="item in navItems"
          :key="item.path"
          :to="item.path"
          class="nav-item"
          :class="{ active: route.path === item.path }"
        >
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.label }}</span>
        </router-link>
      </nav>
      <div class="sidebar-footer">
        <div class="user-badge">
          <div class="avatar">{{ userStore.userInfo.username?.[0]?.toUpperCase() }}</div>
          <div class="user-info">
            <div class="username">{{ userStore.userInfo.username }}</div>
            <div class="role-tag" :class="userStore.userInfo.role?.toLowerCase()">
              {{ userStore.userInfo.role }}
            </div>
          </div>
        </div>
        <el-button class="logout-btn" @click="handleLogout" :icon="SwitchButton" circle />
      </div>
    </aside>

    <div class="main">
      <header class="topbar">
        <h1 class="page-title">{{ pageTitle }}</h1>
        <div class="topbar-right">
          <span class="time">{{ currentTime }}</span>
        </div>
      </header>
      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { logout } from '../api/auth'
import { ElMessage } from 'element-plus'
import { SwitchButton } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const navItems = [
  { path: '/dashboard', label: '数据看板', icon: 'DataBoard' },
  { path: '/cats', label: '猫咪管理', icon: 'Van' },
  { path: '/feeding', label: '喂食记录', icon: 'Food' },
  { path: '/reminders', label: '健康提醒', icon: 'Bell' },
  { path: '/environment', label: '环境监控', icon: 'Sunny' },
  { path: '/ai-chat', label: 'AI 助手', icon: 'ChatDotRound' },
]

const pageTitles = {
  '/dashboard': '数据看板',
  '/cats': '猫咪管理',
  '/feeding': '喂食记录',
  '/reminders': '健康提醒',
  '/environment': '环境监控',
  '/ai-chat': 'AI 助手',
}

const pageTitle = computed(() => pageTitles[route.path] || '智能猫舍')

const currentTime = ref('')
let timer
onMounted(() => {
  const update = () => {
    currentTime.value = new Date().toLocaleString('zh-CN', { hour12: false })
  }
  update()
  timer = setInterval(update, 1000)
})
onUnmounted(() => clearInterval(timer))

const handleLogout = async () => {
  await logout()
  userStore.logout()
  ElMessage.success('退出成功')
  router.push('/login')
}
</script>

<style scoped>
.layout {
  display: flex;
  height: 100vh;
  background: var(--bg-primary);
}

.sidebar {
  width: 220px;
  background: var(--bg-secondary);
  border-right: 1px solid var(--border-color);
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 20px;
  border-bottom: 1px solid var(--border-color);
}

.logo-icon { font-size: 24px; }
.logo-text {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: 1px;
}

.nav {
  flex: 1;
  padding: 12px 8px;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  border-radius: 8px;
  color: var(--text-secondary);
  text-decoration: none;
  font-size: 14px;
  transition: all 0.2s;
}

.nav-item:hover {
  background: var(--bg-hover);
  color: var(--text-primary);
}

.nav-item.active {
  background: var(--accent-glow);
  color: var(--accent);
  font-weight: 500;
}

.nav-item .el-icon { font-size: 16px; }

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-badge {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
  min-width: 0;
}

.avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--accent), #764ba2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 700;
  color: #fff;
  flex-shrink: 0;
}

.user-info { min-width: 0; }
.username {
  font-size: 13px;
  font-weight: 500;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.role-tag {
  font-size: 11px;
  padding: 1px 6px;
  border-radius: 4px;
  display: inline-block;
  margin-top: 2px;
}
.role-tag.admin { background: rgba(245,108,108,0.2); color: #f56c6c; }
.role-tag.keeper { background: rgba(230,162,60,0.2); color: #e6a23c; }
.role-tag.user { background: rgba(64,158,255,0.2); color: #409eff; }

.logout-btn {
  background: transparent !important;
  border: 1px solid var(--border-color) !important;
  color: var(--text-secondary) !important;
  flex-shrink: 0;
}
.logout-btn:hover {
  border-color: var(--danger) !important;
  color: var(--danger) !important;
}

.main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.topbar {
  height: 64px;
  background: var(--bg-secondary);
  border-bottom: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 28px;
  flex-shrink: 0;
}

.page-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
}

.topbar-right .time {
  font-size: 13px;
  color: var(--text-secondary);
  font-variant-numeric: tabular-nums;
}

.content {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
}
</style>
