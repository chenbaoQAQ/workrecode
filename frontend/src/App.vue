<template>
  <!-- 根据登录状态显示不同内容 -->
  <div v-if="!isLoggedIn" class="login-page">
    <router-view />
  </div>
  
  <div v-else class="app-container">
    <!-- 顶部导航栏 -->
    <header class="app-header">
      <div class="header-content">
        <h1 class="app-title">工时统计系统</h1>
        <div class="user-info">
          <span>{{ userInfo.name }} ({{ userInfo.role }})</span>
          <el-button type="text" @click="handleLogout">退出登录</el-button>
        </div>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="app-main">
      <!-- 左侧导航菜单 -->
      <aside class="sidebar">
        <el-menu
          :default-active="activeMenu"
          class="el-menu-vertical-demo"
          background-color="#2a3f54"
          text-color="#fff"
          active-text-color="#ffd04b"
          @select="handleMenuSelect"
        >
          <el-menu-item index="home">
            <el-icon><House /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="employees" v-if="isAdmin">
            <el-icon><User /></el-icon>
            <span>员工管理</span>
          </el-menu-item>
          <el-menu-item index="departments" v-if="isAdmin">
            <el-icon><OfficeBuilding /></el-icon>
            <span>部门管理</span>
          </el-menu-item>
          <el-menu-item index="work-records" v-if="!isAdmin">
            <el-icon><Clock /></el-icon>
            <span>工时填报</span>
          </el-menu-item>
          <el-menu-item index="work-admin" v-if="isAdmin">
            <el-icon><DataAnalysis /></el-icon>
            <span>工时统计</span>
          </el-menu-item>
          <el-menu-item index="work-projects" v-if="isAdmin">
            <el-icon><Folder /></el-icon>
            <span>项目管理</span>
          </el-menu-item>
        </el-menu>
      </aside>

      <!-- 右侧内容 -->
      <section class="content">
        <router-view />
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Clock, DataAnalysis, Folder, House, OfficeBuilding, User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

// 响应式数据
const userInfo = ref({})
const isLoggedIn = ref(false)
const isAdmin = computed(() => userInfo.value?.role === 'ADMIN')

// 计算当前激活的菜单
const activeMenu = computed(() => {
  const path = route.path.replace('/', '')
  return path || 'home'
})

// 检查登录状态
const checkLoginStatus = () => {
  const user = localStorage.getItem('user')
  if (user) {
    userInfo.value = JSON.parse(user)
    isLoggedIn.value = true
  } else {
    isLoggedIn.value = false
  }
}

// 处理菜单选择
const handleMenuSelect = (index) => {
  router.push('/' + index)
}

// 退出登录
const handleLogout = () => {
  // 清除localStorage中的用户信息
  localStorage.removeItem('user')
  // 更新登录状态
  isLoggedIn.value = false
  // 跳转到登录页面
  router.push('/login')
  // 显示退出成功提示
  ElMessage.success('退出登录成功')
}

// 组件挂载时检查登录状态
onMounted(() => {
  checkLoginStatus()
})

// 监听路由变化，每次路由变化时都检查登录状态
watch(() => route.path, () => {
  checkLoginStatus()
})
</script>

<style scoped>
.app-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.app-header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  padding: 0 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.app-title {
  margin: 0;
  font-size: 20px;
  color: #2a3f54;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-info span {
  margin-right: 10px;
}

.app-main {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.sidebar {
  width: 200px;
  background-color: #2a3f54;
  overflow-y: auto;
}

.content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #f5f7fa;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    width: 60px;
  }
  
  .el-menu-item span {
    display: none;
  }
}
</style>
