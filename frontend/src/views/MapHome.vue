<template>
  <div class="map-home">
    <!-- 顶部导航 -->
    <div class="map-navbar">
      <div class="nav-left">
        <span class="logo" @click="$router.push('/map')">NeekoMap</span>
        <div class="nav-tabs">
          <router-link to="/map" class="nav-item active">
            <el-icon><MapLocation /></el-icon>
            地图首页
          </router-link>
          <router-link v-if="isLoggedIn" to="/album" class="nav-item">
            <el-icon><Picture /></el-icon>
            我的相册
          </router-link>
          <router-link v-if="isLoggedIn" to="/friends" class="nav-item">
            <el-icon><User /></el-icon>
            好友
          </router-link>
        </div>
      </div>
      
      <div class="nav-right">
        <template v-if="isLoggedIn">
          <el-dropdown @command="handleUserCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userStore.userInfo?.avatar">
                {{ userStore.userInfo?.nickname?.charAt(0) || 'U' }}
              </el-avatar>
              <span class="username">{{ userStore.userInfo?.nickname || '用户' }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="album">我的相册</el-dropdown-item>
                <el-dropdown-item command="upload">上传照片</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button type="primary" size="small" @click="$router.push('/login')">登录</el-button>
          <el-button size="small" @click="$router.push('/register')">注册</el-button>
        </template>
      </div>
    </div>

    <!-- 地图容器 -->
    <div class="map-wrapper">
      <div id="map" ref="mapRef" class="map-container"></div>
      
      <!-- 左侧功能面板 -->
      <div class="left-panel">
        <!-- 搜索框 -->
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索地点..."
            size="large"
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button :icon="Search" @click="handleSearch" />
            </template>
          </el-input>
        </div>
        
        <!-- 快捷分类 -->
        <div class="quick-tags">
          <div class="tags-title">快捷分类</div>
          <div class="tags-list">
            <el-tag
              v-for="tag in tags"
              :key="tag.id"
              :color="tag.color"
              class="tag-item"
              effect="dark"
              @click="filterByTag(tag)"
            >
              {{ tag.name }}
            </el-tag>
          </div>
        </div>
        
        <!-- 照片列表 -->
        <div class="photo-list">
          <div class="list-title">
            照片列表
            <el-badge :value="photoStore.publicPhotos.length" type="primary" />
          </div>
          <div class="list-content">
            <div
              v-for="photo in photoStore.publicPhotos"
              :key="photo.id"
              class="photo-item"
              @click="focusPhoto(photo)"
            >
              <img :src="photo.thumbnailUrl || photo.url" :alt="photo.name" />
              <div class="photo-info">
                <span class="photo-name">{{ photo.name }}</span>
                <span class="photo-location">{{ photo.location || '未知位置' }}</span>
              </div>
            </div>
            <el-empty v-if="photoStore.publicPhotos.length === 0" description="暂无照片" />
          </div>
        </div>
      </div>
      
      <!-- 底部照片轮播 -->
      <div class="bottom-carousel" v-if="photoStore.publicPhotos.length > 0">
        <div class="carousel-title">照片浏览</div>
        <el-carousel :interval="5000" type="card" height="150px" indicator-position="none">
          <el-carousel-item v-for="photo in photoStore.publicPhotos" :key="photo.id">
            <div class="carousel-item" @click="showPhotoDetail(photo)">
              <img :src="photo.thumbnailUrl || photo.url" :alt="photo.name" />
              <div class="carousel-info">{{ photo.name }}</div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>
    </div>

    <!-- 照片详情弹窗 -->
    <el-dialog v-model="photoDialogVisible" title="照片详情" width="600px" destroy-on-close>
      <div class="photo-detail" v-if="selectedPhoto">
        <div class="detail-image">
          <img :src="selectedPhoto.url" :alt="selectedPhoto.name" />
        </div>
        <div class="detail-info">
          <h3>{{ selectedPhoto.name }}</h3>
          <p class="description">{{ selectedPhoto.description || '暂无描述' }}</p>
          <div class="meta">
            <span><el-icon><Location /></el-icon> {{ selectedPhoto.location || '未知位置' }}</span>
            <span><el-icon><User /></el-icon> {{ selectedPhoto.nickname || '匿名用户' }}</span>
            <span><el-icon><Calendar /></el-icon> {{ formatDate(selectedPhoto.createTime) }}</span>
          </div>
          <div class="location-info" v-if="selectedPhoto.longitude && selectedPhoto.latitude">
            <p>经度: {{ selectedPhoto.longitude }}, 纬度: {{ selectedPhoto.latitude }}</p>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { usePhotoStore } from '@/store/photo'
import { tagApi } from '@/api/tag'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'
import 'leaflet.markercluster'
import 'leaflet.markercluster/dist/MarkerCluster.css'
import 'leaflet.markercluster/dist/MarkerCluster.Default.css'
import { Search, MapLocation, Picture, User as UserIcon, Location, Calendar } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const router = useRouter()
const userStore = useUserStore()
const photoStore = usePhotoStore()

// 状态
const mapRef = ref(null)
const map = ref(null)
const markers = ref(null)
const searchKeyword = ref('')
const tags = ref([])
const photoDialogVisible = ref(false)
const selectedPhoto = ref(null)

// 计算属性
const isLoggedIn = computed(() => userStore.isLoggedIn)

// 高德地图配置
const AMAP_KEY = 'ff5138a6fc4f8958f2a7d24514d02854'

// 初始化地图
const initMap = () => {
  // 创建地图实例 (默认中心点设为中国)
  map.value = L.map(mapRef.value, {
    center: [35.8617, 104.1954], // 中国中心
    zoom: 5,
    zoomControl: true
  })
  
  // 高德地图URL配置
  const vecUrl = `http://webst0{s}.is.autonavi.com/appmaptile?style=6&x={x}&y={y}&z={z}&key=${AMAP_KEY}`
  const cvaUrl = `http://webst0{s}.is.autonavi.com/appmaptile?style=8&x={x}&y={y}&z={z}&key=${AMAP_KEY}`
  
  // 添加底图 - 使用高德地图
  L.tileLayer(vecUrl, {
    subdomains: [1, 2, 3, 4],
    attribution: '&copy; 高德地图'
  }).addTo(map.value)
  
  // 添加标注图层
  L.tileLayer(cvaUrl, {
    subdomains: [1, 2, 3, 4],
    attribution: '&copy; 高德地图'
  }).addTo(map.value)
  
  // 初始化 marker cluster group
  markers.value = L.markerClusterGroup({
    maxClusterRadius: 80,
    spiderfyOnMaxZoom: true,
    showCoverageOnHover: false,
    zoomToBoundsOnClick: true
  })
  
  map.value.addLayer(markers.value)
  
  // 点击地图获取位置
  map.value.on('click', (e) => {
    console.log('点击位置:', e.latlng)
  })
}

// 加载照片标记
const loadPhotoMarkers = (photos) => {
  markers.value.clearLayers()
  
  photos.forEach(photo => {
    if (photo.longitude && photo.latitude) {
      // 创建自定义图标
      const icon = L.divIcon({
        html: `<div class="custom-marker">
          <img src="${photo.thumbnailUrl || photo.url}" alt="${photo.name}" />
        </div>`,
        className: 'custom-marker-wrapper',
        iconSize: [40, 40],
        iconAnchor: [20, 20]
      })
      
      const marker = L.marker([photo.latitude, photo.longitude], { icon })
      
      // 绑定弹窗
      marker.bindPopup(`
        <div class="marker-popup">
          <img src="${photo.url}" alt="${photo.name}" />
          <h4>${photo.name}</h4>
          <p>${photo.location || ''}</p>
        </div>
      `)
      
      // 点击事件
      marker.on('click', () => {
        selectedPhoto.value = photo
        photoDialogVisible.value = true
      })
      
      markers.value.addLayer(marker)
    }
  })
}

// 获取标签列表
const loadTags = async () => {
  try {
    const res = await tagApi.getAllTags()
    tags.value = res.data || []
  } catch (error) {
    console.error('获取标签失败', error)
  }
}

// 获取公开照片
const loadPublicPhotos = async () => {
  try {
    const res = await photoStore.fetchPublicPhotos({ page: 1, size: 100 })
    if (res && res.data) {
      loadPhotoMarkers(photoStore.publicPhotos)
    }
  } catch (error) {
    console.error('获取照片失败', error)
  }
}

// 按标签筛选
const filterByTag = async (tag) => {
  try {
    const res = await photoStore.fetchPhotosByTag(tag.id, { page: 1, size: 100 })
    if (res && res.data) {
      loadPhotoMarkers(photoStore.publicPhotos)
    }
  } catch (error) {
    console.error('筛选失败', error)
  }
}

// 搜索地点
const handleSearch = () => {
  if (!searchKeyword.value) return
  // TODO: 调用地图搜索API
  console.log('搜索:', searchKeyword.value)
}

// 聚焦照片
const focusPhoto = (photo) => {
  if (photo.latitude && photo.longitude) {
    map.value.setView([photo.latitude, photo.longitude], 15)
  }
}

// 显示照片详情
const showPhotoDetail = (photo) => {
  selectedPhoto.value = photo
  photoDialogVisible.value = true
}

// 格式化日期
const formatDate = (date) => {
  return date ? dayjs(date).format('YYYY-MM-DD') : ''
}

// 用户菜单命令
const handleUserCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'album':
      router.push('/album')
      break
    case 'upload':
      router.push('/photo/upload')
      break
    case 'logout':
      userStore.logout()
      router.push('/login')
      break
  }
}

onMounted(() => {
  initMap()
  loadTags()
  loadPublicPhotos()
  if (userStore.isLoggedIn) {
    userStore.fetchUserInfo()
  }
})

onUnmounted(() => {
  if (map.value) {
    map.value.remove()
  }
})
</script>

<style scoped>
.map-home {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.map-navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 60px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  z-index: 1000;
}

.nav-left {
  display: flex;
  align-items: center;
}

.logo {
  font-size: 22px;
  font-weight: bold;
  color: #409eff;
  cursor: pointer;
  margin-right: 40px;
}

.nav-tabs {
  display: flex;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 0 20px;
  height: 60px;
  color: #606266;
  text-decoration: none;
  transition: all 0.3s;
  
  &:hover, &.active {
    color: #409eff;
    background: #f5f7fa;
  }
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  
  .username {
    font-size: 14px;
    color: #303133;
  }
}

.map-wrapper {
  flex: 1;
  position: relative;
  margin-top: 60px;
}

.map-container {
  width: 100%;
  height: 100%;
}

.left-panel {
  position: absolute;
  top: 20px;
  left: 20px;
  width: 300px;
  max-height: calc(100% - 40px);
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
  z-index: 500;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.search-box {
  padding: 15px;
  border-bottom: 1px solid #eee;
}

.quick-tags {
  padding: 15px;
  border-bottom: 1px solid #eee;
  
  .tags-title {
    font-size: 14px;
    color: #909399;
    margin-bottom: 10px;
  }
  
  .tags-list {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
  }
  
  .tag-item {
    cursor: pointer;
    font-size: 12px;
    padding: 4px 10px;
  }
}

.photo-list {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
  
  .list-title {
    font-size: 14px;
    color: #909399;
    margin-bottom: 10px;
    display: flex;
    align-items: center;
    gap: 8px;
  }
  
  .list-content {
    max-height: 300px;
    overflow-y: auto;
  }
  
  .photo-item {
    display: flex;
    gap: 10px;
    padding: 8px;
    border-radius: 4px;
    cursor: pointer;
    transition: background 0.3s;
    
    &:hover {
      background: #f5f7fa;
    }
    
    img {
      width: 50px;
      height: 50px;
      object-fit: cover;
      border-radius: 4px;
    }
    
    .photo-info {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: center;
      
      .photo-name {
        font-size: 14px;
        color: #303133;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
      
      .photo-location {
        font-size: 12px;
        color: #909399;
      }
    }
  }
}

.bottom-carousel {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  width: 80%;
  background: #fff;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
  z-index: 500;
  
  .carousel-title {
    font-size: 14px;
    color: #909399;
    margin-bottom: 10px;
  }
  
  .carousel-item {
    width: 100%;
    height: 100%;
    cursor: pointer;
    border-radius: 8px;
    overflow: hidden;
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
    
    .carousel-info {
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      padding: 8px;
      background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
      color: #fff;
      font-size: 12px;
      text-align: center;
    }
  }
}

.photo-detail {
  .detail-image {
    width: 100%;
    height: 300px;
    border-radius: 8px;
    overflow: hidden;
    margin-bottom: 20px;
    
    img {
      width: 100%;
      height: 100%;
      object-fit: contain;
    }
  }
  
  .detail-info {
    h3 {
      margin: 0 0 10px;
    }
    
    .description {
      color: #606266;
      margin-bottom: 15px;
    }
    
    .meta {
      display: flex;
      flex-wrap: wrap;
      gap: 15px;
      color: #909399;
      font-size: 14px;
      
      span {
        display: flex;
        align-items: center;
        gap: 4px;
      }
    }
    
    .location-info {
      margin-top: 10px;
      padding: 10px;
      background: #f5f7fa;
      border-radius: 4px;
      font-size: 13px;
      color: #606266;
    }
  }
}

:deep(.custom-marker-wrapper) {
  background: transparent !important;
  border: none !important;
}

:deep(.custom-marker) {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

:deep(.marker-popup) {
  img {
    width: 200px;
    height: 150px;
    object-fit: cover;
    border-radius: 4px;
  }
  
  h4 {
    margin: 10px 0 5px;
    font-size: 14px;
  }
  
  p {
    margin: 0;
    font-size: 12px;
    color: #909399;
  }
}
</style>
