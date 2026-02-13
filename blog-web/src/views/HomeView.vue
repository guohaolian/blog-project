<template>
  <div class="home">
    <div
      v-if="bannerUrl"
      class="home-hero"
      :style="{ backgroundImage: `url(${bannerUrl})` }"
    >
      <div class="home-hero__overlay">
        <div class="container home-hero__inner">
          <div class="home-hero__title">{{ site.siteName }}</div>
          <div v-if="site.siteNotice" class="home-hero__subtitle">{{ site.siteNotice }}</div>
        </div>

        <button class="home-hero__down" type="button" @click="scrollToList" aria-label="Scroll down">
          <span class="home-hero__downIcon">⌄</span>
        </button>
      </div>
    </div>

    <div class="container">
      <div ref="listAnchor" class="home__grid">
        <!-- main list -->
        <div>
          <el-card class="home__search" shadow="never">
            <div class="home__searchRow">
              <el-input v-model="keyword" placeholder="Search title..." clearable @keyup.enter="fetchList" />
              <el-button type="primary" @click="fetchList">Search</el-button>
              <el-button @click="$router.push({ path: '/search', query: { q: keyword || undefined } })">Advanced</el-button>
            </div>
          </el-card>

          <div class="home__list" v-loading="loading">
            <PostListCard
              v-for="p in list"
              :key="p.id"
              :post="p"
              class="home__post"
            />

            <el-empty v-if="!loading && list.length === 0" description="No posts" />
          </div>

          <div class="home__pager">
            <el-pagination
              background
              layout="total, sizes, prev, pager, next"
              :current-page="pageNum"
              :page-size="pageSize"
              :page-sizes="[5, 10, 20, 50]"
              :total="total"
              @current-change="onPageChange"
              @size-change="onPageSizeChange"
            />
          </div>
        </div>

        <!-- sidebar -->
        <div class="home__side">
          <el-card v-loading="hotLoading" shadow="never">
            <template #header>
              <div class="home__sideHeader">
                <span>Hot posts</span>
                <el-button size="small" :loading="hotLoading" @click="loadHot">Refresh</el-button>
              </div>
            </template>

            <el-empty v-if="!hotLoading && hot.length === 0" description="No data" />
            <div v-else class="home__hotList">
              <div v-for="(p, idx) in hot" :key="p.id" class="home__hotItem">
                <div class="home__hotLeft">
                  <span class="home__hotRank">{{ idx + 1 }}</span>
                  <router-link :to="`/post/${p.id}`" class="home__hotTitle">
                    {{ p.title }}
                  </router-link>
                </div>
                <span class="home__hotViews">{{ p.viewCount ?? 0 }}</span>
              </div>
            </div>
          </el-card>

          <el-card class="home__widget" v-loading="metaLoading" shadow="never">
            <template #header>
              <div class="home__sideHeader">
                <span>Categories</span>
                <el-button size="small" text @click="$router.push('/categories')">All</el-button>
              </div>
            </template>

            <el-empty v-if="!metaLoading && categories.length === 0" description="No categories" />
            <div v-else class="home__chipList">
              <el-button
                v-for="c in categories"
                :key="c.id"
                size="small"
                @click="$router.push(`/category/${c.id}`)"
              >
                {{ c.name }}
              </el-button>
            </div>
          </el-card>

          <el-card class="home__widget" v-loading="metaLoading" shadow="never">
            <template #header>
              <div class="home__sideHeader">
                <span>Tags</span>
                <el-button size="small" text @click="$router.push('/tags')">All</el-button>
              </div>
            </template>

            <el-empty v-if="!metaLoading && tags.length === 0" description="No tags" />
            <div v-else class="home__chipList">
              <el-button
                v-for="t in tags"
                :key="t.id"
                size="small"
                @click="$router.push(`/tag/${t.id}`)"
              >
                {{ t.name }}
              </el-button>
            </div>
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onBeforeUnmount, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { useSiteStore } from '../stores/site'
import { usePostsStore } from '../stores/posts'
import { getHotPosts, type HotPostVO } from '../api/posts'
import { getCategories, getTags, type CategoryVO, type TagVO } from '../api/meta'
import PostListCard from '../components/PostListCard.vue'

const site = useSiteStore()
const posts = usePostsStore()
const { loading, list, total, pageNum, pageSize, keyword } = storeToRefs(posts)

const hotLoading = ref(false)
const hot = ref<HotPostVO[]>([])

const metaLoading = ref(false)
const categories = ref<CategoryVO[]>([])
const tags = ref<TagVO[]>([])

async function loadHot() {
  hotLoading.value = true
  try {
    hot.value = await getHotPosts({ limit: 10 })
  } finally {
    hotLoading.value = false
  }
}

async function loadMeta() {
  metaLoading.value = true
  try {
    const [cs, ts] = await Promise.all([getCategories(), getTags()])
    categories.value = cs
    tags.value = ts
  } finally {
    metaLoading.value = false
  }
}

async function fetchList() {
  await posts.fetch()
}

function onPageChange(p: number) {
  pageNum.value = p
  fetchList()
}

function onPageSizeChange(ps: number) {
  pageSize.value = ps
  pageNum.value = 1
  fetchList()
}

const bannerUrl = computed(() => site.bannerUrl)
const listAnchor = ref<HTMLElement | null>(null)

function scrollToList() {
  const topbarH = parseFloat(getComputedStyle(document.documentElement).getPropertyValue('--app-topbar-h')) || 0
  const el = listAnchor.value
  if (!el) return
  const y = window.scrollY + el.getBoundingClientRect().top - topbarH - 8
  window.scrollTo({ top: Math.max(0, y), behavior: 'smooth' })
}

function onWheel(e: WheelEvent) {
  if (!bannerUrl.value) return
  if (window.scrollY > 8) return
  if (e.deltaY > 0) {
    // first scroll down from top takes you to list
    scrollToList()
  }
}

onMounted(async () => {
  await site.refresh()
  await fetchList()
  await loadHot()
  await loadMeta()

  window.addEventListener('wheel', onWheel, { passive: true })
})

onBeforeUnmount(() => {
  window.removeEventListener('wheel', onWheel)
})
</script>

<style scoped>
.home {
  padding: 18px 10px 32px;
}

.home-hero {
  /* extend behind the sticky topbar */
  margin-top: calc(-1 * (var(--app-topbar-h, 120px) + 14px));
  height: 100vh;
  min-height: 520px;
  padding-top: calc(var(--app-topbar-h, 120px) + 14px);

  position: relative;
  isolation: isolate;

  border-bottom: 1px solid var(--el-border-color);
  overflow: hidden;
}

.home-hero::before {
  content: '';
  position: absolute;
  inset: 0;
  z-index: -2;
  /* reuse the element's background-image to avoid CSS var warnings */
  background-image: inherit;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  transform: scale(1.02);
}

/* One consistent overlay to avoid visible “seam” near the topbar */
.home-hero::after {
  content: '';
  position: absolute;
  inset: 0;
  z-index: -1;
  background: rgba(0, 0, 0, 0.35);
}

.home-hero__overlay {
  height: 100%;
  position: relative;
  /* remove the gradient that caused banding */
  background: transparent;
}

.home-hero__inner {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 12px;
}

.home-hero__title {
  color: #fff;
  font-size: 44px;
  font-weight: 900;
  letter-spacing: -0.03em;
  text-shadow: 0 10px 30px rgba(0, 0, 0, 0.35);
}

.home-hero__subtitle {
  color: rgba(255, 255, 255, 0.85);
  font-size: 16px;
  max-width: 760px;
  line-height: 1.8;
  text-shadow: 0 10px 24px rgba(0, 0, 0, 0.25);
}

.home-hero__down {
  position: absolute;
  left: 50%;
  bottom: 22px;
  transform: translateX(-50%);
  width: 44px;
  height: 44px;
  border-radius: 999px;
  border: 1px solid rgba(255, 255, 255, 0.35);
  background: rgba(0, 0, 0, 0.25);
  color: #fff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  animation: hero-bounce 1.2s infinite;
}

.home-hero__down:hover {
  background: rgba(0, 0, 0, 0.35);
}

.home-hero__downIcon {
  font-size: 22px;
  line-height: 1;
  transform: translateY(-2px);
}

@keyframes hero-bounce {
  0%, 100% { transform: translateX(-50%) translateY(0); }
  50% { transform: translateX(-50%) translateY(8px); }
}

@media (max-width: 760px) {
  .home-hero__title {
    font-size: 34px;
  }
  .home-hero {
    min-height: 420px;
  }
}

.home__grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 300px;
  gap: 16px;
  align-items: start;
}

@media (max-width: 980px) {
  .home__grid {
    grid-template-columns: 1fr;
  }
}

.home__search {
  margin-bottom: 14px;
  border-radius: var(--app-radius);
}

.home__searchRow {
  display: flex;
  gap: 10px;
  align-items: center;
}

@media (max-width: 560px) {
  .home__searchRow {
    flex-direction: column;
    align-items: stretch;
  }
}

.home__side {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

/* Mobile: only show post cards list (hide hot/categories/tags sidebar) */
@media (max-width: 760px) {
  .home__side {
    display: none;
  }
}

.home__sideHeader {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.home__hotList {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.home__hotItem {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.home__hotLeft {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.home__hotRank {
  width: 22px;
  height: 22px;
  border-radius: 7px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 800;
  background: rgba(64, 158, 255, 0.12);
  color: var(--el-color-primary);
}

.home__hotTitle {
  display: inline-block;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.home__hotViews {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  padding: 4px 8px;
  border-radius: 999px;
  background: rgba(0, 0, 0, 0.04);
}

html.dark .home__hotViews {
  background: rgba(255, 255, 255, 0.08);
}

.home__chipList {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.home__pager {
  margin-top: 14px;
  display: flex;
  justify-content: center;
}

.home__list {
  position: relative;
  min-height: 120px;

  /* Use flex+gap instead of relying on card margin; avoids any margin-collapse/"gray band" artifacts */
  display: flex;
  flex-direction: column;
  gap: 14px;

  /* give list area a subtle, consistent background so gaps don't look like "leaking" */
  padding: 2px;
  border-radius: var(--app-radius);
}

/* Remove the extra bottom margin from card itself in this page; gap takes care of spacing */
.home__post {
  margin-bottom: 0 !important;
}

/* Element Plus loading nodes are injected at runtime; IDE may warn 'unused selector' */
.home__list :deep(.el-loading-mask) {
  background-color: transparent;
}

/* Element Plus loading spinner/text are injected at runtime; keep readable */
.home__list :deep(.el-loading-spinner .circular),
.home__list :deep(.el-loading-spinner .el-loading-text) {
  color: var(--el-color-primary);
}
</style>
