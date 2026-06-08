<template>
  <div class="ai-layout">
    <!-- 左侧会话列表 -->
    <aside class="session-panel">
      <el-button class="new-chat-btn" type="primary" :icon="Plus" @click="startNewChat">
        新对话
      </el-button>
      <div class="session-list">
        <div
          v-for="s in sessions"
          :key="s.sessionId"
          class="session-item"
          :class="{ active: s.sessionId === currentSessionId }"
          @click="openSession(s.sessionId)"
        >
          <el-icon class="session-icon"><ChatLineRound /></el-icon>
          <div class="session-meta">
            <div class="session-title">{{ s.title }}</div>
            <div class="session-time">{{ formatTime(s.updateTime) }}</div>
          </div>
          <el-icon class="session-del" @click.stop="removeSession(s.sessionId)"><Delete /></el-icon>
        </div>
        <div v-if="sessions.length === 0" class="session-empty">暂无历史对话</div>
      </div>
    </aside>

    <!-- 右侧对话区 -->
    <section class="chat-main">
      <div class="messages" ref="messagesRef">
        <div v-if="messages.length === 0 && !loading" class="empty-tip">
          <el-icon :size="44" style="color:var(--accent)"><ChatDotRound /></el-icon>
          <p class="empty-title">智能猫舍助手</p>
          <p class="empty-sub">你可以问我：哪只猫体重最重？本月有哪些待处理提醒？</p>
        </div>
        <div v-for="(msg, i) in messages" :key="i" class="message-group">
          <div class="bubble user">{{ msg.userMessage }}</div>
          <div class="bubble ai">
            <span class="ai-label">AI</span>
            <div class="ai-content" v-html="render(msg.aiResponse)"></div>
          </div>
        </div>
        <div v-if="loading" class="bubble ai loading">
          <span class="dot"></span><span class="dot"></span><span class="dot"></span>
        </div>
      </div>

      <div class="input-bar">
        <el-input
          v-model="inputText"
          type="textarea"
          :rows="1"
          autosize
          resize="none"
          placeholder="问我关于猫舍的任何问题...（Enter 发送，Shift+Enter 换行）"
          @keydown.enter="onEnter"
          :disabled="loading"
        />
        <el-button type="primary" @click="send" :loading="loading" :icon="Promotion">发送</el-button>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { Plus, Promotion, Delete } from '@element-plus/icons-vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { sendMessage, getSessions, getSessionMessages, deleteSession } from '../api/ai'
import { renderMarkdown } from '../utils/markdown'

const sessions = ref([])
const currentSessionId = ref(null)
const messages = ref([])
const inputText = ref('')
const loading = ref(false)
const messagesRef = ref(null)

const render = renderMarkdown

onMounted(async () => {
  await loadSessions()
})

async function loadSessions() {
  try {
    sessions.value = (await getSessions()).data || []
  } catch (e) { /* 未登录或无数据时静默处理 */ }
}

function startNewChat() {
  currentSessionId.value = generateId()
  messages.value = []
}

async function openSession(sessionId) {
  currentSessionId.value = sessionId
  try {
    messages.value = (await getSessionMessages(sessionId)).data || []
    await nextTick()
    scrollToBottom()
  } catch (e) { ElMessage.error('加载对话失败') }
}

async function removeSession(sessionId) {
  await ElMessageBox.confirm('删除该对话？', '提示', { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' })
  await deleteSession(sessionId)
  sessions.value = sessions.value.filter(s => s.sessionId !== sessionId)
  if (currentSessionId.value === sessionId) {
    currentSessionId.value = null
    messages.value = []
  }
}

async function send() {
  const text = inputText.value.trim()
  if (!text || loading.value) return

  // 第一次发送时自动创建新 sessionId
  if (!currentSessionId.value) currentSessionId.value = generateId()

  loading.value = true
  inputText.value = ''
  // 乐观展示用户消息
  const tempMsg = { userMessage: text, aiResponse: '' }
  messages.value.push(tempMsg)
  await nextTick(); scrollToBottom()

  try {
    const res = await sendMessage(text, currentSessionId.value)
    tempMsg.aiResponse = res.data
    await nextTick(); scrollToBottom()
    await loadSessions() // 刷新左侧会话列表（更新标题/时间）
  } catch (e) {
    tempMsg.aiResponse = '请求失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

function onEnter(e) {
  if (!e.shiftKey) { e.preventDefault(); send() }
}

function scrollToBottom() {
  if (messagesRef.value) messagesRef.value.scrollTop = messagesRef.value.scrollHeight
}

function generateId() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, c => {
    const r = Math.random() * 16 | 0
    return (c === 'x' ? r : (r & 0x3 | 0x8)).toString(16)
  })
}

function formatTime(t) {
  if (!t) return ''
  const d = new Date(t)
  const now = new Date()
  if (d.toDateString() === now.toDateString()) {
    return d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  }
  return d.toLocaleDateString('zh-CN', { month: 'numeric', day: 'numeric' })
}
</script>

<style scoped>
.ai-layout {
  display: flex;
  height: calc(100vh - 112px);
  gap: 0;
  overflow: hidden;
}

/* 左侧 */
.session-panel {
  width: 220px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  background: var(--bg-secondary);
  border-right: 1px solid var(--border-color);
  border-radius: 12px 0 0 12px;
  overflow: hidden;
}

.new-chat-btn {
  margin: 14px 12px 10px;
  border-radius: 8px !important;
  justify-content: center;
}

.session-list {
  flex: 1;
  overflow-y: auto;
  padding: 4px 8px;
}

.session-empty {
  font-size: 12px;
  color: var(--text-secondary);
  text-align: center;
  padding: 24px 0;
}

.session-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 9px 10px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.15s;
  position: relative;
}
.session-item:hover { background: var(--bg-hover); }
.session-item.active { background: var(--accent-glow); }
.session-item:hover .session-del { opacity: 1; }

.session-icon { color: var(--text-secondary); flex-shrink: 0; font-size: 15px; }

.session-meta { flex: 1; min-width: 0; }
.session-title {
  font-size: 13px;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.session-time { font-size: 11px; color: var(--text-secondary); margin-top: 2px; }

.session-del {
  font-size: 13px;
  color: var(--text-secondary);
  opacity: 0;
  transition: opacity 0.15s;
  flex-shrink: 0;
}
.session-del:hover { color: #f56c6c; }

/* 右侧 */
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: var(--bg-primary);
  border-radius: 0 12px 12px 0;
}

.messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px 24px 8px;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.empty-tip {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding-bottom: 60px;
}
.empty-title { font-size: 18px; font-weight: 600; color: var(--text-primary); margin: 0; }
.empty-sub { font-size: 13px; color: var(--text-secondary); margin: 0; }

.message-group { display: flex; flex-direction: column; gap: 10px; }

.bubble {
  max-width: 72%;
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.7;
  word-break: break-word;
}

.bubble.user {
  background: var(--accent);
  color: #fff;
  align-self: flex-end;
  border-bottom-right-radius: 4px;
  white-space: pre-wrap;
}

.bubble.ai {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  color: var(--text-primary);
  align-self: flex-start;
  border-bottom-left-radius: 4px;
}

.ai-label {
  display: inline-block;
  font-size: 10px;
  font-weight: 700;
  background: linear-gradient(135deg, var(--accent), #764ba2);
  color: #fff;
  padding: 1px 6px;
  border-radius: 4px;
  margin-right: 8px;
  vertical-align: middle;
}

/* markdown 内容样式 */
.ai-content { display: inline; }
.ai-content :deep(p) { margin: 6px 0; }
.ai-content :deep(pre) {
  background: #1e1e2e;
  border-radius: 8px;
  padding: 12px 16px;
  overflow-x: auto;
  margin: 8px 0;
}
.ai-content :deep(code) { font-family: 'JetBrains Mono', monospace; font-size: 13px; }
.ai-content :deep(p code) {
  background: rgba(64,158,255,0.1);
  border-radius: 3px;
  padding: 1px 5px;
  font-size: 12px;
}
.ai-content :deep(ul), .ai-content :deep(ol) { padding-left: 20px; margin: 6px 0; }
.ai-content :deep(li) { margin: 3px 0; }
.ai-content :deep(h1), .ai-content :deep(h2), .ai-content :deep(h3) {
  margin: 10px 0 4px; font-weight: 600;
}

.bubble.loading {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 14px 20px;
}

.dot {
  width: 6px; height: 6px;
  border-radius: 50%;
  background: var(--text-secondary);
  animation: bounce 1.2s infinite;
}
.dot:nth-child(2) { animation-delay: 0.2s; }
.dot:nth-child(3) { animation-delay: 0.4s; }
@keyframes bounce {
  0%, 80%, 100% { transform: scale(0.8); opacity: 0.5; }
  40% { transform: scale(1.2); opacity: 1; }
}

.input-bar {
  display: flex;
  align-items: flex-end;
  gap: 10px;
  padding: 14px 24px;
  border-top: 1px solid var(--border-color);
}
.input-bar .el-textarea :deep(textarea) {
  background: var(--bg-secondary);
  border-color: var(--border-color);
  color: var(--text-primary);
  max-height: 160px;
}
</style>
