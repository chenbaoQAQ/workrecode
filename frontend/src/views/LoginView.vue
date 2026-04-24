<template>
  <div class="login-container">
    <el-card class="login-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h2>工时统计系统</h2>
          <p>请登录您的账号</p>
        </div>
      </template>

      <el-form ref="loginFormRef" :model="loginForm" :rules="rules" label-position="left" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleLogin" style="width: 100%">
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from '../axios'

// 路由实例
const router = useRouter()

// 加载状态
const loading = ref(false)

// 登录表单引用（注意：ref不能与model同名）
const loginFormRef = ref(null)

// 登录表单数据
const loginForm = reactive({
  username: '',
  password: ''
})

// 表单验证规则
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

/**
 * 处理登录逻辑
 */
const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    // 表单验证
    await loginFormRef.value.validate()
    
    // 设置加载状态
    loading.value = true
    
    // 发送登录请求
    const res = await axios.post('/login', loginForm)
    
    if (res.code === 200) {
      // 登录成功，保存用户信息到本地存储
      localStorage.setItem('user', JSON.stringify(res.data))
      ElMessage.success('登录成功')
      
      // 按角色跳转到对应首页
      router.push(res.data?.role === 'ADMIN' ? '/work-admin' : '/work-records')
    } else {
      // 登录失败，显示错误信息
      ElMessage.error(res.message || '登录失败')
    }
  } catch (error) {
    // 处理表单验证失败或请求异常
    console.log('Login error:', error)
    if (error.name !== 'Error') {
      ElMessage.error('表单验证失败: ' + error.message || '请检查表单填写是否正确')
    } else {
      ElMessage.error('登录失败，请检查后端服务是否启动')
    }
  } finally {
    // 关闭加载状态
    loading.value = false
  }
}
</script>

<style scoped>
/* 登录容器样式 */
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

/* 登录卡片样式 */
.login-card {
  width: 100%;
  max-width: 400px;
  border-radius: 12px;
  overflow: hidden;
}

/* 卡片头部样式 */
.card-header {
  text-align: center;
  margin-bottom: 20px;
}

.card-header h2 {
  margin: 0 0 10px 0;
  color: #333;
}

.card-header p {
  margin: 0;
  color: #666;
}

/* 表单样式 */
.el-form {
  padding: 0 20px 20px;
}

.el-form-item {
  margin-bottom: 20px;
}
</style>
