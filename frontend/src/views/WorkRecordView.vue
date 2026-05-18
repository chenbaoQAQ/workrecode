<template>
  <div class="page-card">
    <div class="page-header">
      <div>
        <h2 class="page-title">工时权重填报</h2>
        <p class="page-subtitle">选择登记日期和项目后提交权重；待审批记录可在管理员处理前撤回。</p>
      </div>
      <el-tag type="info">可补登最近 7 天</el-tag>
    </div>

    <el-form :model="form" label-width="100px" class="entry-form">
      <el-form-item label="登记日期" required>
        <el-date-picker
          v-model="form.workDate"
          type="date"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          placeholder="请选择日期"
          :disabled-date="disableOutOfRangeDate"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="项目名称" required>
        <el-select v-model="form.projectId" placeholder="请选择项目" style="width: 100%">
          <el-option
            v-for="item in projects"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="项目权重" required>
        <el-input-number v-model="form.workHours" :min="0.01" :max="2" :step="0.1" :precision="2" style="width: 100%" />
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" placeholder="可填写补充说明或加班原因" />
      </el-form-item>
      <el-form-item>
        <el-alert
          title="员工提交的记录统一进入待审批；管理员审批前可以撤回，审批后才会计入正式统计。"
          type="warning"
          show-icon
          :closable="false"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="saving" @click="submitRecord">提交权重</el-button>
        <el-button @click="resetForm">重置</el-button>
      </el-form-item>
    </el-form>

    <el-divider />

    <h3 class="section-title">我的权重记录</h3>
    <el-table :data="records" stripe border>
      <el-table-column prop="workContent" label="项目名称" min-width="220" />
      <el-table-column prop="workDate" label="登记日期" width="120" />
      <el-table-column prop="workHours" label="项目权重" width="110" />
      <el-table-column prop="overtimeHours" label="加班权重" width="110" />
      <el-table-column prop="status" label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="180" />
      <el-table-column prop="adminRemark" label="审批备注" min-width="160" />
      <el-table-column label="操作" width="110" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.status === 'PENDING'"
            size="small"
            type="warning"
            plain
            @click="cancelRecord(row)"
          >
            撤回
          </el-button>
          <span v-else class="muted-action">-</span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from '../axios'

const records = ref([])
const projects = ref([])
const saving = ref(false)
const user = computed(() => JSON.parse(localStorage.getItem('user') || '{}'))

const form = ref({
  workDate: new Date().toISOString().slice(0, 10),
  projectId: undefined,
  workHours: 1,
  remark: ''
})

const disableOutOfRangeDate = (date) => {
  const today = new Date()
  today.setHours(23, 59, 59, 999)
  const start = new Date()
  start.setDate(start.getDate() - 7)
  start.setHours(0, 0, 0, 0)
  return date < start || date > today
}

const statusText = (status) => ({ APPROVED: '已通过', PENDING: '待审批', REJECTED: '已驳回', CANCELLED: '已撤回' }[status] || status)
const statusType = (status) => ({ APPROVED: 'success', PENDING: 'warning', REJECTED: 'danger', CANCELLED: 'info' }[status] || 'info')

const fetchRecords = async () => {
  const params = user.value.role === 'ADMIN' ? {} : { employeeId: user.value.id }
  const res = await axios.get('/work-records', { params })
  if (res.code === 200) records.value = res.data || []
}

const fetchProjects = async () => {
  const res = await axios.get('/work-projects')
  if (res.code === 200) {
    projects.value = (res.data || []).filter((item) => item.enabled)
  }
}

const resetForm = () => {
  form.value = {
    workDate: new Date().toISOString().slice(0, 10),
    projectId: undefined,
    workHours: 1,
    remark: ''
  }
}

const submitRecord = async () => {
  const selectedProject = projects.value.find((item) => item.id === form.value.projectId)
  if (!form.value.workDate || !form.value.projectId || !form.value.workHours || !selectedProject) {
    ElMessage.warning('请完整填写日期、项目和权重')
    return
  }
  saving.value = true
  try {
    const res = await axios.post('/work-records', {
      ...form.value,
      workContent: selectedProject.name,
      employeeId: user.value.id,
      employeeName: user.value.name || user.value.username || '员工'
    })
    if (res.code === 200) {
      ElMessage.success('已提交管理员审批')
      resetForm()
      await fetchRecords()
    } else {
      ElMessage.error(res.message || '提交失败')
    }
  } finally {
    saving.value = false
  }
}

const cancelRecord = async (row) => {
  try {
    await ElMessageBox.confirm(`确定撤回「${row.workContent}」这条待审批记录吗？`, '撤回确认', {
      confirmButtonText: '撤回',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await axios.post(`/work-records/${row.id}/cancel`, {
      employeeId: user.value.id
    })
    if (res.code === 200) {
      ElMessage.success('已撤回')
      await fetchRecords()
    } else {
      ElMessage.error(res.message || '撤回失败')
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('撤回失败')
    }
  }
}

onMounted(async () => {
  await fetchProjects()
  await fetchRecords()
})
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
  gap: 16px;
  align-items: flex-start;
}

.page-title {
  margin: 0 0 8px;
}

.page-subtitle {
  margin: 0;
  color: #667085;
}

.entry-form {
  max-width: 720px;
  margin-top: 22px;
}

.section-title {
  margin: 10px 0 16px;
}

.muted-action {
  color: #98a2b3;
}
</style>
