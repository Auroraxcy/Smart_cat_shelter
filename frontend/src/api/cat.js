import request from '../utils/request'

export const getAllCats = () => {
  return request.get('/cats')
}

export const getCatById = (id) => {
  return request.get(`/cats/${id}`)
}

export const createCat = (data) => {
  return request.post('/cats', data)
}

export const updateCat = (id, data) => {
  return request.put(`/cats/${id}`, data)
}

export const deleteCat = (id) => {
  return request.delete(`/cats/${id}`)
}
