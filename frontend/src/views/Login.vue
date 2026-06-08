<template>
  <div class="login-page">
    <div class="login-box">
      <div class="brand">
        <span class="brand-icon">🐱</span>
        <h1>智能猫舍管理系统</h1>
        <p>Smart Cat Shelter Management</p>
      </div>

      <div class="tabs">
        <button :class="{ active: activeTab === 'login' }" @click="activeTab = 'login'">登录</button>
        <button :class="{ active: activeTab === 'register' }" @click="activeTab = 'register'">注册</button>
      </div>

      <el-form v-if="activeTab === 'login'" :model="loginForm" :rules="loginRules" ref="loginFormRef" class="form">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="密码" prefix-icon="Lock" @keyup.enter="handleLogin" show-password />
        </el-form-item>
        <el-button type="primary" class="submit-btn" @click="handleLogin" :loading="loading">登录</el-button>
      </el-form>

      <el-form v-else :model="registerForm" :rules="registerRules" ref="registerFormRef" class="form">
        <el-form-item prop="username">
          <el-input v-model="registerForm.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item prop="role">
          <el-select v-model="registerForm.role" placeholder="选择角色" style="width:100%">
            <el-option label="普通用户" value="USER" />
            <el-option label="饲养员" value="KEEPER" />
          </el-select>
        </el-form-item>
        <el-button type="primary" class="submit-btn" @click="handleRegister" :loading="loading">注册</el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login, register } from '../api/auth'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const activeTab = ref('login')
const loginFormRef = ref()
const registerFormRef = ref()

const loginForm = reactive({ username: '', password: '' })
const registerForm = reactive({ username: '', password: '', role: 'USER' })

const validatePassword = (rule, value, callback) => {
  if (!value) return callback(new Error('请输入密码'))
  const isPrivileged = registerForm.role === 'ADMIN' || registerForm.role === 'KEEPER'
  if (value.length < 8) return callback(new Error('密码长度至少为8位'))
  if (!/[a-zA-Z]/.test(value)) return callback(new Error('密码必须包含字母'))
  if (!/[0-9]/.test(value)) return callback(new Error('密码必须包含数字'))
  if (!isPrivileged && !/[^a-zA-Z0-9]/.test(value)) return callback(new Error('密码必须包含特殊符号'))
  callback()
}

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}
const registerRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, validator: validatePassword, trigger: 'blur' }]
}

const handleLogin = async () => {
  loading.value = true
  try {
    const res = await login(loginForm)
    userStore.setToken(res.data.token)
    userStore.setUserInfo(res.data.user)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleRegister = async () => {
  loading.value = true
  try {
    await register(registerForm)
    ElMessage.success('注册成功，请登录')
    activeTab.value = 'login'
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: var(--bg-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  background-image: radial-gradient(ellipse at 20% 50%, rgba(64,158,255,0.08) 0%, transparent 60%),
                    radial-gradient(ellipse at 80% 20%, rgba(118,75,162,0.08) 0%, transparent 60%);
}

.login-box {
  width: 400px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 20px;
  padding: 40px;
}

.brand {
  text-align: center;
  margin-bottom: 32px;
}

.brand-icon { font-size: 40px; }

.brand h1 {
  margin: 12px 0 4px;
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
}

.brand p {
  margin: 0;
  font-size: 12px;
  color: var(--text-secondary);
  letter-spacing: 1px;
}

.tabs {
  display: flex;
  background: var(--bg-secondary);
  border-radius: 8px;
  padding: 4px;
  margin-bottom: 24px;
}

.tabs button {
  flex: 1;
  padding: 8px;
  border: none;
  background: transparent;
  color: var(--text-secondary);
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.tabs button.active {
  background: var(--bg-card);
  color: var(--text-primary);
  font-weight: 500;
  box-shadow: 0 1px 4px rgba(0,0,0,0.3);
}

.form { display: flex; flex-direction: column; gap: 4px; }

.submit-btn {
  width: 100%;
  height: 42px;
  margin-top: 8px;
  font-size: 15px;
  border-radius: 8px !important;
}
</style>
