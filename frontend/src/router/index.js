import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/map'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/map',
    name: 'MapHome',
    component: () => import('@/views/MapHome.vue'),
    meta: { title: '地图首页' }
  },
  {
    path: '/album',
    name: 'Album',
    component: () => import('@/views/Album.vue'),
    meta: { title: '我的相册', requireAuth: true }
  },
  {
    path: '/album/:id',
    name: 'AlbumDetail',
    component: () => import('@/views/AlbumDetail.vue'),
    meta: { title: '相册详情', requireAuth: true }
  },
  {
    path: '/photo/upload',
    name: 'PhotoUpload',
    component: () => import('@/views/PhotoUpload.vue'),
    meta: { title: '上传照片', requireAuth: true }
  },
  {
    path: '/friends',
    name: 'Friends',
    component: () => import('@/views/Friends.vue'),
    meta: { title: '我的好友', requireAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/Profile.vue'),
    meta: { title: '个人中心', requireAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - NeekoMap` : 'NeekoMap'
  
  // 检查是否需要登录
  if (to.meta.requireAuth) {
    const token = localStorage.getItem('token')
    if (!token) {
      next({ path: '/login', query: { redirect: to.fullPath } })
      return
    }
  }
  
  next()
})

export default router
