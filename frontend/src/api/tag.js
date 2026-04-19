import request from './request'

// 标签相关API
export const tagApi = {
  // 获取所有标签
  getAllTags() {
    return request.get('/tag/list')
  }
}
