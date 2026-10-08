import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    component: () => import('@/layouts/PortalLayout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('@/views/home/index.vue') },
      { path: 'products', name: 'ProductList', component: () => import('@/views/product/list.vue') },
      { path: 'product/:id', name: 'ProductDetail', component: () => import('@/views/product/detail.vue') },
      { path: 'cart', name: 'Cart', component: () => import('@/views/cart/index.vue') },
      { path: 'order-confirm', name: 'OrderConfirm', component: () => import('@/views/order/confirm.vue'), meta: { requiresAuth: true } },
      { path: 'pay/:orderId', name: 'Pay', component: () => import('@/views/pay/index.vue'), meta: { requiresAuth: true } },
      { path: 'orders', name: 'Orders', component: () => import('@/views/order/list.vue'), meta: { requiresAuth: true } },
      { path: 'order/:id', name: 'OrderDetail', component: () => import('@/views/order/detail.vue'), meta: { requiresAuth: true } },
      { path: 'user', name: 'UserCenter', component: () => import('@/views/user/index.vue'), meta: { requiresAuth: true } },
      { path: 'addresses', name: 'Addresses', component: () => import('@/views/user/address.vue'), meta: { requiresAuth: true } },
      { path: 'reviews', name: 'MyReviews', component: () => import('@/views/user/reviews.vue'), meta: { requiresAuth: true } },
      { path: 'messages', name: 'Messages', component: () => import('@/views/user/messages.vue'), meta: { requiresAuth: true } },
      { path: 'coupons', name: 'Coupons', component: () => import('@/views/coupon/index.vue') },
      { path: 'my-coupons', name: 'MyCoupons', component: () => import('@/views/coupon/mine.vue'), meta: { requiresAuth: true } },
      { path: 'seckill', name: 'Seckill', component: () => import('@/views/seckill/index.vue') }
    ]
  },
  { path: '/login', name: 'Login', component: () => import('@/views/auth/login.vue'), meta: { public: true } },
  { path: '/register', name: 'Register', component: () => import('@/views/auth/register.vue'), meta: { public: true } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.meta.requiresAuth && !userStore.isLoggedIn()) {
    next('/login?redirect=' + encodeURIComponent(to.fullPath))
  } else {
    next()
  }
})

export default router
