<template>
  <div class="upload-page">
    <!-- 顶部导航 -->
    <div class="page-navbar">
      <div class="nav-left">
        <span class="logo" @click="$router.push('/map')">NeekoMap</span>
      </div>
    </div>

    <!-- 主内容 -->
    <div class="page-content">
      <el-card class="upload-card">
        <template #header>
          <div class="card-header">
            <span>上传照片</span>
            <el-button text @click="$router.back()">
              <el-icon><ArrowLeft /></el-icon>
              返回
            </el-button>
          </div>
        </template>
        
        <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
          <!-- 照片上传 -->
          <el-form-item label="选择照片" prop="file">
            <el-upload
              ref="uploadRef"
              class="photo-uploader"
              drag
              :auto-upload="false"
              :limit="1"
              :on-change="handleFileChange"
              :on-remove="handleFileRemove"
              accept="image/*"
            >
              <div v-if="!form.previewUrl" class="upload-placeholder">
                <el-icon :size="48"><Upload /></el-icon>
                <span>点击或拖拽上传照片</span>
                <span class="tip">支持 JPG、PNG、GIF、WebP 格式</span>
              </div>
              <div v-else class="upload-preview">
                <img :src="form.previewUrl" alt="预览" />
              </div>
            </el-upload>
          </el-form-item>
          
          <el-form-item label="照片名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入照片名称" />
          </el-form-item>
          
          <el-form-item label="照片描述" prop="description">
            <el-input 
              v-model="form.description" 
              type="textarea" 
              :rows="3"
              placeholder="请输入照片描述（选填）"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>
          
          <el-form-item label="所属相册" prop="albumId">
            <el-select v-model="form.albumId" placeholder="请选择相册（选填）" clearable>
              <el-option 
                v-for="album in albums" 
                :key="album.id" 
                :label="album.name" 
                :value="album.id" 
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="照片标签">
            <div class="tag-selector">
              <el-checkbox-group v-model="form.tagIds">
                <el-checkbox 
                  v-for="tag in tags" 
                  :key="tag.id" 
                  :label="tag.id"
                  :style="{ '--tag-color': tag.color }"
                >
                  {{ tag.name }}
                </el-checkbox>
              </el-checkbox-group>
            </div>
          </el-form-item>
          
          <el-form-item label="隐私设置" prop="privacy">
            <el-radio-group v-model="form.privacy">
              <el-radio :label="0">仅自己可见</el-radio>
              <el-radio :label="1">好友可见</el-radio>
              <el-radio :label="2">完全公开</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <!-- 位置定位 -->
          <el-form-item label="拍摄位置">
            <div class="location-picker">
              <el-input 
                v-model="form.location" 
                placeholder="输入位置描述（选填）"
                style="margin-bottom: 15px"
              />
              <div class="map-picker">
                <div id="location-map" ref="mapRef" class="mini-map"></div>
                <div class="coordinate-info" v-if="form.longitude && form.latitude">
                  <p>经度: {{ form.longitude }}</p>
                  <p>纬度: {{ form.latitude }}</p>
                </div>
              </div>
              <el-button type="primary" plain @click="getCurrentLocation">
                <el-icon><Location /></el-icon>
                获取当前位置
              </el-button>
            </div>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" size="large" :loading="uploading" @click="handleUpload">
              上传照片
            </el-button>
            <el-button size="large" @click="$router.back()">取消</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { photoApi } from '@/api/photo'
import { albumApi } from '@/api/album'
import { tagApi } from '@/api/tag'
import { ElMessage } from 'element-plus'
import { Upload, Location, ArrowLeft } from '@element-plus/icons-vue'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'

const router = useRouter()
const userStore = useUserStore()

// 状态
const formRef = ref(null)
const uploadRef = ref(null)
const mapRef = ref(null)
const uploading = ref(false)
const tags = ref([])
const albums = ref([])
const locationMap = ref(null)
const locationMarker = ref(null)

const form = reactive({
  file: null,
  previewUrl: '',
  name: '',
  description: '',
  albumId: null,
  tagIds: [],
  privacy: 2,
  location: '',
  longitude: null,
  latitude: null
})

const rules = {
  file: [{ required: true, message: '请选择照片', trigger: 'change' }],
  name: [{ required: true, message: '请输入照片名称', trigger: 'blur' }]
}

// 初始化地图
const initMap = () => {
  locationMap.value = L.map(mapRef.value, {
    center: [35.8617, 104.1954],
    zoom: 5
  })
  
  L.tileLayer('https://t{s}.tianditu.gov.cn/DataServer?T=vec_w&X={x}&Y={y}&L={z}', {
    subdomains: [0, 1, 2, 3, 4, 5, 6, 7],
    attribution: '&copy; 天地图'
  }).addTo(locationMap.value)
  
  // 点击地图选择位置
  locationMap.value.on('click', (e) => {
    form.longitude = e.latlng.lng
    form.latitude = e.latlng.lat
    
    if (locationMarker.value) {
      locationMarker.value.setLatLng(e.latlng)
    } else {
      locationMarker.value = L.marker(e.latlng).addTo(locationMap.value)
    }
  })
}

// 文件变化
const handleFileChange = (file) => {
  form.file = file.raw
  form.previewUrl = URL.createObjectURL(file.raw)
  if (!form.name) {
    form.name = file.name.replace(/\.[^.]+$/, '')
  }
}

const handleFileRemove = () => {
  form.file = null
  form.previewUrl = ''
}

// 加载标签
const loadTags = async () => {
  try {
    const res = await tagApi.getAllTags()
    tags.value = res.data || []
  } catch (error) {
    console.error('获取标签失败', error)
  }
}

// 加载相册
const loadAlbums = async () => {
  if (!userStore.userInfo?.id) return
  try {
    const res = await albumApi.getUserAlbums(userStore.userInfo.id, { page: 1, size: 100 })
    albums.value = res.data?.records || []
  } catch (error) {
    console.error('获取相册失败', error)
  }
}

// 获取当前位置
const getCurrentLocation = () => {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        const lat = position.coords.latitude
        const lng = position.coords.longitude
        
        form.longitude = lng
        form.latitude = lat
        
        locationMap.value.setView([lat, lng], 15)
        
        if (locationMarker.value) {
          locationMarker.value.setLatLng([lat, lng])
        } else {
          locationMarker.value = L.marker([lat, lng]).addTo(locationMap.value)
        }
      },
      (error) => {
        ElMessage.error('获取位置失败: ' + error.message)
      }
    )
  } else {
    ElMessage.error('浏览器不支持地理定位')
  }
}

// 上传照片
const handleUpload = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid || !form.file) return
  
  uploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', form.file)
    formData.append('name', form.name)
    formData.append('description', form.description || '')
    if (form.albumId) formData.append('albumId', form.albumId)
    formData.append('tagIds', form.tagIds.join(','))
    formData.append('privacy', form.privacy)
    formData.append('location', form.location || '')
    
    if (form.longitude && form.latitude) {
      formData.append('longitude', form.longitude)
      formData.append('latitude', form.latitude)
    }
    
    await photoApi.uploadPhoto(formData)
    ElMessage.success('上传成功')
    router.push('/album')
  } catch (error) {
    console.error('上传失败', error)
  } finally {
    uploading.value = false
  }
}

onMounted(() => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  userStore.fetchUserInfo()
  loadTags()
  loadAlbums()
  
  setTimeout(() => {
    initMap()
  }, 100)
})

onUnmounted(() => {
  if (locationMap.value) {
    locationMap.value.remove()
  }
})
</script>

<style scoped>
.upload-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.page-navbar {
  height: 60px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.logo {
  font-size: 22px;
  font-weight: bold;
  color: #409eff;
  cursor: pointer;
}

.page-content {
  max-width: 800px;
  margin: 30px auto;
  padding: 0 20px;
}

.upload-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}

.photo-uploader {
  width: 100%;
  
  :deep(.el-upload) {
    width: 100%;
  }
  
  :deep(.el-upload-dragger) {
    width: 100%;
    height: 300px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  color: #909399;
  
  .tip {
    font-size: 12px;
    color: #c0c4cc;
  }
}

.upload-preview {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  
  img {
    max-width: 100%;
    max-height: 100%;
    object-fit: contain;
  }
}

.tag-selector {
  :deep(.el-checkbox) {
    margin-right: 15px;
    margin-bottom: 10px;
    padding: 5px 10px;
    border-radius: 4px;
    border: 1px solid #dcdfe6;
    
    &.is-checked {
      background: var(--tag-color, #409eff);
      border-color: var(--tag-color, #409eff);
      color: #fff;
    }
  }
}

.location-picker {
  width: 100%;
}

.map-picker {
  height: 300px;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 15px;
  
  .mini-map {
    width: 100%;
    height: 100%;
  }
  
  .coordinate-info {
    position: absolute;
    top: 10px;
    right: 10px;
    background: rgba(255, 255, 255, 0.9);
    padding: 10px;
    border-radius: 4px;
    font-size: 12px;
    
    p {
      margin: 0 0 5px;
      
      &:last-child {
        margin-bottom: 0;
      }
    }
  }
}
</style>
