<template>
  <div class="home-container">
    <h2 class="page-title">欢迎使用工时统计系统</h2>

    <div class="dashboard-stats">
      <el-card v-if="isAdmin" shadow="hover" class="stat-card">
        <div class="stat-content">
          <div class="stat-number">{{ employeeCount }}</div>
          <div class="stat-label">员工总数</div>
        </div>
        <div class="stat-icon">
          <el-icon><User /></el-icon>
        </div>
      </el-card>

      <el-card v-if="isAdmin" shadow="hover" class="stat-card">
        <div class="stat-content">
          <div class="stat-number">{{ departmentCount }}</div>
          <div class="stat-label">部门总数</div>
        </div>
        <div class="stat-icon">
          <el-icon><OfficeBuilding /></el-icon>
        </div>
      </el-card>

      <el-card shadow="hover" class="stat-card">
        <div class="stat-content">
          <div class="stat-number">{{ recordCount }}</div>
          <div class="stat-label">{{ isAdmin ? '全部权重记录' : '我的权重记录' }}</div>
        </div>
        <div class="stat-icon">
          <el-icon><Clock /></el-icon>
        </div>
      </el-card>

      <el-card v-if="!isAdmin" shadow="hover" class="stat-card">
        <div class="stat-content">
          <div class="stat-number">{{ pendingCount }}</div>
          <div class="stat-label">我的待审批</div>
        </div>
        <div class="stat-icon">
          <el-icon><Clock /></el-icon>
        </div>
      </el-card>

      <el-card v-if="!isAdmin" shadow="hover" class="stat-card">
        <div class="stat-content">
          <div class="stat-number">{{ approvedCount }}</div>
          <div class="stat-label">我的已通过</div>
        </div>
        <div class="stat-icon">
          <el-icon><Clock /></el-icon>
        </div>
      </el-card>
    </div>

    <div class="recent-employees">
      <h3>{{ isAdmin ? '最近权重记录' : '我的最近审批记录' }}</h3>
      <el-table :data="recentEmployees" style="width: 100%" stripe border>
        <el-table-column v-if="isAdmin" prop="employeeName" label="员工" width="120" />
        <el-table-column prop="workContent" label="项目名称" min-width="220" />
        <el-table-column prop="workDate" label="登记日期" width="120" />
        <el-table-column prop="workHours" label="项目权重" width="110" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { Clock, OfficeBuilding, User } from '@element-plus/icons-vue'
import axios from '../axios'

const employeeCount = ref(0)
const departmentCount = ref(0)
const recordCount = ref(0)
const pendingCount = ref(0)
const approvedCount = ref(0)
const recentEmployees = ref([])
const user = computed(() => JSON.parse(localStorage.getItem('user') || '{}'))
const isAdmin = computed(() => user.value?.role === 'ADMIN')

const statusText = (status) => ({ APPROVED: '已通过', PENDING: '待审批', REJECTED: '已驳回', CANCELLED: '已撤回' }[status] || status)
const statusType = (status) => ({ APPROVED: 'success', PENDING: 'warning', REJECTED: 'danger', CANCELLED: 'info' }[status] || 'info')

onMounted(() => {
  fetchStats()
  fetchRecentEmployees()
})

const fetchStats = async () => {
  try {
    if (isAdmin.value) {
      const employeesResponse = await axios.get('/employees')
      if (employeesResponse?.code === 200) {
        employeeCount.value = Array.isArray(employeesResponse.data) ? employeesResponse.data.length : 0
      }

      const departmentsResponse = await axios.get('/departments')
      if (departmentsResponse?.code === 200) {
        departmentCount.value = Array.isArray(departmentsResponse.data) ? departmentsResponse.data.length : 0
      }
    }

    const recordsResponse = await axios.get('/work-records', { params: recordQueryParams() })
    if (recordsResponse?.code === 200) {
      const rows = Array.isArray(recordsResponse.data) ? recordsResponse.data : []
      recordCount.value = rows.length
      pendingCount.value = rows.filter((item) => item.status === 'PENDING').length
      approvedCount.value = rows.filter((item) => item.status === 'APPROVED').length
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

const fetchRecentEmployees = async () => {
  try {
    const res = await axios.get('/work-records', { params: recordQueryParams() })
    if (res?.code === 200 && Array.isArray(res.data)) {
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

const recordQueryParams = () => {
  return isAdmin.value ? {} : { employeeId: user.value.id }
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
