import { createRouter, createWebHistory } from 'vue-router'

const HomeView = () => import('../views/HomeView.vue')
const PostDetailView = () => import('../views/PostDetailView.vue')
const CategoriesView = () => import('../views/CategoriesView.vue')
const CategoryPostsView = () => import('../views/CategoryPostsView.vue')
const TagsView = () => import('../views/TagsView.vue')
const TagPostsView = () => import('../views/TagPostsView.vue')
const AboutView = () => import('../views/AboutView.vue')
const LinksView = () => import('../views/LinksView.vue')
const NotFoundView = () => import('../views/NotFoundView.vue')
const SearchView = () => import('../views/SearchView.vue')
const ArchivesView = () => import('../views/ArchivesView.vue')

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: { title: 'Home' },
    },
    {
      path: '/post/:id',
      name: 'post-detail',
      component: PostDetailView,
      meta: { title: 'Post' },
    },
    {
      path: '/categories',
      name: 'categories',
      component: CategoriesView,
      meta: { title: 'Categories' },
    },
    {
      path: '/category/:id',
      name: 'category-posts',
      component: CategoryPostsView,
      meta: { title: 'Category' },
    },
    {
      path: '/tags',
      name: 'tags',
      component: TagsView,
      meta: { title: 'Tags' },
    },
    {
      path: '/tag/:id',
      name: 'tag-posts',
      component: TagPostsView,
      meta: { title: 'Tag' },
    },
    {
      path: '/about',
      name: 'about',
      component: AboutView,
      meta: { title: 'About' },
    },
    {
      path: '/links',
      name: 'links',
      component: LinksView,
      meta: { title: 'Links' },
    },
    {
      path: '/search',
      name: 'search',
      component: SearchView,
      meta: { title: 'Search' },
    },
    {
      path: '/archives',
      name: 'archives',
      component: ArchivesView,
      meta: { title: 'Archives' },
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: NotFoundView,
      meta: { title: 'Not Found' },
    },
  ],
})

function setTitle(routeTitle: string, siteName?: string) {
  const name = (siteName || '').trim() || 'My Blog'
  const page = (routeTitle || '').trim()
  document.title = page ? `${page} - ${name}` : name
}

router.afterEach((to) => {
  const routeTitle = (to.meta?.title as string) || (typeof to.name === 'string' ? to.name : '')
  // no pinia here; use a default site name and let App.vue overwrite once site setting is loaded
  setTitle(routeTitle, 'My Blog')
})
