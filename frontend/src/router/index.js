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
      meta: { requiresAuth: true }
    },

    // 部门
    {
      path: '/departments',
      name: 'departments',
      component: () => import('../views/DepartmentView.vue'),
      meta: { requiresAuth: true }
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth) {
    const user = localStorage.getItem('user')
    if (user) next()
    else next('/login')
  } else {
    next()
  }
})

export default router
