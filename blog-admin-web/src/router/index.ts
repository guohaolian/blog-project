import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const LoginView = () => import('../views/LoginView.vue')
const AdminHomeView = () => import('../views/AdminHomeView.vue')
const PostsView = () => import('../views/PostsView.vue')
const PostEditView = () => import('../views/PostEditView.vue')

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/admin' },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/admin',
      name: 'admin',
      component: AdminHomeView,
      meta: { requiresAuth: true },
    },
    {
      path: '/posts',
      name: 'posts',
      component: PostsView,
      meta: { requiresAuth: true },
    },
    {
      path: '/posts/new',
      name: 'post-new',
      component: PostEditView,
      meta: { requiresAuth: true },
    },
    {
      path: '/posts/:id/edit',
      name: 'post-edit',
      component: PostEditView,
      meta: { requiresAuth: true },
    },
  ],
})

router.beforeEach(async (to) => {
  const auth = useAuthStore()

  if (to.meta.requiresAuth) {
    if (!auth.token) {
      return { path: '/login', query: { redirect: to.fullPath } }
    }

    // Token exists, but user not loaded yet: try to validate token.
    if (!auth.user) {
      try {
        await auth.fetchMe()
      } catch {
        auth.clearToken()
        return { path: '/login', query: { redirect: to.fullPath } }
      }
    }
  }

  if (to.path === '/login' && auth.token) {
    return { path: '/admin' }
  }

  return true
})
