import request from './request'

// 照片相关API
export const photoApi = {
  // 上传照片
  uploadPhoto(formData) {
    return request.post('/photo/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },
  
  // 获取公开照片列表
  getPublicPhotos(params) {
    return request.get('/photo/public', { params })
  },
  
  // 获取用户照片列表
  getUserPhotos(userId, params) {
    return request.get(`/photo/user/${userId}`, { params })
  },
  
  // 按标签筛选照片
  getPhotosByTag(tagId, params) {
    return request.get(`/photo/tag/${tagId}`, { params })
  },
  
  // 按位置范围查询照片
  getNearbyPhotos(params) {
    return request.get('/photo/nearby', { params })
  },
  
  // 更新照片位置
  updateLocation(id, params) {
    return request.put(`/photo/${id}/location`, null, { params })
  },
  
  // 删除照片
  deletePhoto(id) {
    return request.delete(`/photo/${id}`)
  }
}
