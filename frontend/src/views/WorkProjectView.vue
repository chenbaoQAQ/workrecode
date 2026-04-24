<template>
  <div class="page-card">
    <div class="toolbar">
      <h2 class="page-title">项目管理</h2>
      <el-button type="primary" @click="openDialog()">新增项目</el-button>
    </div>

    <el-table :data="projects" stripe border>
      <el-table-column prop="name" label="项目名称" min-width="180" />
      <el-table-column prop="description" label="项目说明" min-width="220" />
      <el-table-column prop="enabled" label="状态" width="110">
        <template #default="{ row }">
          <el-tag :type="row.enabled ? 'success' : 'info'">{{ row.enabled ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="openDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteProject(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑项目' : '新增项目'" width="520px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="项目名称" required>
          <el-input v-model="form.name" placeholder="请输入项目名称" />
        </el-form-item>
        <el-form-item label="项目说明">
          <el-input v-model="form.description" type="textarea" placeholder="请输入项目说明" />
        </el-form-item>
        <el-form-item label="是否启用">
          <el-switch v-model="form.enabled" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveProject">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from '../axios'

const projects = ref([])
const dialogVisible = ref(false)
const form = ref({ id: null, name: '', description: '', enabled: true })

const fetchProjects = async () => {
  const res = await axios.get('/work-projects')
  if (res.code === 200) projects.value = res.data || []
}

const openDialog = (row) => {
  form.value = row ? { ...row } : { id: null, name: '', description: '', enabled: true }
  dialogVisible.value = true
}

const saveProject = async () => {
  if (!form.value.name?.trim()) {
    ElMessage.warning('项目名称不能为空')
    return
  }
  const payload = {
    name: form.value.name.trim(),
    description: form.value.description || '',
    enabled: form.value.enabled
  }
  const res = form.value.id
    ? await axios.put(`/work-projects/${form.value.id}`, payload)
    : await axios.post('/work-projects', payload)
  if (res.code === 200) {
    ElMessage.success('保存成功')
    dialogVisible.value = false
    fetchProjects()
  } else {
    ElMessage.error(res.message || '保存失败')
  }
}

const deleteProject = async (row) => {
  await ElMessageBox.confirm(`确认删除项目「${row.name}」吗？已有权重记录的项目不建议删除。`, '提示', {
    type: 'warning'
  })
  const res = await axios.delete(`/work-projects/${row.id}`)
  if (res.code === 200) {
    ElMessage.success('删除成功')
    fetchProjects()
  }
}

onMounted(fetchProjects)
</script>

<style scoped>
.page-card {
  background: #fff;
  border-radius: 10px;
  padding: 22px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
}

.page-title {
  margin: 0;
}
</style>
