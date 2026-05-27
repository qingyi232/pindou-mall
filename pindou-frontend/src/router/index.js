import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    children: [
      { path: '', component: () => import('../views/Home.vue') },
      { path: 'products', component: () => import('../views/Products.vue') },
      { path: 'product/:id', component: () => import('../views/ProductDetail.vue') },
      { path: 'cart', component: () => import('../views/Cart.vue'), meta: { auth: true } },
      { path: 'orders', component: () => import('../views/Orders.vue'), meta: { auth: true } },
      { path: 'profile', component: () => import('../views/Profile.vue'), meta: { auth: true } },
      { path: 'checkout', component: () => import('../views/Checkout.vue'), meta: { auth: true } }
    ]
  },
  { path: '/login', component: () => import('../views/Login.vue') },
  { path: '/register', component: () => import('../views/Register.vue') },
  {
    path: '/admin',
    component: () => import('../views/admin/AdminLayout.vue'),
    meta: { auth: true, admin: true },
    children: [
      { path: '', component: () => import('../views/admin/Dashboard.vue') },
      { path: 'products', component: () => import('../views/admin/ProductManage.vue') },
      { path: 'categories', component: () => import('../views/admin/CategoryManage.vue') },
      { path: 'orders', component: () => import('../views/admin/OrderManage.vue') },
      { path: 'users', component: () => import('../views/admin/UserManage.vue') },
      { path: 'banners', component: () => import('../views/admin/BannerManage.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const user = JSON.parse(localStorage.getItem('user') || 'null')

  if (to.meta.auth && !token) {
    next('/login')
  } else if (to.meta.admin && user?.role !== 'ADMIN') {
    next('/')
  } else {
    next()
  }
})

export default router
