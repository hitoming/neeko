import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)
  
  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  
  // 方法
  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }
  
  const setUserInfo = (info) => {
    userInfo.value = info
  }
  
  const login = async (data) => {
    const res = await authApi.login(data)
    setToken(res.data)
    await fetchUserInfo()
    return res
  }
  
  const register = async (data) => {
    return await authApi.register(data)
  }
  
  const fetchUserInfo = async () => {
    if (!token.value) return
    try {
      const res = await authApi.getUserInfo()
      userInfo.value = res.data
    } catch (error) {
      console.error('获取用户信息失败', error)
    }
  }
  
  const logout = async () => {
    try {
      await authApi.logout()
    } catch (error) {
      console.error('退出登录失败', error)
    }
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }
  
  return {
    token,
    userInfo,
    isLoggedIn,
    setToken,
    setUserInfo,
    login,
    register,
    fetchUserInfo,
    logout
  }
})
