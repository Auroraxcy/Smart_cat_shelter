import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('../layouts/MainLayout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'cats',
        name: 'Cats',
        component: () => import('../views/CatList.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'feeding',
        name: 'Feeding',
        component: () => import('../views/FeedingRecords.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'reminders',
        name: 'Reminders',
        component: () => import('../views/HealthReminders.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'environment',
        name: 'Environment',
        component: () => import('../views/Environment.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'ai-chat',
        name: 'AiChat',
        component: () => import('../views/AiChat.vue'),
        meta: { requiresAuth: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 导航守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next('/login')
  } else if (to.path === '/login' && userStore.isLoggedIn) {
    next('/')
  } else {
    next()
  }
})

export default router
