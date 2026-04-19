<template>
  <div class="friends-container">
    <el-row :gutter="20">
      <!-- 左侧：好友列表 -->
      <el-col :span="16">
        <el-card class="friends-list-card">
          <template #header>
            <div class="card-header">
              <span>我的好友</span>
              <el-input 
                v-model="searchKeyword" 
                placeholder="搜索好友" 
                prefix-icon="Search"
                style="width: 200px;"
                clearable
              />
            </div>
          </template>
          
          <el-tabs v-model="activeTab" @tab-change="handleTabChange">
            <el-tab-pane label="全部好友" name="all">
              <div class="friend-list" v-if="filteredFriends.length > 0">
                <div 
                  v-for="friend in filteredFriends" 
                  :key="friend.id" 
                  class="friend-item"
                >
                  <div class="friend-info" @click="viewFriendProfile(friend)">
                    <el-avatar :size="50" :src="friend.avatar">
                      <el-icon :size="24"><UserFilled /></el-icon>
                    </el-avatar>
                    <div class="friend-detail">
                      <div class="friend-name">{{ friend.nickname || friend.username }}</div>
                      <div class="friend-meta">
                        <span>{{ friend.city || '未知城市' }}</span>
                        <span v-if="friend.commonPhotos">共同照片: {{ friend.commonPhotos }}张</span>
                      </div>
                    </div>
                  </div>
                  <div class="friend-actions">
                    <el-button text circle @click="openChat(friend)">
                      <el-icon><ChatDotRound /></el-icon>
                    </el-button>
                    <el-button text circle @click="showLocation(friend)">
                      <el-icon><Location /></el-icon>
                    </el-button>
                    <el-dropdown trigger="click">
                      <el-button text circle>
                        <el-icon><More /></el-icon>
                      </el-button>
                      <template #dropdown>
                        <el-dropdown-menu>
                          <el-dropdown-item @click="viewFriendProfile(friend)">查看资料</el-dropdown-item>
                          <el-dropdown-item @click="viewFriendAlbums(friend)">查看相册</el-dropdown-item>
                          <el-dropdown-item divided @click="handleDeleteFriend(friend.id)">
                            删除好友
                          </el-dropdown-item>
                        </el-dropdown-menu>
                      </template>
                    </el-dropdown>
                  </div>
                </div>
              </div>
              <el-empty v-else description="暂无好友，快去添加吧~">
                <el-button type="primary" @click="showSearchDialog = true">添加好友</el-button>
              </el-empty>
            </el-tab-pane>
            
            <el-tab-pane label="待确认" name="pending">
              <div class="friend-list" v-if="pendingFriends.length > 0">
                <div 
                  v-for="friend in pendingFriends" 
                  :key="friend.id" 
                  class="friend-item"
                >
                  <div class="friend-info">
                    <el-avatar :size="50" :src="friend.avatar">
                      <el-icon :size="24"><UserFilled /></el-icon>
                    </el-avatar>
                    <div class="friend-detail">
                      <div class="friend-name">{{ friend.nickname || friend.username }}</div>
                      <div class="friend-meta">
                        <el-tag type="warning" size="small">等待确认</el-tag>
                      </div>
                    </div>
                  </div>
                  <div class="friend-actions">
                    <el-button type="primary" @click="handleConfirmFriend(friend.id)">接受</el-button>
                    <el-button @click="handleRejectFriend(friend.id)">拒绝</el-button>
                  </div>
                </div>
              </div>
              <el-empty v-else description="没有待确认的好友请求" />
            </el-tab-pane>
          </el-tabs>
          
          <div class="add-friend-btn">
            <el-button type="primary" @click="showSearchDialog = true">
              <el-icon><Plus /></el-icon>添加好友
            </el-button>
          </div>
        </el-card>
      </el-col>
      
      <!-- 右侧：好友地图 -->
      <el-col :span="8">
        <el-card class="friend-map-card">
          <template #header>
            <span>好友分布</span>
          </template>
          <div id="friend-map" class="friend-map"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 搜索好友对话框 -->
    <el-dialog v-model="showSearchDialog" title="添加好友" width="500px">
      <el-input 
        v-model="searchKeyword" 
        placeholder="输入用户名或邮箱搜索" 
        prefix-icon="Search"
        @keyup.enter="handleSearchFriend"
        clearable
      />
      <div class="search-results" v-if="searchResults.length > 0">
        <div 
          v-for="user in searchResults" 
          :key="user.id" 
          class="search-result-item"
        >
          <el-avatar :size="40" :src="user.avatar">
            <el-icon><UserFilled /></el-icon>
          </el-avatar>
          <div class="user-info">
            <div class="user-name">{{ user.nickname || user.username }}</div>
            <div class="user-email">{{ user.email }}</div>
          </div>
          <el-button 
            v-if="user.isFriend === 0" 
            type="primary" 
            size="small"
            @click="handleAddFriend(user.id)"
          >
            添加
          </el-button>
          <el-tag v-else-if="user.isFriend === 1" type="success">已添加</el-tag>
          <el-tag v-else type="warning">待确认</el-tag>
        </div>
      </div>
    </el-dialog>
    
    <!-- 好友资料对话框 -->
    <el-dialog v-model="showProfileDialog" title="查看资料" width="400px">
      <div class="profile-content">
        <el-avatar :size="80" :src="currentFriend?.avatar">
          <el-icon :size="36"><UserFilled /></el-icon>
        </el-avatar>
        <div class="profile-info">
          <div class="profile-name">{{ currentFriend?.nickname || currentFriend?.username }}</div>
          <div class="profile-meta">
            <span><el-icon><Message /></el-icon> {{ currentFriend?.email }}</span>
            <span><el-icon><Location /></el-icon> {{ currentFriend?.city || '未知' }}</span>
          </div>
          <div class="profile-bio">{{ currentFriend?.bio || '这个人很懒，什么都没写' }}</div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showProfileDialog = false">关闭</el-button>
        <el-button type="primary" @click="openChat(currentFriend)">发消息</el-button>
      </template>
    </el-dialog>
    
    <!-- 聊天对话框 -->
    <el-dialog 
      v-model="showChatDialog" 
      :title="`与 ${currentFriend?.nickname || currentFriend?.username} 的聊天`" 
      width="600px"
    >
      <div class="chat-container">
        <div class="chat-messages" ref="chatMessagesRef">
          <div 
            v-for="msg in chatMessages" 
            :key="msg.id" 
            :class="['message-item', msg.isSelf ? 'self' : 'other']"
          >
            <el-avatar :size="36" :src="msg.isSelf ? userStore.avatar : currentFriend?.avatar">
              <el-icon><UserFilled /></el-icon>
            </el-avatar>
            <div class="message-content">
              <div class="message-bubble">{{ msg.content }}</div>
              <div class="message-time">{{ formatTime(msg.createTime) }}</div>
            </div>
          </div>
        </div>
        <div class="chat-input">
          <el-input 
            v-model="chatMessage" 
            placeholder="输入消息..."
            @keyup.enter="handleSendMessage"
          />
          <el-button type="primary" @click="handleSendMessage">
            <el-icon><Promotion /></el-icon>
          </el-button>
        </div>
      </div>
    </el-dialog>
    
    <!-- 位置对话框 -->
    <el-dialog v-model="showLocationDialog" title="好友位置" width="800px">
      <div id="location-dialog-map" class="location-map"></div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, UserFilled, ChatDotRound, Location, Plus, More, Message, Promotion } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { getFriends, getPendingFriends, searchUsers, addFriend, confirmFriend, rejectFriend, deleteFriend, getChatHistory } from '@/api/request'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('all')
const searchKeyword = ref('')
const showSearchDialog = ref(false)
const showProfileDialog = ref(false)
const showChatDialog = ref(false)
const showLocationDialog = ref(false)

const friends = ref([])
const pendingFriends = ref([])
const searchResults = ref([])
const currentFriend = ref(null)
const chatMessage = ref('')
const chatMessages = ref([])
const chatMessagesRef = ref(null)

const filteredFriends = computed(() => {
  if (!searchKeyword.value) return friends.value
  const kw = searchKeyword.value.toLowerCase()
  return friends.value.filter(f => 
    (f.username || '').toLowerCase().includes(kw) ||
    (f.nickname || '').toLowerCase().includes(kw)
  )
})

let friendMap = null
let locationMap = null

onMounted(() => {
  loadFriends()
  loadPendingFriends()
  initFriendMap()
})

onUnmounted(() => {
  if (friendMap) friendMap.remove()
  if (locationMap) locationMap.remove()
})

const loadFriends = async () => {
  try {
    const res = await getFriends()
    if (res.code === 200) {
      friends.value = res.data
    }
  } catch (error) {
    console.error('加载好友列表失败:', error)
  }
}

const loadPendingFriends = async () => {
  try {
    const res = await getPendingFriends()
    if (res.code === 200) {
      pendingFriends.value = res.data
    }
  } catch (error) {
    console.error('加载待确认好友失败:', error)
  }
}

const handleTabChange = () => {
  if (activeTab.value === 'pending') {
    loadPendingFriends()
  }
}

const initFriendMap = () => {
  nextTick(() => {
    const L = window.L
    if (!L) return
    
    friendMap = L.map('friend-map').setView([30.52, 114.36], 4)
    
    L.tileLayer('https://t{s}.tianditu.gov.cn/DataServer?T=vec_w&X={x}&Y={y}&L={z}', {
      subdomains: '0123456789',
      attribution: '© 天地图'
    }).addTo(friendMap)
    
    // 添加好友标记
    friends.value.forEach(friend => {
      if (friend.longitude && friend.latitude) {
        const marker = L.marker([friend.latitude, friend.longitude]).addTo(friendMap)
        marker.bindPopup(`
          <div style="text-align: center;">
            <img src="${friend.avatar}" style="width: 40px; height: 40px; border-radius: 50%;" />
            <div style="margin-top: 5px; font-weight: bold;">${friend.nickname || friend.username}</div>
          </div>
        `)
      }
    })
    
    // 自动调整视图
    if (friends.value.length > 0) {
      const locations = friends.value.filter(f => f.longitude && f.latitude)
      if (locations.length > 0) {
        const bounds = L.latLngBounds(locations.map(f => [f.latitude, f.longitude]))
        friendMap.fitBounds(bounds, { padding: [50, 50] })
      }
    }
  })
}

const handleSearchFriend = async () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }
  
  try {
    const res = await searchUsers(searchKeyword.value)
    if (res.code === 200) {
      searchResults.value = res.data
    }
  } catch (error) {
    console.error('搜索失败:', error)
  }
}

const handleAddFriend = async (userId) => {
  try {
    const res = await addFriend(userId)
    if (res.code === 200) {
      ElMessage.success('已发送好友请求')
      handleSearchFriend()
    }
  } catch (error) {
    ElMessage.error('添加失败')
  }
}

const handleConfirmFriend = async (friendId) => {
  try {
    const res = await confirmFriend(friendId)
    if (res.code === 200) {
      ElMessage.success('已添加为好友')
      loadFriends()
      loadPendingFriends()
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleRejectFriend = async (friendId) => {
  try {
    await ElMessageBox.confirm('确定要拒绝该好友请求吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await rejectFriend(friendId)
    if (res.code === 200) {
      ElMessage.success('已拒绝')
      loadPendingFriends()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const handleDeleteFriend = async (friendId) => {
  try {
    await ElMessageBox.confirm('确定要删除该好友吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await deleteFriend(friendId)
    if (res.code === 200) {
      ElMessage.success('已删除')
      loadFriends()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const viewFriendProfile = (friend) => {
  currentFriend.value = friend
  showProfileDialog.value = true
}

const viewFriendAlbums = (friend) => {
  router.push({ name: 'FriendAlbums', params: { userId: friend.id } })
}

const openChat = async (friend) => {
  currentFriend.value = friend
  showProfileDialog.value = false
  showChatDialog.value = true
  
  try {
    const res = await getChatHistory(friend.id)
    if (res.code === 200) {
      chatMessages.value = res.data
      scrollToBottom()
    }
  } catch (error) {
    console.error('加载聊天记录失败:', error)
  }
}

const handleSendMessage = () => {
  if (!chatMessage.value.trim()) return
  
  chatMessages.value.push({
    id: Date.now(),
    content: chatMessage.value,
    isSelf: true,
    createTime: new Date().toISOString()
  })
  
  scrollToBottom()
  chatMessage.value = ''
}

const scrollToBottom = () => {
  nextTick(() => {
    if (chatMessagesRef.value) {
      chatMessagesRef.value.scrollTop = chatMessagesRef.value.scrollHeight
    }
  })
}

const showLocation = (friend) => {
  if (!friend.longitude || !friend.latitude) {
    ElMessage.info('该好友未开启位置共享')
    return
  }
  
  showLocationDialog.value = true
  nextTick(() => {
    const L = window.L
    if (!L) return
    
    if (locationMap) locationMap.remove()
    
    locationMap = L.map('location-dialog-map').setView([friend.latitude, friend.longitude], 12)
    
    L.tileLayer('https://t{s}.tianditu.gov.cn/DataServer?T=vec_w&X={x}&Y={y}&L={z}', {
      subdomains: '0123456789',
      attribution: '© 天地图'
    }).addTo(locationMap)
    
    L.marker([friend.latitude, friend.longitude]).addTo(locationMap)
      .bindPopup(friend.nickname || friend.username)
      .openPopup()
  })
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getHours()}:${String(date.getMinutes()).padStart(2, '0')}`
}
</script>

<style scoped>
.friends-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.friend-list {
  max-height: 500px;
  overflow-y: auto;
}

.friend-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #ebeef5;
  transition: background-color 0.3s;
}

.friend-item:hover {
  background-color: #f5f7fa;
}

.friend-info {
  display: flex;
  align-items: center;
  gap: 15px;
  cursor: pointer;
  flex: 1;
}

.friend-detail {
  flex: 1;
}

.friend-name {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.friend-meta {
  display: flex;
  gap: 15px;
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.friend-actions {
  display: flex;
  gap: 5px;
}

.add-friend-btn {
  margin-top: 20px;
  text-align: center;
}

.friend-map-card {
  height: 100%;
}

.friend-map {
  height: 500px;
}

.search-results {
  margin-top: 20px;
  max-height: 300px;
  overflow-y: auto;
}

.search-result-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 10px;
  border-bottom: 1px solid #ebeef5;
}

.user-info {
  flex: 1;
}

.user-name {
  font-weight: bold;
  color: #303133;
}

.user-email {
  font-size: 12px;
  color: #909399;
}

.profile-content {
  text-align: center;
  padding: 20px;
}

.profile-info {
  margin-top: 20px;
}

.profile-name {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 10px;
}

.profile-meta {
  display: flex;
  justify-content: center;
  gap: 20px;
  color: #909399;
  font-size: 14px;
}

.profile-bio {
  margin-top: 15px;
  color: #606266;
  line-height: 1.6;
}

.chat-container {
  display: flex;
  flex-direction: column;
  height: 400px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 8px;
}

.message-item {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}

.message-item.self {
  flex-direction: row-reverse;
}

.message-content {
  max-width: 70%;
}

.message-bubble {
  padding: 10px 15px;
  border-radius: 8px;
  background: white;
  word-wrap: break-word;
}

.message-item.self .message-bubble {
  background: #409eff;
  color: white;
}

.message-time {
  font-size: 11px;
  color: #c0c4cc;
  margin-top: 5px;
}

.message-item.self .message-time {
  text-align: right;
}

.chat-input {
  display: flex;
  gap: 10px;
  margin-top: 15px;
}

.location-map {
  height: 500px;
}
</style>
