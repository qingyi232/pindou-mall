import request from './request'

export const userApi = {
  login: (data) => request.post('/api/user/login', data),
  register: (data) => request.post('/api/user/register', data),
  getInfo: () => request.get('/api/user/info'),
  update: (data) => request.put('/api/user/update', data),
  updatePassword: (data) => request.put('/api/user/password', data),
  list: (params) => request.get('/api/user/list', { params }),
  updateStatus: (id, data) => request.put(`/api/user/status/${id}`, data)
}

export const categoryApi = {
  list: () => request.get('/api/category/list'),
  all: () => request.get('/api/category/all'),
  add: (data) => request.post('/api/category/add', data),
  update: (data) => request.put('/api/category/update', data),
  delete: (id) => request.delete(`/api/category/delete/${id}`)
}

export const productApi = {
  list: (params) => request.get('/api/product/list', { params }),
  detail: (id) => request.get(`/api/product/detail/${id}`),
  hot: () => request.get('/api/product/hot'),
  newProducts: () => request.get('/api/product/new'),
  search: (params) => request.get('/api/product/search', { params }),
  adminList: (params) => request.get('/api/product/admin/list', { params }),
  add: (data) => request.post('/api/product/add', data),
  update: (data) => request.put('/api/product/update', data),
  delete: (id) => request.delete(`/api/product/delete/${id}`),
  updateStatus: (id, data) => request.put(`/api/product/status/${id}`, data)
}

export const cartApi = {
  list: () => request.get('/api/cart/list'),
  add: (data) => request.post('/api/cart/add', data),
  update: (data) => request.put('/api/cart/update', data),
  delete: (id) => request.delete(`/api/cart/delete/${id}`),
  clear: () => request.delete('/api/cart/clear')
}

export const orderApi = {
  create: (data) => request.post('/api/order/create', data),
  list: (params) => request.get('/api/order/list', { params }),
  detail: (id) => request.get(`/api/order/detail/${id}`),
  pay: (id) => request.put(`/api/order/pay/${id}`),
  cancel: (id) => request.put(`/api/order/cancel/${id}`),
  confirm: (id) => request.put(`/api/order/confirm/${id}`),
  adminList: (params) => request.get('/api/order/admin/list', { params }),
  ship: (id) => request.put(`/api/order/admin/ship/${id}`),
  stats: () => request.get('/api/order/admin/stats')
}

export const reviewApi = {
  list: (productId, params) => request.get(`/api/review/list/${productId}`, { params }),
  add: (data) => request.post('/api/review/add', data)
}

export const bannerApi = {
  list: () => request.get('/api/banner/list'),
  all: () => request.get('/api/banner/all'),
  add: (data) => request.post('/api/banner/add', data),
  update: (data) => request.put('/api/banner/update', data),
  delete: (id) => request.delete(`/api/banner/delete/${id}`)
}

export const addressApi = {
  list: () => request.get('/api/address/list'),
  add: (data) => request.post('/api/address/add', data),
  update: (data) => request.put('/api/address/update', data),
  delete: (id) => request.delete(`/api/address/delete/${id}`)
}

export const fileApi = {
  upload: (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/api/file/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}
