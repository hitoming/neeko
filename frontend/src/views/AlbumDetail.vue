<template>
  <div class="album-detail-container">
    <el-page-header @back="goBack" title="返回">
      <template #content>
        <span class="album-title">{{ album.title }}</span>
      </template>
    </el-page-header>
    
    <el-card class="album-info" style="margin: 20px 0;">
      <el-row :gutter="20">
        <el-col :span="16">
          <div class="info-content">
            <p class="description">{{ album.description || '暂无描述' }}</p>
            <div class="meta">
              <span><el-icon><Calendar /></el-icon> {{ formatDate(album.createTime) }}</span>
              <span><el-icon><Picture /></el-icon> {{ photos.length }} 张照片</span>
              <span v-if="album.isPublic === 2">
                <el-tag type="success" size="small">公开</el-tag>
              </span>
              <span v-else-if="album.isPublic === 1">
                <el-tag type="warning" size="small">好友可见</el-tag>
              </span>
              <span v-else>
                <el-tag type="info" size="small">私密</el-tag>
              </span>
            </div>
          </div>
        </el-col>
        <el-col :span="8" class="action-bar">
          <el-button type="primary" @click="handleAddPhoto">
            <el-icon><Plus /></el-icon>添加照片
          </el-button>
          <el-button @click="showEditDialog = true">
            <el-icon><Edit /></el-icon>编辑
          </el-button>
        </el-col>
      </el-row>
    </el-card>
    
    <div class="photo-grid" v-if="photos.length > 0">
      <div 
        v-for="photo in photos" 
        :key="photo.id" 
        class="photo-item"
        @click="handlePhotoClick(photo)"
      >
        <el-image 
          :src="photo.thumbnailUrl || photo.url" 
          fit="cover"
          class="photo-image"
          :preview-src-list="[photo.url]"
        />
        <div class="photo-overlay">
          <span class="photo-location" v-if="photo.locationName">
            <el-icon><Location /></el-icon> {{ photo.locationName }}
          </span>
          <div class="photo-actions">
            <el-button 
              type="primary" 
              text 
              circle 
              @click.stop="handleEditPhoto(photo)"
            >
              <el-icon><Edit /></el-icon>
            </el-button>
            <el-button 
              type="danger" 
              text 
              circle 
              @click.stop="handleDeletePhoto(photo.id)"
            >
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
        </div>
      </div>
    </div>
    
    <el-empty v-else description="相册中暂无照片">
      <el-button type="primary" @click="handleAddPhoto">上传照片</el-button>
    </el-empty>
    
    <!-- 编辑相册对话框 -->
    <el-dialog v-model="showEditDialog" title="编辑相册" width="500px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="相册名称">
          <el-input v-model="editForm.title" placeholder="请输入相册名称" />
        </el-form-item>
        <el-form-item label="相册描述">
          <el-input 
            v-model="editForm.description" 
            type="textarea" 
            :rows="3"
            placeholder="请输入相册描述" 
          />
        </el-form-item>
        <el-form-item label="隐私设置">
          <el-select v-model="editForm.isPublic">
            <el-option :value="0" label="私密" />
            <el-option :value="1" label="好友可见" />
            <el-option :value="2" label="公开" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSaveEdit">保存</el-button>
      </template>
    </el-dialog>
    
    <!-- 编辑照片对话框 -->
    <el-dialog v-model="showPhotoDialog" title="编辑照片" width="500px">
      <el-form :model="photoForm" label-width="80px">
        <el-form-item label="照片描述">
          <el-input 
            v-model="photoForm.description" 
            type="textarea" 
            :rows="2"
            placeholder="请输入照片描述" 
          />
        </el-form-item>
        <el-form-item label="拍摄地点">
          <el-input v-model="photoForm.locationName" placeholder="请输入地点名称" />
        </el-form-item>
        <el-form-item label="标签">
          <el-select v-model="photoForm.tags" multiple placeholder="选择标签" style="width: 100%">
            <el-option 
              v-for="tag in availableTags" 
              :key="tag.id" 
              :label="tag.name" 
              :value="tag.id" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="隐私设置">
          <el-select v-model="photoForm.isPublic">
            <el-option :value="0" label="私密" />
            <el-option :value="1" label="好友可见" />
            <el-option :value="2" label="公开" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPhotoDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSavePhoto">保存</el-button>
      </template>
    </el-dialog>
    
    <!-- 照片地图对话框 -->
    <el-dialog v-model="showMapDialog" title="照片位置" width="800px" height="600px">
      <div id="photo-map" class="photo-map"></div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Calendar, Picture, Plus, Edit, Delete, Location } from '@element-plus/icons-vue'
import { getAlbumDetail, updateAlbum, deleteAlbum, getPhotoList, updatePhoto, deletePhoto, getTagList } from '@/api/request'

const router = useRouter()
const route = useRoute()

const albumId = ref(route.params.id)
const album = ref({
  id: '',
  title: '',
  description: '',
  isPublic: 0,
  createTime: ''
})
const photos = ref([])
const availableTags = ref([])

const showEditDialog = ref(false)
const showPhotoDialog = ref(false)
const showMapDialog = ref(false)

const editForm = reactive({
  title: '',
  description: '',
  isPublic: 0
})

const photoForm = reactive({
  id: '',
  description: '',
  locationName: '',
  tags: [],
  isPublic: 0
})

let photoMap = null

onMounted(() => {
  loadAlbumDetail()
  loadPhotos()
  loadTags()
})

onUnmounted(() => {
  if (photoMap) {
    photoMap.remove()
  }
})

const loadAlbumDetail = async () => {
  try {
    const res = await getAlbumDetail(albumId.value)
    if (res.code === 200) {
      album.value = res.data
      Object.assign(editForm, {
        title: res.data.title,
        description: res.data.description,
        isPublic: res.data.isPublic
      })
    }
  } catch (error) {
    console.error('加载相册详情失败:', error)
  }
}

const loadPhotos = async () => {
  try {
    const res = await getPhotoList({ albumId: albumId.value })
    if (res.code === 200) {
      photos.value = res.data.records || []
    }
  } catch (error) {
    console.error('加载照片列表失败:', error)
  }
}

const loadTags = async () => {
  try {
    const res = await getTagList()
    if (res.code === 200) {
      availableTags.value = res.data
    }
  } catch (error) {
    console.error('加载标签失败:', error)
  }
}

const goBack = () => {
  router.back()
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN')
}

const handleAddPhoto = () => {
  router.push({ name: 'PhotoUpload', query: { albumId: albumId.value } })
}

const handlePhotoClick = (photo) => {
  if (photo.longitude && photo.latitude) {
    showMapDialog.value = true
    setTimeout(() => {
      initPhotoMap(photo)
    }, 100)
  }
}

const handleEditPhoto = (photo) => {
  Object.assign(photoForm, {
    id: photo.id,
    description: photo.description || '',
    locationName: photo.locationName || '',
    tags: photo.tags || [],
    isPublic: photo.isPublic || 0
  })
  showPhotoDialog.value = true
}

const handleDeletePhoto = async (photoId) => {
  try {
    await ElMessageBox.confirm('确定要删除这张照片吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await deletePhoto(photoId)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      photos.value = photos.value.filter(p => p.id !== photoId)
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSaveEdit = async () => {
  try {
    const res = await updateAlbum(albumId.value, editForm)
    if (res.code === 200) {
      ElMessage.success('修改成功')
      album.value = { ...album.value, ...editForm }
      showEditDialog.value = false
    }
  } catch (error) {
    ElMessage.error('修改失败')
  }
}

const handleSavePhoto = async () => {
  try {
    const res = await updatePhoto(photoForm)
    if (res.code === 200) {
      ElMessage.success('保存成功')
      const index = photos.value.findIndex(p => p.id === photoForm.id)
      if (index !== -1) {
        photos.value[index] = { ...photos.value[index], ...photoForm }
      }
      showPhotoDialog.value = false
    }
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const initPhotoMap = (photo) => {
  if (photoMap) {
    photoMap.remove()
  }
  
  // 动态加载Leaflet
  const L = window.L
  if (!L) {
    console.error('Leaflet未加载')
    return
  }
  
  photoMap = L.map('photo-map').setView([photo.latitude, photo.longitude], 15)
  
  L.tileLayer('https://t{s}.tianditu.gov.cn/DataServer?T=vec_w&X={x}&Y={y}&L={z}', {
    subdomains: '0123456789',
    attribution: '© 天地图'
  }).addTo(photoMap)
  
  L.marker([photo.latitude, photo.longitude]).addTo(photoMap)
    .bindPopup(photo.locationName || '照片位置')
    .openPopup()
}
</script>

<style scoped>
.album-detail-container {
  padding: 20px;
}

.album-title {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
}

.info-content {
  padding: 10px 0;
}

.description {
  color: #606266;
  margin-bottom: 15px;
  line-height: 1.6;
}

.meta {
  display: flex;
  gap: 20px;
  color: #909399;
  font-size: 14px;
}

.meta span {
  display: flex;
  align-items: center;
  gap: 5px;
}

.action-bar {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  align-items: center;
}

.photo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 15px;
  margin-top: 20px;
}

.photo-item {
  position: relative;
  aspect-ratio: 1;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.photo-image {
  width: 100%;
  height: 100%;
}

.photo-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 10px;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
  color: white;
  opacity: 0;
  transition: opacity 0.3s;
}

.photo-item:hover .photo-overlay {
  opacity: 1;
}

.photo-location {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
}

.photo-actions {
  display: flex;
  gap: 5px;
  margin-top: 5px;
}

.photo-map {
  width: 100%;
  height: 500px;
}
</style>
