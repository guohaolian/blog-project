import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const LoginView = () => import('../views/LoginView.vue')
const AdminLayout = () => import('../layouts/AdminLayout.vue')
const AdminHomeView = () => import('../views/AdminHomeView.vue')
const PostsView = () => import('../views/PostsView.vue')
const PostEditView = () => import('../views/PostEditView.vue')
const CategoriesView = () => import('../views/CategoriesView.vue')
const TagsView = () => import('../views/TagsView.vue')
const CommentsView = () => import('../views/CommentsView.vue')
const SettingsView = () => import('../views/SettingsView.vue')

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
      component: AdminLayout,
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'admin-home',
          component: AdminHomeView,
        },
        {
          path: 'posts',
          name: 'posts',
          component: PostsView,
        },
        {
          path: 'posts/new',
          name: 'post-new',
          component: PostEditView,
        },
        {
          path: 'posts/:id/edit',
          name: 'post-edit',
          component: PostEditView,
        },
        {
          path: 'categories',
          name: 'categories',
          component: CategoriesView,
        },
        {
          path: 'tags',
          name: 'tags',
          component: TagsView,
        },
        {
          path: 'comments',
          name: 'comments',
          component: CommentsView,
        },
        { path: 'settings', name: 'settings', component: SettingsView },
      ],
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
