import request from './request'

// 好友相关API
export const friendApi = {
  // 添加好友
  addFriend(friendId) {
    return request.post('/friend/add', null, { params: { friendId } })
  },
  
  // 确认好友
  confirmFriend(friendId) {
    return request.post('/friend/confirm', null, { params: { friendId } })
  },
  
  // 删除好友
  deleteFriend(friendId) {
    return request.delete(`/friend/${friendId}`)
  },
  
  // 获取好友列表
  getFriendList() {
    return request.get('/friend/list')
  },
  
  // 获取待确认请求
  getPendingRequests() {
    return request.get('/friend/requests')
  },
  
  // 搜索用户
  searchUsers(keyword) {
    return request.get('/friend/search', { params: { keyword } })
  }
}

// 聊天相关API
export const chatApi = {
  // 发送消息
  sendMessage(params) {
    return request.post('/chat/send', null, { params })
  },
  
  // 获取聊天记录
  getChatHistory(friendId, limit = 50) {
    return request.get(`/chat/history/${friendId}`, { params: { limit } })
  },
  
  // 获取未读消息数
  getUnreadCount() {
    return request.get('/chat/unread')
  },
  
  // 标记已读
  markAsRead(friendId) {
    return request.post(`/chat/read/${friendId}`)
  }
}
