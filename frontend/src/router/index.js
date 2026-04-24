import { createRouter, createWebHistory } from 'vue-router'

// 说明：
// - 首页实际访问的是 /home（你截图就是 /home）
// - / 直接重定向到 /home
// - 所有业务页面都需要登录（requiresAuth: true）

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
      meta: { requiresAuth: false }
    },

    // 根路径直接跳到首页
    {
      path: '/',
      redirect: '/home'
    },

    // 首页
{
  path: '/home',
  name: 'home',
  component: () => import('../views/HomeView.vue'),
  meta: { requiresAuth: true }
},


    // 员工
    {
      path: '/employees',
      name: 'employees',
      component: () => import('../views/EmployeeView.vue'),
      meta: { requiresAuth: true, adminOnly: true }
    },

    // 部门
    {
      path: '/departments',
      name: 'departments',
      component: () => import('../views/DepartmentView.vue'),
      meta: { requiresAuth: true, adminOnly: true }
    },

    {
      path: '/work-records',
      name: 'work-records',
      component: () => import('../views/WorkRecordView.vue'),
      meta: { requiresAuth: true, employeeOnly: true }
    },

    {
      path: '/work-admin',
      name: 'work-admin',
      component: () => import('../views/WorkAdminView.vue'),
      meta: { requiresAuth: true, adminOnly: true }
    },

    {
      path: '/work-projects',
      name: 'work-projects',
      component: () => import('../views/WorkProjectView.vue'),
      meta: { requiresAuth: true, adminOnly: true }
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth) {
    const userText = localStorage.getItem('user')
    if (!userText) {
      next('/login')
      return
    }
    const user = JSON.parse(userText)
    const isAdmin = user?.role === 'ADMIN'
    if (to.meta.adminOnly && !isAdmin) {
      next('/work-records')
      return
    }
    if (to.meta.employeeOnly && isAdmin) {
      next('/work-admin')
      return
    }
    next()
  } else {
    next()
  }
})

export default router
