import request from '../utils/request'

export const getAllReminders = () => {
  return request.get('/reminders')
}

export const getPendingReminders = () => {
  return request.get('/reminders/pending')
}

export const createReminder = (data) => {
  return request.post('/reminders', data)
}

export const markCompleted = (id) => {
  return request.put(`/reminders/${id}/complete`)
}

export const deleteReminder = (id) => {
  return request.delete(`/reminders/${id}`)
}
