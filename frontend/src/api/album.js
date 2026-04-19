import request from './request'

// 相册相关API
export const albumApi = {
  // 创建相册
  createAlbum(data) {
    return request.post('/album', data)
  },
  
  // 获取用户相册列表
  getUserAlbums(userId, params) {
    return request.get(`/album/user/${userId}`, { params })
  },
  
  // 获取公开相册列表
  getPublicAlbums(params) {
    return request.get('/album/public', { params })
  },
  
  // 获取相册详情
  getAlbumById(id) {
    return request.get(`/album/${id}`)
  },
  
  // 更新相册
  updateAlbum(id, data) {
    return request.put(`/album/${id}`, data)
  },
  
  // 删除相册
  deleteAlbum(id) {
    return request.delete(`/album/${id}`)
  }
}
