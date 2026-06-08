import request from '../utils/request'

export const getLatestData = () => {
  return request.get('/environment/latest')
}

export const getHistory = (params) => {
  return request.get('/environment/history', { params })
}

export const createRecord = (data) => {
  return request.post('/environment', data)
}
