import request from '../utils/request'

export const getAllRecords = () => {
  return request.get('/feeding-records')
}

export const getRecordsByCatId = (catId) => {
  return request.get(`/feeding-records/cat/${catId}`)
}

export const getTodayTotal = () => {
  return request.get('/feeding-records/today-total')
}

export const createRecord = (data) => {
  return request.post('/feeding-records', data)
}

export const deleteRecord = (id) => {
  return request.delete(`/feeding-records/${id}`)
}
