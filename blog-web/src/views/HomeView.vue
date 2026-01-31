<template>
  <div class="home">
    <div class="container">
      <div class="home__grid">
        <!-- main list -->
        <div>
          <el-card class="home__search" shadow="never">
            <div class="home__searchRow">
              <el-input v-model="keyword" placeholder="Search title..." clearable @keyup.enter="fetchList" />
              <el-button type="primary" @click="fetchList">Search</el-button>
              <el-button @click="$router.push({ path: '/search', query: { q: keyword || undefined } })">Advanced</el-button>
            </div>
          </el-card>

          <el-card v-for="p in list" :key="p.id" class="home__post" v-loading="loading">
            <div class="home__postRow">
              <router-link v-if="p.coverUrl" :to="`/post/${p.id}`" class="home__cover">
                <img :src="p.coverUrl" alt="cover" loading="lazy" />
              </router-link>

              <div class="home__postMain">
                <router-link :to="`/post/${p.id}`" class="home__postTitle">
                  {{ p.title }}
                </router-link>
                <div class="home__postMeta">
                  <span v-if="p.publishedAt">{{ p.publishedAt }}</span>
                  <span v-if="p.category"> · {{ p.category.name }}</span>
                </div>
                <div class="home__postSummary">{{ p.summary }}</div>
              </div>

              <div class="home__postRight">Views: {{ p.viewCount ?? 0 }}</div>
            </div>
          </el-card>

          <el-empty v-if="!loading && list.length === 0" description="No posts" />

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
import { onMounted, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { useSiteStore } from '../stores/site'
import { usePostsStore } from '../stores/posts'
import { getHotPosts, type HotPostVO } from '../api/posts'
import { getCategories, getTags, type CategoryVO, type TagVO } from '../api/meta'

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

onMounted(async () => {
  await site.refresh()
  await fetchList()
  await loadHot()
  await loadMeta()
})
</script>

<style scoped>
.home {
  padding: 18px 16px 32px;
}

.home__grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 280px;
  gap: 14px;
  align-items: start;
}

@media (max-width: 980px) {
  .home__grid {
    grid-template-columns: 1fr;
  }
}

.home__search {
  margin-bottom: 12px;
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

.home__post {
  margin-bottom: 12px;
  /* make the card feel less cramped */
  padding: 4px 0;
}

.home__postRow {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  align-items: flex-start;
  min-height: 190px;
}

.home__postMain {
  min-width: 0;
  flex: 1 1 auto;
}

.home__postTitle {
  display: inline-block;
  font-size: 18px;
  font-weight: 700;
  color: var(--el-text-color-primary);
}

.home__postMeta {
  color: var(--el-text-color-secondary);
  margin: 6px 0;
}

.home__postSummary {
  color: var(--el-text-color-regular);
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.home__postRight {
  min-width: 90px;
  text-align: right;
  color: var(--el-text-color-secondary);
  white-space: nowrap;
  flex: 0 0 auto;
}

.home__pager {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}

.home__side {
  position: sticky;
  top: 12px;
}

@media (max-width: 980px) {
  .home__side {
    position: static;
  }
}

.home__sideHeader {
  display: flex;
  align-items: center;
  justify-content: space-between;
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
  border-radius: 6px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  background: var(--el-fill-color-light);
  color: var(--el-text-color-secondary);
}

.home__hotTitle {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: var(--el-text-color-primary);
}

.home__hotViews {
  color: var(--el-text-color-secondary);
  white-space: nowrap;
}

.home__cover {
  width: 280px;
  height: 180px;
  flex: 0 0 auto;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid var(--el-border-color);
  background: var(--el-fill-color-light);
}

.home__cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

@media (max-width: 560px) {
  .home__cover {
    width: 120px;
    height: 84px;
  }

  .home__postRight {
    min-width: 68px;
  }
}

.home__widget {
  margin-top: 12px;
}

.home__chipList {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
</style>
