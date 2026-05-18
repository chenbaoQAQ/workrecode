<template>
  <div class="statistics-page">
    <section class="filter-panel">
      <div>
        <h2 class="page-title">统计分析</h2>
        <p class="page-subtitle">按项目、人员、日期和状态组合筛选，查看权重与加班分布</p>
      </div>

      <div class="filter-grid">
        <el-select v-model="filters.project" clearable filterable placeholder="全部项目">
          <el-option
            v-for="item in projectOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
        <el-select v-model="filters.employee" clearable filterable placeholder="全部人员">
          <el-option
            v-for="item in employeeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
        <el-select v-model="filters.workDate" clearable filterable placeholder="全部日期">
          <el-option
            v-for="item in dateOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
        <el-select v-model="filters.status" clearable placeholder="全部状态">
          <el-option label="已通过" value="APPROVED" />
          <el-option label="待审批" value="PENDING" />
          <el-option label="已驳回" value="REJECTED" />
          <el-option label="已撤回" value="CANCELLED" />
        </el-select>
        <el-button @click="resetFilters">重置筛选</el-button>
      </div>
    </section>

    <section class="summary-grid">
      <div class="summary-card">
        <div class="summary-value">{{ projectStats.length }}</div>
        <div class="summary-label">项目数</div>
      </div>
      <div class="summary-card">
        <div class="summary-value">{{ employeeStats.length }}</div>
        <div class="summary-label">参与人员</div>
      </div>
      <div class="summary-card">
        <div class="summary-value">{{ filteredRecords.length }}</div>
        <div class="summary-label">记录数</div>
      </div>
      <div class="summary-card primary">
        <div class="summary-value">{{ formatWeight(summary.totalWorkHours) }}</div>
        <div class="summary-label">总权重</div>
      </div>
      <div class="summary-card warning">
        <div class="summary-value">{{ formatWeight(summary.totalOvertimeHours) }}</div>
        <div class="summary-label">加班权重</div>
      </div>
    </section>

    <section class="chart-grid">
      <div class="chart-panel dark-panel">
        <div class="panel-header">
          <h3>项目权重分布</h3>
          <span>{{ filters.project || '全部项目' }}</span>
        </div>
        <div v-if="projectStats.length" class="bar-list">
          <div v-for="item in projectStats.slice(0, 8)" :key="item.name" class="bar-row">
            <div class="bar-info">
              <strong>{{ item.name }}</strong>
              <span>{{ item.employeeCount }} 人 / {{ formatWeight(item.totalWorkHours) }}</span>
            </div>
            <div class="bar-track">
              <div class="bar-fill" :style="{ width: `${item.percent}%` }" />
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无统计数据" />
      </div>

      <div class="chart-panel">
        <div class="panel-header">
          <h3>日期权重趋势</h3>
          <span>{{ filters.workDate || '全部日期' }}</span>
        </div>
        <div v-if="dateStats.length" class="date-chart">
          <div v-for="item in dateStats" :key="item.date" class="date-column">
            <span class="date-value">{{ formatCompact(item.totalWorkHours) }}</span>
            <div class="date-track">
              <div class="date-fill" :style="{ height: `${item.percent}%` }" />
            </div>
            <span class="date-label">{{ shortDate(item.date) }}</span>
          </div>
        </div>
        <el-empty v-else description="暂无统计数据" />
      </div>
    </section>

    <section class="table-panel">
      <div class="panel-header">
        <h3>筛选结果</h3>
        <el-segmented v-model="activeView" :options="viewOptions" />
      </div>

      <el-table v-if="activeView === 'project'" :data="projectStats" stripe border>
        <el-table-column prop="name" label="项目" min-width="220" />
        <el-table-column prop="employeeCount" label="参与人数" width="100" sortable />
        <el-table-column prop="recordCount" label="记录数" width="90" sortable />
        <el-table-column label="总权重" width="110">
          <template #default="{ row }">{{ formatWeight(row.totalWorkHours) }}</template>
        </el-table-column>
        <el-table-column label="加班权重" width="110">
          <template #default="{ row }">{{ formatWeight(row.totalOvertimeHours) }}</template>
        </el-table-column>
        <el-table-column prop="employeeNames" label="参与人员" min-width="260" show-overflow-tooltip />
        <el-table-column prop="dateRange" label="日期范围" width="190" />
      </el-table>

      <el-table v-else-if="activeView === 'employee'" :data="employeeStats" stripe border>
        <el-table-column prop="name" label="人员" min-width="140" />
        <el-table-column prop="projectCount" label="项目数" width="90" sortable />
        <el-table-column prop="recordCount" label="记录数" width="90" sortable />
        <el-table-column label="总权重" width="110">
          <template #default="{ row }">{{ formatWeight(row.totalWorkHours) }}</template>
        </el-table-column>
        <el-table-column label="加班权重" width="110">
          <template #default="{ row }">{{ formatWeight(row.totalOvertimeHours) }}</template>
        </el-table-column>
        <el-table-column prop="projectNames" label="参与项目" min-width="260" show-overflow-tooltip />
        <el-table-column prop="dateRange" label="日期范围" width="190" />
      </el-table>

      <el-table v-else-if="activeView === 'date'" :data="dateStatsReversed" stripe border>
        <el-table-column prop="date" label="日期" width="130" sortable />
        <el-table-column prop="employeeCount" label="参与人数" width="100" sortable />
        <el-table-column prop="projectCount" label="项目数" width="90" sortable />
        <el-table-column prop="recordCount" label="记录数" width="90" sortable />
        <el-table-column label="总权重" width="110">
          <template #default="{ row }">{{ formatWeight(row.totalWorkHours) }}</template>
        </el-table-column>
        <el-table-column label="加班权重" width="110">
          <template #default="{ row }">{{ formatWeight(row.totalOvertimeHours) }}</template>
        </el-table-column>
        <el-table-column prop="projectNames" label="项目" min-width="240" show-overflow-tooltip />
      </el-table>

      <el-table v-else :data="filteredRecords" stripe border>
        <el-table-column prop="workDate" label="日期" width="120" sortable />
        <el-table-column prop="employeeName" label="人员" width="120" />
        <el-table-column prop="workContent" label="项目" min-width="220" />
        <el-table-column label="权重" width="90">
          <template #default="{ row }">{{ formatWeight(row.workHours) }}</template>
        </el-table-column>
        <el-table-column label="加班" width="90">
          <template #default="{ row }">{{ formatWeight(row.overtimeHours) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="180" show-overflow-tooltip />
      </el-table>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import axios from '../axios'

const records = ref([])
const activeView = ref('project')
const filters = reactive({
  project: '',
  employee: '',
  workDate: '',
  status: 'APPROVED'
})

const viewOptions = [
  { label: '按项目', value: 'project' },
  { label: '按人员', value: 'employee' },
  { label: '按日期', value: 'date' },
  { label: '明细', value: 'detail' }
]

const filteredRecords = computed(() => {
  return records.value.filter((item) => {
    return (!filters.project || projectName(item) === filters.project)
      && (!filters.employee || employeeName(item) === filters.employee)
      && (!filters.workDate || item.workDate === filters.workDate)
      && (!filters.status || item.status === filters.status)
  })
})

const optionBaseRecords = (exceptKey) => {
  return records.value.filter((item) => {
    return (exceptKey === 'project' || !filters.project || projectName(item) === filters.project)
      && (exceptKey === 'employee' || !filters.employee || employeeName(item) === filters.employee)
      && (exceptKey === 'workDate' || !filters.workDate || item.workDate === filters.workDate)
      && (exceptKey === 'status' || !filters.status || item.status === filters.status)
  })
}

const projectOptions = computed(() => toOptions(optionBaseRecords('project').map(projectName)))
const employeeOptions = computed(() => toOptions(optionBaseRecords('employee').map(employeeName)))
const dateOptions = computed(() => toOptions(optionBaseRecords('workDate').map((item) => item.workDate)).reverse())

const summary = computed(() => {
  return filteredRecords.value.reduce((sum, item) => {
    sum.totalWorkHours += Number(item.workHours || 0)
    sum.totalOvertimeHours += Number(item.overtimeHours || 0)
    return sum
  }, { totalWorkHours: 0, totalOvertimeHours: 0 })
})

const projectStats = computed(() => {
  const rows = new Map()
  filteredRecords.value.forEach((item) => {
    const name = projectName(item)
    const row = ensureRow(rows, name, { employees: new Set(), dates: new Set() })
    row.recordCount += 1
    row.totalWorkHours += Number(item.workHours || 0)
    row.totalOvertimeHours += Number(item.overtimeHours || 0)
    row.employees.add(employeeName(item))
    row.dates.add(item.workDate)
  })
  const maxValue = maxTotal(rows)
  return Array.from(rows.values())
    .map((item) => ({
      ...item,
      percent: percent(item.totalWorkHours, maxValue),
      employeeCount: item.employees.size,
      employeeNames: Array.from(item.employees).join('、'),
      dateRange: formatDateRange(item.dates)
    }))
    .sort((a, b) => b.totalWorkHours - a.totalWorkHours)
})

const employeeStats = computed(() => {
  const rows = new Map()
  filteredRecords.value.forEach((item) => {
    const name = employeeName(item)
    const row = ensureRow(rows, name, { projects: new Set(), dates: new Set() })
    row.recordCount += 1
    row.totalWorkHours += Number(item.workHours || 0)
    row.totalOvertimeHours += Number(item.overtimeHours || 0)
    row.projects.add(projectName(item))
    row.dates.add(item.workDate)
  })
  return Array.from(rows.values())
    .map((item) => ({
      ...item,
      projectCount: item.projects.size,
      projectNames: Array.from(item.projects).join('、'),
      dateRange: formatDateRange(item.dates)
    }))
    .sort((a, b) => b.totalWorkHours - a.totalWorkHours)
})

const dateStats = computed(() => {
  const rows = new Map()
  filteredRecords.value.forEach((item) => {
    const date = item.workDate || '未填写日期'
    const row = ensureRow(rows, date, { date, projects: new Set(), employees: new Set() })
    row.recordCount += 1
    row.totalWorkHours += Number(item.workHours || 0)
    row.totalOvertimeHours += Number(item.overtimeHours || 0)
    row.projects.add(projectName(item))
    row.employees.add(employeeName(item))
  })
  const maxValue = maxTotal(rows)
  return Array.from(rows.values())
    .map((item) => ({
      ...item,
      percent: percent(item.totalWorkHours, maxValue),
      projectCount: item.projects.size,
      employeeCount: item.employees.size,
      projectNames: Array.from(item.projects).join('、')
    }))
    .sort((a, b) => String(a.date).localeCompare(String(b.date)))
})

const dateStatsReversed = computed(() => [...dateStats.value].reverse())

const fetchAll = async () => {
  const res = await axios.get('/work-records')
  if (res.code === 200) {
    records.value = Array.isArray(res.data) ? res.data : []
  }
}

const resetFilters = () => {
  filters.project = ''
  filters.employee = ''
  filters.workDate = ''
  filters.status = 'APPROVED'
}

const projectName = (item) => item.projectName || item.workContent || '未命名项目'
const employeeName = (item) => item.employeeName || '未命名人员'
const formatWeight = (value) => Number(value || 0).toFixed(2)
const formatCompact = (value) => Number(value || 0).toFixed(1)
const shortDate = (date) => String(date || '').slice(5) || '-'
const statusText = (status) => ({ APPROVED: '已通过', PENDING: '待审批', REJECTED: '已驳回', CANCELLED: '已撤回' }[status] || status)
const statusType = (status) => ({ APPROVED: 'success', PENDING: 'warning', REJECTED: 'danger', CANCELLED: 'info' }[status] || 'info')

const toOptions = (values) => Array.from(new Set(values.filter(Boolean)))
  .sort((a, b) => String(a).localeCompare(String(b), 'zh-Hans-CN'))
  .map((value) => ({ label: value, value }))

const ensureRow = (rows, name, extra) => {
  if (!rows.has(name)) {
    rows.set(name, {
      name,
      recordCount: 0,
      totalWorkHours: 0,
      totalOvertimeHours: 0,
      ...extra
    })
  }
  return rows.get(name)
}

const maxTotal = (rows) => Math.max(...Array.from(rows.values()).map((item) => item.totalWorkHours), 0)
const percent = (value, maxValue) => maxValue ? Math.max((value / maxValue) * 100, 8) : 0

const formatDateRange = (dates) => {
  const values = Array.from(dates).filter(Boolean).sort()
  if (!values.length) return '-'
  if (values.length === 1) return values[0]
  return `${values[0]} 至 ${values[values.length - 1]}`
}

onMounted(fetchAll)
</script>

<style scoped>
.statistics-page {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.filter-panel,
.table-panel,
.chart-panel {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(15, 23, 42, 0.08);
}

.filter-panel {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20px;
}

.page-title {
  margin: 0;
  font-size: 22px;
  color: #1f2937;
}

.page-subtitle {
  margin: 8px 0 0;
  color: #667085;
}

.filter-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(170px, 1fr));
  gap: 12px;
  min-width: 520px;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(140px, 1fr));
  gap: 14px;
}

.summary-card {
  min-height: 86px;
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 12px rgba(15, 23, 42, 0.08);
  border-top: 3px solid #d8e3f8;
}

.summary-card.primary {
  border-top-color: #2563eb;
}

.summary-card.warning {
  border-top-color: #f97316;
}

.summary-value {
  font-size: 28px;
  font-weight: 700;
  color: #1f3a8a;
  line-height: 1;
}

.summary-label {
  margin-top: 10px;
  color: #667085;
}

.chart-grid {
  display: grid;
  grid-template-columns: minmax(360px, 1fr) minmax(360px, 1fr);
  gap: 18px;
}

.dark-panel {
  background: linear-gradient(135deg, #0f2b63, #123f8f);
  color: #eaf2ff;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.panel-header h3 {
  margin: 0;
  font-size: 17px;
  color: inherit;
}

.panel-header span {
  color: #8a98ad;
  font-size: 13px;
}

.dark-panel .panel-header span {
  color: #bdd3ff;
}

.bar-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.bar-info {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 7px;
  font-size: 13px;
  color: #bdd3ff;
}

.bar-info strong {
  color: #fff;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.bar-track {
  height: 8px;
  background: rgba(255, 255, 255, 0.14);
  border-radius: 8px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #2f80ed, #22d3ee);
  border-radius: 8px;
}

.date-chart {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(42px, 1fr));
  align-items: end;
  gap: 12px;
  min-height: 238px;
  padding-top: 8px;
}

.date-column {
  display: grid;
  grid-template-rows: 22px 180px 20px;
  gap: 6px;
  min-width: 0;
  text-align: center;
}

.date-value {
  color: #46617f;
  font-size: 12px;
}

.date-track {
  display: flex;
  align-items: end;
  justify-content: center;
  height: 180px;
  background: linear-gradient(180deg, #f8fbff, #eef5ff);
  border-radius: 8px;
  overflow: hidden;
}

.date-fill {
  width: 64%;
  min-height: 8px;
  background: linear-gradient(180deg, #2563eb, #22c55e);
  border-radius: 8px 8px 0 0;
}

.date-label {
  color: #667085;
  font-size: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

@media (max-width: 1180px) {
  .filter-panel {
    flex-direction: column;
  }

  .filter-grid {
    min-width: 0;
    width: 100%;
  }

  .summary-grid {
    grid-template-columns: repeat(3, minmax(140px, 1fr));
  }

  .chart-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 760px) {
  .filter-grid,
  .summary-grid {
    grid-template-columns: 1fr;
  }
}
</style>
