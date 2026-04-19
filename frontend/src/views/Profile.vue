<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人中心</span>
        </div>
      </template>
      
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="avatar-section">
            <el-avatar :size="120" :src="userStore.avatar">
              <el-icon :size="60"><UserFilled /></el-icon>
            </el-avatar>
            <el-button type="primary" text @click="showAvatarDialog = true">更换头像</el-button>
          </div>
        </el-col>
        
        <el-col :span="16">
          <el-form :model="profileForm" label-width="100px" class="profile-form">
            <el-form-item label="用户名">
              <el-input v-model="profileForm.username" disabled />
            </el-form-item>
            <el-form-item label="昵称">
              <el-input v-model="profileForm.nickname" placeholder="请输入昵称" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item label="个人简介">
              <el-input 
                v-model="profileForm.bio" 
                type="textarea" 
                :rows="3"
                placeholder="介绍一下自己吧~" 
              />
            </el-form-item>
            <el-form-item label="所在城市">
              <el-input v-model="profileForm.city" placeholder="请输入所在城市" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUpdateProfile">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>
    </el-card>
    
    <el-card class="stats-card" style="margin-top: 20px;">
      <template #header>
        <span>我的足迹</span>
      </template>
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value">{{ stats.photoCount }}</div>
            <div class="stat-label">照片</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value">{{ stats.albumCount }}</div>
            <div class="stat-label">相册</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value">{{ stats.friendCount }}</div>
            <div class="stat-label">好友</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value">{{ stats.visitCount }}</div>
            <div class="stat-label">访问</div>
          </div>
        </el-col>
      </el-row>
    </el-card>
    
    <el-card class="location-card" style="margin-top: 20px;">
      <template #header>
        <span>常去地点</span>
        <el-button type="primary" text @click="showAddLocationDialog = true">
          <el-icon><Plus /></el-icon>添加
        </el-button>
      </template>
      <div class="location-list" v-if="locations.length > 0">
        <el-tag 
          v-for="loc in locations" 
          :key="loc.id" 
          closable 
          @close="handleDeleteLocation(loc.id)"
          class="location-tag"
        >
          {{ loc.name }} ({{ loc.count }}次)
        </el-tag>
      </div>
      <el-empty v-else description="暂无常去地点记录" />
    </el-card>
    
    <el-dialog v-model="showAvatarDialog" title="更换头像" width="400px">
      <el-upload
        class="avatar-uploader"
        :http-request="handleAvatarUpload"
        :show-file-list="false"
        accept="image/*"
      >
        <img v-if="avatarPreview" :src="avatarPreview" class="avatar-preview" />
        <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
      </el-upload>
    </el-dialog>
    
    <el-dialog v-model="showAddLocationDialog" title="添加常去地点" width="400px">
      <el-form :model="locationForm" label-width="80px">
        <el-form-item label="地点名称">
          <el-input v-model="locationForm.name" placeholder="如：北京天安门" />
        </el-form-item>
        <el-form-item label="坐标">
          <el-input v-model="locationForm.coords" placeholder="经度,纬度" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddLocationDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAddLocation">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { UserFilled, Plus } from '@element-plus/icons-vue'
import { getUserStats, updateUserProfile, uploadAvatar } from '@/api/request'

const userStore = useUserStore()

const profileForm = reactive({
  username: '',
  nickname: '',
  email: '',
  bio: '',
  city: ''
})

const stats = ref({
  photoCount: 0,
  albumCount: 0,
  friendCount: 0,
  visitCount: 0
})

const locations = ref([])
const showAvatarDialog = ref(false)
const showAddLocationDialog = ref(false)
const avatarPreview = ref('')
const locationForm = reactive({
  name: '',
  coords: ''
})

onMounted(() => {
  loadProfile()
  loadStats()
  loadFavoriteLocations()
})

const loadProfile = () => {
  Object.assign(profileForm, {
    username: userStore.username,
    nickname: userStore.nickname || '',
    email: userStore.email || '',
    bio: userStore.bio || '',
    city: userStore.city || ''
  })
}

const loadStats = async () => {
  try {
    const res = await getUserStats()
    if (res.code === 200) {
      stats.value = res.data
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const loadFavoriteLocations = async () => {
  // 模拟数据
  locations.value = [
    { id: 1, name: '武汉大学', count: 15 },
    { id: 2, name: '北京故宫', count: 8 }
  ]
}

const handleUpdateProfile = async () => {
  try {
    const res = await updateUserProfile(profileForm)
    if (res.code === 200) {
      ElMessage.success('修改成功')
      userStore.updateUserInfo({
        nickname: profileForm.nickname,
        email: profileForm.email,
        bio: profileForm.bio,
        city: profileForm.city
      })
    }
  } catch (error) {
    ElMessage.error('修改失败')
  }
}

const handleAvatarUpload = async (options) => {
  const { file } = options
  const formData = new FormData()
  formData.append('file', file)
  
  try {
    const res = await uploadAvatar(formData)
    if (res.code === 200) {
      userStore.updateAvatar(res.data.url)
      avatarPreview.value = ''
      showAvatarDialog.value = false
      ElMessage.success('头像更换成功')
    }
  } catch (error) {
    ElMessage.error('上传失败')
  }
}

const handleAddLocation = async () => {
  if (!locationForm.name) {
    ElMessage.warning('请输入地点名称')
    return
  }
  
  locations.value.push({
    id: Date.now(),
    name: locationForm.name,
    count: 1
  })
  
  showAddLocationDialog.value = false
  locationForm.name = ''
  locationForm.coords = ''
  ElMessage.success('添加成功')
}

const handleDeleteLocation = (id) => {
  locations.value = locations.value.filter(loc => loc.id !== id)
}
</script>

<style scoped>
.profile-container {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.profile-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.profile-form {
  padding: 20px 0;
}

.stat-item {
  text-align: center;
  padding: 20px;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
}

.stat-label {
  margin-top: 8px;
  color: #909399;
}

.location-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.location-tag {
  font-size: 14px;
  padding: 8px 15px;
}

.avatar-uploader {
  display: flex;
  justify-content: center;
}

.avatar-preview {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  object-fit: cover;
}
</style>
