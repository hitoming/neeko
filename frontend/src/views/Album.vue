<template>
  <div class="album-page">
    <!-- 顶部导航 -->
    <div class="page-navbar">
      <div class="nav-left">
        <span class="logo" @click="$router.push('/map')">NeekoMap</span>
        <div class="nav-tabs">
          <router-link to="/map" class="nav-item">
            <el-icon><MapLocation /></el-icon>
            地图首页
          </router-link>
          <router-link to="/album" class="nav-item active">
            <el-icon><Picture /></el-icon>
            我的相册
          </router-link>
          <router-link to="/friends" class="nav-item">
            <el-icon><User /></el-icon>
            好友
          </router-link>
        </div>
      </div>
      
      <div class="nav-right">
        <el-button type="primary" @click="showCreateDialog">
          <el-icon><Plus /></el-icon>
          创建相册
        </el-button>
        <el-button type="primary" @click="$router.push('/photo/upload')">
          <el-icon><Upload /></el-icon>
          上传照片
        </el-button>
      </div>
    </div>

    <!-- 主内容 -->
    <div class="page-content">
      <div class="content-header">
        <h2>我的相册</h2>
        <span class="album-count">共 {{ albums.length }} 个相册</span>
      </div>
      
      <!-- 相册列表 -->
      <div class="album-grid">
        <el-card 
          v-for="album in albums" 
          :key="album.id" 
          class="album-card"
          shadow="hover"
          @click="goToAlbumDetail(album.id)"
        >
          <div class="album-cover">
            <img :src="album.coverUrl || defaultCover" alt="封面" />
            <div class="album-overlay">
              <span>{{ album.photoCount || 0 }} 张照片</span>
            </div>
          </div>
          <div class="album-info">
            <h4>{{ album.name }}</h4>
            <p>{{ album.description || '暂无描述' }}</p>
            <div class="album-meta">
              <el-tag size="small" :type="getPrivacyType(album.privacy)">
                {{ getPrivacyText(album.privacy) }}
              </el-tag>
              <span class="date">{{ formatDate(album.createTime) }}</span>
            </div>
          </div>
          <div class="album-actions" @click.stop>
            <el-dropdown trigger="click">
              <el-button type="text" :icon="More" />
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="editAlbum(album)">编辑</el-dropdown-item>
                  <el-dropdown-item @click="deleteAlbum(album.id)" divided>删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-card>
        
        <!-- 创建相册卡片 -->
        <el-card class="album-card add-card" @click="showCreateDialog">
          <div class="add-content">
            <el-icon :size="48"><Plus /></el-icon>
            <span>创建新相册</span>
          </div>
        </el-card>
      </div>
      
      <el-empty v-if="albums.length === 0 && !loading" description="暂无相册，点击创建第一个相册" />
    </div>

    <!-- 创建/编辑相册弹窗 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="isEdit ? '编辑相册' : '创建相册'" 
      width="500px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="相册名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入相册名称" />
        </el-form-item>
        <el-form-item label="相册描述" prop="description">
          <el-input 
            v-model="form.description" 
            type="textarea" 
            :rows="3"
            placeholder="请输入相册描述"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="封面图片" prop="coverUrl">
          <el-input v-model="form.coverUrl" placeholder="请输入封面图片URL" />
        </el-form-item>
        <el-form-item label="隐私设置" prop="privacy">
          <el-radio-group v-model="form.privacy">
            <el-radio :label="0">仅自己可见</el-radio>
            <el-radio :label="1">好友可见</el-radio>
            <el-radio :label="2">完全公开</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { albumApi } from '@/api/album'
import { ElMessage, ElMessageBox } from 'element-plus'
import { MapLocation, Picture, User, Plus, Upload, More } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const router = useRouter()
const userStore = useUserStore()

// 状态
const albums = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const defaultCover = 'https://via.placeholder.com/200x150?text=No+Cover'

const form = reactive({
  id: null,
  name: '',
  description: '',
  coverUrl: '',
  privacy: 2
})

const rules = {
  name: [{ required: true, message: '请输入相册名称', trigger: 'blur' }]
}

// 加载相册列表
const loadAlbums = async () => {
  if (!userStore.userInfo?.id) return
  
  loading.value = true
  try {
    const res = await albumApi.getUserAlbums(userStore.userInfo.id, { page: 1, size: 100 })
    albums.value = res.data?.records || []
  } catch (error) {
    console.error('获取相册失败', error)
  } finally {
    loading.value = false
  }
}

// 显示创建弹窗
const showCreateDialog = () => {
  isEdit.value = false
  form.id = null
  form.name = ''
  form.description = ''
  form.coverUrl = ''
  form.privacy = 2
  dialogVisible.value = true
}

// 编辑相册
const editAlbum = (album) => {
  isEdit.value = true
  form.id = album.id
  form.name = album.name
  form.description = album.description
  form.coverUrl = album.coverUrl
  form.privacy = album.privacy
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  submitting.value = true
  try {
    if (isEdit.value) {
      await albumApi.updateAlbum(form.id, form)
      ElMessage.success('相册更新成功')
    } else {
      await albumApi.createAlbum({ ...form, userId: userStore.userInfo.id })
      ElMessage.success('相册创建成功')
    }
    dialogVisible.value = false
    loadAlbums()
  } catch (error) {
    console.error('操作失败', error)
  } finally {
    submitting.value = false
  }
}

// 删除相册
const deleteAlbum = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个相册吗？相册中的照片也会被删除', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await albumApi.deleteAlbum(id)
    ElMessage.success('删除成功')
    loadAlbums()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败', error)
    }
  }
}

// 跳转到相册详情
const goToAlbumDetail = (id) => {
  router.push(`/album/${id}`)
}

// 获取隐私类型
const getPrivacyType = (privacy) => {
  const types = { 0: 'info', 1: 'warning', 2: 'success' }
  return types[privacy] || 'info'
}

const getPrivacyText = (privacy) => {
  const texts = { 0: '私密', 1: '好友可见', 2: '公开' }
  return texts[privacy] || '未知'
}

const formatDate = (date) => {
  return date ? dayjs(date).format('YYYY-MM-DD') : ''
}

onMounted(() => {
  if (userStore.isLoggedIn) {
    userStore.fetchUserInfo()
    loadAlbums()
  } else {
    router.push('/login')
  }
})
</script>

<style scoped>
.album-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.page-navbar {
  position: sticky;
  top: 0;
  height: 60px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  z-index: 100;
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
  gap: 10px;
}

.page-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

.content-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 30px;
  
  h2 {
    margin: 0;
    font-size: 24px;
  }
  
  .album-count {
    color: #909399;
    font-size: 14px;
  }
}

.album-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.album-card {
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  position: relative;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  }
}

.album-cover {
  position: relative;
  height: 180px;
  overflow: hidden;
  border-radius: 8px;
  margin-bottom: 15px;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  .album-overlay {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    padding: 10px;
    background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
    color: #fff;
    font-size: 14px;
  }
}

.album-info {
  h4 {
    margin: 0 0 8px;
    font-size: 16px;
  }
  
  p {
    margin: 0 0 10px;
    font-size: 14px;
    color: #909399;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  
  .album-meta {
    display: flex;
    align-items: center;
    justify-content: space-between;
    
    .date {
      font-size: 12px;
      color: #c0c4cc;
    }
  }
}

.album-actions {
  position: absolute;
  top: 10px;
  right: 10px;
}

.add-card {
  border: 2px dashed #dcdfe6;
  background: transparent;
  
  &:hover {
    border-color: #409eff;
    background: #f0f9ff;
  }
  
  .add-content {
    height: 200px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 15px;
    color: #909399;
  }
}
</style>
