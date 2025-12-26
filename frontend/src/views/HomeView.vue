<template>
  <div class="home-container">
    <h2 class="page-title">欢迎使用员工管理系统</h2>

    <div class="dashboard-stats">
      <el-card shadow="hover" class="stat-card">
        <div class="stat-content">
          <div class="stat-number">{{ employeeCount }}</div>
          <div class="stat-label">员工总数</div>
        </div>
        <div class="stat-icon">
          <el-icon><User /></el-icon>
        </div>
      </el-card>

      <el-card shadow="hover" class="stat-card">
        <div class="stat-content">
          <div class="stat-number">{{ departmentCount }}</div>
          <div class="stat-label">部门总数</div>
        </div>
        <div class="stat-icon">
          <el-icon><OfficeBuilding /></el-icon>
        </div>
      </el-card>
    </div>

    <div class="recent-employees">
      <h3>最近员工</h3>
      <el-table :data="recentEmployees" style="width: 100%" stripe border>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="gender" label="性别" width="100" align="center" />
        <!-- 关键修复：用 departmentName（驼峰） -->
        <el-table-column prop="departmentName" label="部门" width="150" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { User, OfficeBuilding } from '@element-plus/icons-vue'
import axios from '../axios'

const employeeCount = ref(0)
const departmentCount = ref(0)
const recentEmployees = ref([])

onMounted(() => {
  fetchStats()
  fetchRecentEmployees()
})

const fetchStats = async () => {
  try {
    const employeesResponse = await axios.get('/employees')
    if (employeesResponse?.code === 200) {
      employeeCount.value = Array.isArray(employeesResponse.data) ? employeesResponse.data.length : 0
    }

    const departmentsResponse = await axios.get('/departments')
    if (departmentsResponse?.code === 200) {
      departmentCount.value = Array.isArray(departmentsResponse.data) ? departmentsResponse.data.length : 0
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

const fetchRecentEmployees = async () => {
  try {
    const res = await axios.get('/employees')
    if (res?.code === 200 && Array.isArray(res.data)) {
      // 取最新 5 个：按 id 倒序再截取（避免拿到最早的）
      recentEmployees.value = [...res.data]
        .sort((a, b) => (b.id || 0) - (a.id || 0))
        .slice(0, 5)
    } else {
      recentEmployees.value = []
    }
  } catch (error) {
    console.error('获取最近员工失败:', error)
  }
}
</script>

<style scoped>
.home-container {
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.page-title {
  margin: 0 0 20px 0;
  font-size: 20px;
  color: #333;
  border-bottom: 1px solid #e6e6e6;
  padding-bottom: 10px;
}

.dashboard-stats {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.stat-icon {
  font-size: 48px;
  color: #409eff;
  opacity: 0.6;
}

.recent-employees {
  margin-top: 30px;
}

.recent-employees h3 {
  margin: 0 0 15px 0;
  font-size: 16px;
  color: #333;
}
</style>
