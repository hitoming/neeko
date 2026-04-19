import { defineStore } from 'pinia'
import { ref } from 'vue'
import { photoApi } from '@/api/photo'

export const usePhotoStore = defineStore('photo', () => {
  // 状态
  const publicPhotos = ref([])
  const currentPhoto = ref(null)
  const loading = ref(false)
  
  // 获取公开照片
  const fetchPublicPhotos = async (params = {}) => {
    loading.value = true
    try {
      const res = await photoApi.getPublicPhotos(params)
      publicPhotos.value = res.data.records || []
      return res.data
    } finally {
      loading.value = false
    }
  }
  
  // 按标签获取照片
  const fetchPhotosByTag = async (tagId, params = {}) => {
    loading.value = true
    try {
      const res = await photoApi.getPhotosByTag(tagId, params)
      publicPhotos.value = res.data.records || []
      return res.data
    } finally {
      loading.value = false
    }
  }
  
  // 按位置获取照片
  const fetchNearbyPhotos = async (lng, lat, radius = 5, params = {}) => {
    loading.value = true
    try {
      const res = await photoApi.getNearbyPhotos({ lng, lat, radius, ...params })
      publicPhotos.value = res.data.records || []
      return res.data
    } finally {
      loading.value = false
    }
  }
  
  return {
    publicPhotos,
    currentPhoto,
    loading,
    fetchPublicPhotos,
    fetchPhotosByTag,
    fetchNearbyPhotos
  }
})
