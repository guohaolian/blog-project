import { createRouter, createWebHistory } from 'vue-router'

const HomeView = () => import('../views/HomeView.vue')
const PostDetailView = () => import('../views/PostDetailView.vue')
const CategoriesView = () => import('../views/CategoriesView.vue')
const CategoryPostsView = () => import('../views/CategoryPostsView.vue')
const TagsView = () => import('../views/TagsView.vue')
const TagPostsView = () => import('../views/TagPostsView.vue')

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/post/:id',
      name: 'post-detail',
      component: PostDetailView,
    },
    {
      path: '/categories',
      name: 'categories',
      component: CategoriesView,
    },
    {
      path: '/category/:id',
      name: 'category-posts',
      component: CategoryPostsView,
    },
    {
      path: '/tags',
      name: 'tags',
      component: TagsView,
    },
    {
      path: '/tag/:id',
      name: 'tag-posts',
      component: TagPostsView,
    },
  ],
})
