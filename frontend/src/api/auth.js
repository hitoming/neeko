import request from './request'

// 认证相关API
export const authApi = {
  // 登录
  login(data) {
    return request.post('/auth/login', data)
  },
  
  // 注册
  register(data) {
    return request.post('/auth/register', data)
  },
  
  // 获取用户信息
  getUserInfo() {
    return request.get('/auth/info')
  },
  
  // 退出登录
  logout() {
    return request.post('/auth/logout')
  }
}
