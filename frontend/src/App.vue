<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'

onMounted(() => {
  console.log(`
    ██╗    ██╗██╗  ██╗ ██████╗ ███████╗ ██████╗ ███╗   ██╗
    ██║    ██║██║ ██╔╝██╔═══██╗██╔════╝██╔═══██╗████╗  ██║
    ██║ █╗ ██║█████╔╝ ██║   ██║███████╗██║   ██║██╔██╗ ██║
    ██║███╗██║██╔═██╗ ██║   ██║╚════██║██║   ██║██║╚██╗██║
    ╚███╔███╔╝██║  ██╗╚██████╔╝███████║╚██████╔╝██║ ╚████║
     ╚══╝╚══╝ ╚═╝  ╚═╝ ╚═════╝ ╚══════╝ ╚═════╝ ╚═╝  ╚═══╝
    
    🎉 NeekoMap Frontend 已启动!
    📍 基于Web电子地图的照片服务系统
    🌐 http://localhost:3000
  `)
})
</script>

<style>
#app {
  width: 100%;
  height: 100%;
  min-height: 100vh;
}

html, body {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
}
</style>
