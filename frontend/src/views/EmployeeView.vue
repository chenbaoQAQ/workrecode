<template>
  <div class="employee-container">
    <h2 class="page-title">员工管理</h2>

    <div class="search-add-area">
      <div class="search-form">
        <el-input
          v-model="searchForm.name"
          placeholder="姓名"
          style="width: 150px; margin-right: 10px;"
          clearable
        >
          <template #prefix>
            <el-icon><User /></el-icon>
          </template>
        </el-input>

        <el-input
          v-model="searchForm.gender"
          placeholder="性别（男/女）"
          style="width: 150px; margin-right: 10px;"
          clearable
        >
          <template #prefix>
            <el-icon><CircleCheck /></el-icon>
          </template>
        </el-input>

        <el-select
          v-model="searchForm.departmentId"
          placeholder="部门"
          style="width: 150px; margin-right: 10px;"
          clearable
        >
          <el-option
            v-for="dept in departments"
            :key="dept.id"
            :label="dept.name"
            :value="dept.id"
          />
        </el-select>

        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          查询
        </el-button>
      </div>

      <el-button type="success" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增
      </el-button>
    </div>

    <div class="employee-table">
      <el-table :data="pagedEmployees" style="width: 100%" stripe border>
        <!-- 序号：跟数据库ID无关，删除/新增后会自动更新 -->
        <el-table-column label="序号" width="80" align="center">
          <template #default="scope">
            {{ (currentPage - 1) * pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>

        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="username" label="登录账号" width="140">
          <template #default="{ row }">
            {{ row.username || `emp${row.id}` }}
          </template>
        </el-table-column>
        <el-table-column prop="gender" label="性别" width="100" align="center" />

        <!-- 后端返回 departmentName（驼峰），不是 department_name -->
        <el-table-column prop="departmentName" label="部门" width="150" />

        <el-table-column label="操作" width="200" align="center">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)" style="margin-right: 5px;">
              <el-icon><Edit /></el-icon>
              修改
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="total"
        :page-size="pageSize"
        :current-page="currentPage"
        @current-change="handleCurrentChange"
      />
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="姓名" required>
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>

        <el-form-item label="登录账号">
          <el-input v-model="form.username" placeholder="不填则自动生成，如 emp10" />
        </el-form-item>

        <el-form-item label="性别" required>
          <el-radio-group v-model="form.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="部门" required>
          <el-select v-model="form.departmentId" placeholder="请选择部门" style="width: 100%;">
            <el-option
              v-for="dept in departments"
              :key="dept.id"
              :label="dept.name"
              :value="dept.id"
            />
          </el-select>
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
import { User, CircleCheck, Search, Plus, Edit, Delete } from '@element-plus/icons-vue'
import axios from '../axios'

// 原始数据
const employeesRaw = ref([])
const departments = ref([])

// 搜索表单
const searchForm = ref({
  name: '',
  gender: '',
  departmentId: null
})

// 弹窗表单：id 用 null，不要用 ''（更稳）
const form = ref({
  id: null,
  name: '',
  username: '',
  gender: '男',
  departmentId: null
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增员工')

// 分页
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

onMounted(async () => {
  await fetchDepartments()
  await fetchEmployees()
})

// 获取员工列表
const fetchEmployees = async () => {
  try {
    const res = await axios.get('/employees')
    if (res.code === 200) {
      employeesRaw.value = Array.isArray(res.data) ? res.data : []
      // 刷新 total
      total.value = filteredEmployees.value.length
    }
  } catch (e) {
    ElMessage.error('获取员工列表失败')
    console.error(e)
  }
}

// 获取部门列表
const fetchDepartments = async () => {
  try {
    const res = await axios.get('/departments')
    if (res.code === 200) {
      departments.value = Array.isArray(res.data) ? res.data : []
    }
  } catch (e) {
    ElMessage.error('获取部门列表失败')
    console.error(e)
  }
}

// 前端过滤（简单够用）
const filteredEmployees = computed(() => {
  const name = (searchForm.value.name || '').trim()
  const gender = (searchForm.value.gender || '').trim()
  const deptId = searchForm.value.departmentId

  return employeesRaw.value.filter(e => {
    if (name && !String(e.name || '').includes(name)) return false
    if (gender && String(e.gender || '') !== gender) return false
    if (deptId != null && deptId !== '' && e.departmentId !== deptId) return false
    return true
  })
})

const pagedEmployees = computed(() => {
  total.value = filteredEmployees.value.length
  const start = (currentPage.value - 1) * pageSize.value
  return filteredEmployees.value.slice(start, start + pageSize.value)
})

const handleSearch = () => {
  currentPage.value = 1
}

const handleCurrentChange = (page) => {
  currentPage.value = page
}

const handleAdd = () => {
  dialogTitle.value = '新增员工'
  form.value = { id: null, name: '', username: '', gender: '男', departmentId: null }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '修改员工'
  // 后端字段是 departmentId（驼峰），不是 department_id
  form.value = {
    id: row.id,
    name: row.name,
    username: row.username || `emp${row.id}`,
    gender: row.gender,
    departmentId: row.departmentId
  }
  dialogVisible.value = true
}

const buildPayload = () => {
  // 不要把 id:'' 发给后端
  const payload = {
    name: form.value.name,
    username: form.value.username,
    gender: form.value.gender,
    departmentId: form.value.departmentId
  }
  return payload
}

const handleSave = async () => {
  try {
    let res
    const payload = buildPayload()

    if (form.value.id != null) {
      res = await axios.put(`/employees/${form.value.id}`, payload)
    } else {
      res = await axios.post('/employees', payload)
    }

    if (res.code === 200) {
      ElMessage.success(res.message || '操作成功')
      dialogVisible.value = false
      await fetchEmployees() // 保存后刷新列表
      currentPage.value = 1
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (e) {
    ElMessage.error('操作失败')
    console.error(e)
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该员工吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await axios.delete(`/employees/${id}`)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      await fetchEmployees()
      // 如果删完当前页没数据，往前翻一页
      const maxPage = Math.max(1, Math.ceil(total.value / pageSize.value))
      currentPage.value = Math.min(currentPage.value, maxPage)
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
      console.error(e)
    }
  }
}
</script>

<style scoped>
.employee-container {
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

.employee-table {
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: center;
}
</style>
