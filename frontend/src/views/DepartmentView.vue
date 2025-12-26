<template>
  <div class="department-container">
    <h2 class="page-title">部门管理</h2>

    <div class="search-add-area">
      <div class="search-form">
        <el-input
          v-model="searchForm.name"
          placeholder="部门名称"
          style="width: 200px; margin-right: 10px"
          clearable
        >
          <template #prefix>
            <el-icon><OfficeBuilding /></el-icon>
          </template>
        </el-input>

        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
      </div>

      <el-button type="success" :icon="Plus" @click="handleAdd">新增</el-button>
    </div>

    <el-table :data="pagedDepartments" stripe style="width: 100%">
      <el-table-column type="index" label="序号" width="80" />
      <el-table-column prop="name" label="部门名称" />
      <el-table-column prop="description" label="部门描述" />
      <el-table-column prop="employeeCount" label="员工数量" width="120" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="filteredDepartments.length"
        :page-size="pageSize"
        :current-page="currentPage"
        @current-change="handleCurrentChange"
      />
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="部门名称" required>
          <el-input v-model="form.name" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="部门描述">
          <el-input v-model="form.description" type="textarea" placeholder="请输入部门描述" />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSave">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { OfficeBuilding, Search, Plus, Edit, Delete } from '@element-plus/icons-vue'
import axios from '../axios'

// ---- state ----
const departments = ref([])
const searchForm = ref({ name: '' })

const currentPage = ref(1)
const pageSize = ref(10)

const dialogVisible = ref(false)
const dialogTitle = ref('新增部门')
const form = ref({ id: null, name: '', description: '' })

// ---- computed ----
const filteredDepartments = computed(() => {
  const key = (searchForm.value.name || '').trim()
  if (!key) return departments.value
  return departments.value.filter((d) => (d.name || '').includes(key))
})

const pagedDepartments = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredDepartments.value.slice(start, start + pageSize.value)
})

// ---- api ----
const fetchDepartments = async () => {
  try {
    const res = await axios.get('/departments')
    if (res && res.code === 200) {
      departments.value = Array.isArray(res.data) ? res.data : []
      departments.value.forEach((d) => {
        if (d.employeeCount == null) d.employeeCount = 0
      })
    } else {
      ElMessage.error(res?.message || '查询失败')
      departments.value = []
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('查询失败')
    departments.value = []
  }
}

const handleSearch = () => {
  currentPage.value = 1
}

const handleCurrentChange = (page) => {
  currentPage.value = page
}

// ---- CRUD ----
const handleAdd = () => {
  dialogTitle.value = '新增部门'
  form.value = { id: null, name: '', description: '' }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑部门'
  form.value = { id: row.id, name: row.name, description: row.description }
  dialogVisible.value = true
}

const handleSave = async () => {
  const name = (form.value.name || '').trim()
  if (!name) {
    ElMessage.warning('部门名称不能为空')
    return
  }

  const payload = { name: name, description: form.value.description || '' }

  try {
    let res
    if (form.value.id) {
      res = await axios.put(`/departments/${form.value.id}`, payload)
    } else {
      res = await axios.post('/departments', payload)
    }

    if (res && res.code === 200) {
      ElMessage.success('操作成功')
      dialogVisible.value = false
      await fetchDepartments()
      currentPage.value = 1
    } else {
      ElMessage.error(res?.message || '操作失败')
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除部门「${row.name}」吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await axios.delete(`/departments/${row.id}`)
    if (res && res.code === 200) {
      ElMessage.success('删除成功')
      await fetchDepartments()
      currentPage.value = 1
    } else {
      ElMessage.error(res?.message || '删除失败')
    }
  } catch (e) {
    if (e !== 'cancel') {
      console.error(e)
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  fetchDepartments()
})
</script>

<style scoped>
.department-container {
  padding: 20px;
}

.page-title {
  margin-bottom: 20px;
  font-size: 22px;
  font-weight: bold;
}

.search-add-area {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
