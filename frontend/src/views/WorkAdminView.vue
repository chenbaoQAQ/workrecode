<template>
  <div class="page-card">
    <div class="page-header">
      <h2 class="page-title">权重统计与审批</h2>
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        format="YYYY-MM-DD"
        value-format="YYYY-MM-DD"
        @change="fetchAll"
      />
    </div>

    <div class="stat-grid">
      <el-card shadow="never">
        <div class="stat-number">{{ totals.totalWorkHours }}</div>
        <div class="stat-label">填报总权重</div>
      </el-card>
      <el-card shadow="never">
        <div class="stat-number">{{ totals.totalOvertimeHours }}</div>
        <div class="stat-label">加班总权重</div>
      </el-card>
      <el-card shadow="never">
        <div class="stat-number">{{ pendingRecords.length }}</div>
        <div class="stat-label">待审批申请</div>
      </el-card>
    </div>

    <h3>待审批申请</h3>
    <el-table :data="pendingRecords" stripe border>
      <el-table-column prop="employeeName" label="申请人" width="120" />
      <el-table-column prop="workContent" label="项目名称" min-width="220" />
      <el-table-column prop="workDate" label="登记日期" width="120" />
      <el-table-column prop="workHours" label="项目权重" width="100" />
      <el-table-column label="加班权重" width="100">
        <template #default="{ row }">
          {{ displayOvertime(row) }}
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="申请说明" min-width="180" />
      <el-table-column label="操作" width="170">
        <template #default="{ row }">
          <el-button size="small" type="success" @click="approve(row)">同意</el-button>
          <el-button size="small" type="danger" @click="reject(row)">驳回</el-button>
        </template>
      </el-table-column>
    </el-table>

    <h3>全部登记记录</h3>
    <el-table :data="records" stripe border>
      <el-table-column prop="employeeName" label="员工" width="120" />
      <el-table-column prop="workContent" label="项目名称" min-width="220" />
      <el-table-column prop="workDate" label="登记日期" width="120" />
      <el-table-column prop="workHours" label="项目权重" width="100" />
      <el-table-column label="加班权重" width="100">
        <template #default="{ row }">
          {{ displayOvertime(row) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="openEdit(row)">修改</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="editVisible" title="修改权重登记" width="560px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="项目名称">
          <el-input :model-value="editForm.workContent" type="textarea" readonly />
        </el-form-item>
        <el-form-item label="登记日期">
          <el-input :model-value="editForm.workDate" readonly />
        </el-form-item>
        <el-form-item label="项目权重">
          <el-input :model-value="editForm.workHours" readonly />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editForm.status" style="width: 100%">
            <el-option label="已通过" value="APPROVED" />
            <el-option label="待审批" value="PENDING" />
            <el-option label="已驳回" value="REJECTED" />
          </el-select>
        </el-form-item>
        <el-form-item label="管理员备注">
          <el-input v-model="editForm.adminRemark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="saveEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from '../axios'

const dateRange = ref([])
const records = ref([])
const editVisible = ref(false)
const editForm = ref({})

const queryParams = computed(() => ({
  startDate: dateRange.value?.[0] || undefined,
  endDate: dateRange.value?.[1] || undefined
}))

const pendingRecords = computed(() => records.value.filter((item) => item.status === 'PENDING'))

const approvedWeightMap = computed(() => {
  return records.value
    .filter((item) => item.status === 'APPROVED')
    .reduce((map, item) => {
      const key = `${item.employeeId}-${item.workDate}`
      map[key] = (map[key] || 0) + Number(item.workHours || 0)
      return map
    }, {})
})

const totals = computed(() => {
  const approvedRecords = records.value.filter((item) => item.status === 'APPROVED')
  const result = approvedRecords.reduce((sum, item) => {
    sum.totalWorkHours += Number(item.workHours || 0)
    sum.totalOvertimeHours += Number(item.overtimeHours || 0)
    return sum
  }, { totalWorkHours: 0, totalOvertimeHours: 0 })
  return {
    totalWorkHours: result.totalWorkHours.toFixed(2),
    totalOvertimeHours: result.totalOvertimeHours.toFixed(2)
  }
})

const statusText = (status) => ({ APPROVED: '已通过', PENDING: '待审批', REJECTED: '已驳回', CANCELLED: '已撤回' }[status] || status)
const statusType = (status) => ({ APPROVED: 'success', PENDING: 'warning', REJECTED: 'danger', CANCELLED: 'info' }[status] || 'info')
const formatWeight = (value) => Number(value || 0).toFixed(2)

const displayOvertime = (row) => {
  if (row.status !== 'PENDING') {
    return formatWeight(row.overtimeHours)
  }
  const key = `${row.employeeId}-${row.workDate}`
  const approvedWeight = Number(approvedWeightMap.value[key] || 0)
  if (approvedWeight <= 0) {
    return formatWeight(0)
  }
  return formatWeight(Math.max(approvedWeight + Number(row.workHours || 0) - 1, 0))
}

const fetchAll = async () => {
  const [recordRes] = await Promise.all([
    axios.get('/work-records', { params: queryParams.value })
  ])
  if (recordRes.code === 200) records.value = recordRes.data || []
}

const approve = async (row) => {
  await axios.post(`/work-records/${row.id}/approve`, { adminRemark: '同意加班申请' })
  ElMessage.success('已同意')
  fetchAll()
}

const reject = async (row) => {
  const { value } = await ElMessageBox.prompt('请输入驳回原因', '驳回申请', {
    inputPlaceholder: '例如：请补充加班说明'
  })
  await axios.post(`/work-records/${row.id}/reject`, { adminRemark: value || '驳回申请' })
  ElMessage.success('已驳回')
  fetchAll()
}

const openEdit = (row) => {
  editForm.value = { ...row }
  editVisible.value = true
}

const saveEdit = async () => {
  let res
  if (editForm.value.status === 'APPROVED') {
    res = await axios.post(`/work-records/${editForm.value.id}/approve`, {
      adminRemark: editForm.value.adminRemark || '同意申请'
    })
  } else if (editForm.value.status === 'REJECTED') {
    res = await axios.post(`/work-records/${editForm.value.id}/reject`, {
      adminRemark: editForm.value.adminRemark || '驳回申请'
    })
  } else {
    res = await axios.put(`/work-records/${editForm.value.id}`, editForm.value)
  }
  if (res.code === 200) {
    ElMessage.success('修改成功')
    editVisible.value = false
    fetchAll()
  }
}

onMounted(fetchAll)
</script>

<style scoped>
.page-card {
  background: #fff;
  border-radius: 10px;
  padding: 22px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  margin-bottom: 18px;
}

.page-title {
  margin: 0;
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(140px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  color: #2563eb;
}

.stat-label {
  color: #667085;
  margin-top: 6px;
}

h3 {
  margin: 24px 0 14px;
}
</style>
