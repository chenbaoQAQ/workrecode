const api = require('../../utils/request')
const { demoEmployee } = require('../../config')

const platform = typeof dd !== 'undefined' ? dd : my

function formatDate(date) {
  const year = date.getFullYear()
  const month = `${date.getMonth() + 1}`.padStart(2, '0')
  const day = `${date.getDate()}`.padStart(2, '0')
  return `${year}-${month}-${day}`
}

function toFixedWeight(value) {
  return Number(value || 0).toFixed(2)
}

Page({
  data: {
    today: formatDate(new Date()),
    minDate: formatDate(new Date(Date.now() - 7 * 24 * 60 * 60 * 1000)),
    projects: [],
    projectNames: [],
    projectIndex: -1,
    selectedProjectName: '请选择项目',
    records: [],
    totalWeight: '0.00',
    remainingWeight: '1.00',
    pendingCount: 0,
    saving: false,
    form: {
      workDate: formatDate(new Date()),
      workHours: '1.00',
      remark: ''
    }
  },

  onLoad() {
    this.fetchData()
  },

  onPullDownRefresh() {
    this.fetchData().finally(() => platform.stopPullDownRefresh && platform.stopPullDownRefresh())
  },

  async fetchData() {
    try {
      const workDate = this.data.form.workDate || this.data.today
      const [projectRes, recordRes] = await Promise.all([
        api.get('/work-projects'),
        api.get('/work-records', {
          employeeId: demoEmployee.id,
          startDate: workDate,
          endDate: workDate
        })
      ])

      const projects = ((projectRes && projectRes.data) || []).filter((item) => item.enabled)
      const records = ((recordRes && recordRes.data) || []).map((item) => this.decorateRecord(item))
      const effectiveRecords = records.filter((item) => item.status !== 'REJECTED')
      const total = effectiveRecords.reduce((sum, item) => sum + Number(item.workHours || 0), 0)

      this.setData({
        projects,
        projectNames: projects.map((item) => item.name),
        records,
        totalWeight: toFixedWeight(total),
        remainingWeight: toFixedWeight(Math.max(1 - total, 0)),
        pendingCount: records.filter((item) => item.status === 'PENDING').length
      })
    } catch (err) {
      platform.showToast({ type: 'fail', content: '数据加载失败，请检查后端地址' })
    }
  },

  decorateRecord(record) {
    const map = {
      APPROVED: { text: '已通过', className: 'status-approved' },
      PENDING: { text: '待审批', className: 'status-pending' },
      REJECTED: { text: '已驳回', className: 'status-rejected' }
    }
    const status = map[record.status] || { text: record.status || '未知', className: 'status-pending' }
    return {
      ...record,
      statusText: status.text,
      statusClass: status.className
    }
  },

  onDateChange(event) {
    this.setData({
      'form.workDate': event.detail.value
    })
    this.fetchData()
  },

  onProjectChange(event) {
    const projectIndex = Number(event.detail.value)
    this.setData({
      projectIndex,
      selectedProjectName: this.data.projectNames[projectIndex] || '请选择项目'
    })
  },

  onWeightInput(event) {
    this.setData({
      'form.workHours': event.detail.value
    })
  },

  onRemarkInput(event) {
    this.setData({
      'form.remark': event.detail.value
    })
  },

  minusWeight() {
    const next = Math.max(Number(this.data.form.workHours || 0) - 0.1, 0.1)
    this.setData({ 'form.workHours': toFixedWeight(next) })
  },

  plusWeight() {
    const next = Math.min(Number(this.data.form.workHours || 0) + 0.1, 2)
    this.setData({ 'form.workHours': toFixedWeight(next) })
  },

  async submitRecord() {
    const project = this.data.projects[this.data.projectIndex]
    const weight = Number(this.data.form.workHours)

    if (!project) {
      platform.showToast({ type: 'fail', content: '请选择项目' })
      return
    }

    if (!weight || weight <= 0) {
      platform.showToast({ type: 'fail', content: '请填写有效权重' })
      return
    }

    this.setData({ saving: true })
    try {
      const res = await api.post('/work-records', {
        projectId: project.id,
        workContent: project.name,
        employeeId: demoEmployee.id,
        employeeName: demoEmployee.name,
        workDate: this.data.form.workDate,
        workHours: weight,
        remark: this.data.form.remark
      })

      if (res && res.code === 200) {
        platform.showToast({ type: 'success', content: '已提交审批' })
        this.setData({
          projectIndex: -1,
          selectedProjectName: '请选择项目',
          'form.workHours': '1.00',
          'form.remark': ''
        })
        await this.fetchData()
      } else {
        platform.showToast({ type: 'fail', content: (res && res.message) || '提交失败' })
      }
    } catch (err) {
      platform.showToast({ type: 'fail', content: '提交失败，请检查后端' })
    } finally {
      this.setData({ saving: false })
    }
  }
})
