import request from '../utils/request'

// 发送消息（携带当前会话 sessionId）
export const sendMessage = (message, sessionId) =>
  request.post('/ai/chat', { message, sessionId })

// 获取历史会话列表
export const getSessions = () => request.get('/ai/sessions')

// 获取某个会话的全部消息
export const getSessionMessages = (sessionId) =>
  request.get(`/ai/sessions/${sessionId}/messages`)

// 删除某个会话
export const deleteSession = (sessionId) =>
  request.delete(`/ai/sessions/${sessionId}`)
