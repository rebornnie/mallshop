import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    component: () => import('@/layouts/AdminLayout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/dashboard/index.vue'), meta: { title: '首页概览' } },
      { path: 'pms/product', name: 'ProductList', component: () => import('@/views/product/list.vue'), meta: { title: '商品列表' } },
      { path: 'pms/category', name: 'Category', component: () => import('@/views/product/category.vue'), meta: { title: '分类管理' } },
      { path: 'pms/brand', name: 'Brand', component: () => import('@/views/product/brand.vue'), meta: { title: '品牌管理' } },
      { path: 'pms/review', name: 'ReviewList', component: () => import('@/views/product/review.vue'), meta: { title: '评价管理' } },
      { path: 'oms/order', name: 'OrderList', component: () => import('@/views/order/list.vue'), meta: { title: '订单管理' } },
      { path: 'ums/member', name: 'MemberList', component: () => import('@/views/member/list.vue'), meta: { title: '会员管理' } },
      { path: 'stat/order', name: 'StatOrder', component: () => import('@/views/stat/order.vue'), meta: { title: '订单统计' } },
      { path: 'stat/product', name: 'StatProduct', component: () => import('@/views/stat/product.vue'), meta: { title: '商品统计' } },
      { path: 'stat/user', name: 'StatUser', component: () => import('@/views/stat/user.vue'), meta: { title: '用户统计' } },
      { path: 'report/index', name: 'ReportIndex', component: () => import('@/views/report/index.vue'), meta: { title: '销售报表' } },
      { path: 'report/export', name: 'ReportExport', component: () => import('@/views/report/export.vue'), meta: { title: '订单导出' } },
      { path: 'sms/coupon', name: 'CouponList', component: () => import('@/views/coupon/list.vue'), meta: { title: '优惠券管理' } },
      { path: 'sys/admin', name: 'SysAdmin', component: () => import('@/views/system/admin.vue'), meta: { title: '管理员管理' } },
      { path: 'sys/role', name: 'SysRole', component: () => import('@/views/system/role.vue'), meta: { title: '角色管理' } },
      { path: 'sys/menu', name: 'SysMenu', component: () => import('@/views/system/menu.vue'), meta: { title: '菜单管理' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (!to.meta.public && !userStore.isLoggedIn()) {
    next('/login')
  } else {
    next()
  }
})

export default router
