import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

function extractBackendMessage(data) {
  if (data == null) return ''
  if (typeof data === 'string') {
    const s = data.trim()
    if (s.startsWith('<')) return '服务器返回了网页而非接口数据，请确认后端已启动（端口 8081）且地址正确'
    return s.length > 120 ? `${s.slice(0, 120)}…` : s
  }
  if (typeof data === 'object') {
    return data.message || data.msg || data.error || ''
  }
  return ''
}

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    const okObject = res !== null && typeof res === 'object' && !Array.isArray(res) && 'code' in res
    if (!okObject) {
      console.error('[api] 非预期响应:', response.config?.url, res)
      const hint = '接口未返回 JSON（请确认已运行后端: mvn spring-boot:run，端口 8081）'
      ElMessage.error(hint)
      return Promise.reject(new Error(hint))
    }
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    if (error.response) {
      if (error.response.status === 401) {
        ElMessage.error('登录已过期，请重新登录')
        localStorage.removeItem('token')
        router.push('/login')
      } else {
        const msg = extractBackendMessage(error.response.data)
        ElMessage.error(msg || `请求失败 (${error.response.status})`)
      }
    } else {
      ElMessage.error('无法连接后端（请检查是否已启动后端、端口是否为 8081）')
    }
    return Promise.reject(error)
  }
)

export default request

// ============ 认证相关 ============
export const login = (data) => request.post('/auth/login', data)
export const register = (data) => request.post('/auth/register', data)
export const logout = () => request.post('/auth/logout')
export const getUserInfo = () => request.get('/auth/info')
export const getUserStats = () => request.get('/user/stats')
export const updateUserProfile = (data) => request.put('/user/profile', data)
export const uploadAvatar = (data) => request.post('/user/avatar', data)

// ============ 照片相关 ============
export const uploadPhoto = (data) => request.post('/photo/upload', data, {
  headers: { 'Content-Type': 'multipart/form-data' }
})
export const getPublicPhotos = (params) => request.get('/photo/public', { params })
export const getPhotosByLocation = (params) => request.get('/photo/nearby', { params })
export const getPhotoList = (params) => request.get('/photo/list', { params })
export const updatePhoto = (data) => request.put('/photo/update', data)
export const deletePhoto = (id) => request.delete(`/photo/${id}`)
export const getPhotoDetail = (id) => request.get(`/photo/${id}`)

// ============ 相册相关 ============
export const getAlbumList = () => request.get('/album/list')
export const getAlbumDetail = (id) => request.get(`/album/${id}`)
export const createAlbum = (data) => request.post('/album/create', data)
export const updateAlbum = (id, data) => request.put(`/album/${id}`, data)
export const deleteAlbum = (id) => request.delete(`/album/${id}`)

// ============ 标签相关 ============
export const getTagList = () => request.get('/tag/list')

// ============ 好友相关 ============
export const getFriends = () => request.get('/friend/list')
export const getPendingFriends = () => request.get('/friend/pending')
export const searchUsers = (keyword) => request.get('/friend/search', { params: { keyword } })
export const addFriend = (userId) => request.post('/friend/add', { friendId: userId })
export const confirmFriend = (id) => request.post(`/friend/confirm/${id}`)
export const rejectFriend = (id) => request.post(`/friend/reject/${id}`)
export const deleteFriend = (id) => request.delete(`/friend/${id}`)
export const getChatHistory = (friendId) => request.get(`/chat/history/${friendId}`)
export const sendMessage = (data) => request.post('/chat/send', data)

// ============ 地图相关 ============
export const getMapConfig = () => request.get('/map/config')
export const searchNearby = (params) => request.get('/map/searchNearby', { params })
