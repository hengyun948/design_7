import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    redirect: '/category',
    children: [
      { path: 'category', component: () => import('../views/Category.vue') },
      { path: 'dish', component: () => import('../views/Dish.vue') },
      { path: 'order', component: () => import('../views/Order.vue') },
      { path: 'review', component: () => import('../views/Review.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
