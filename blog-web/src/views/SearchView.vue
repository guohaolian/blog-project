<template>
  <div class="container search">
    <div class="search__header">
      <h2 class="search__title">Search</h2>
      <el-button text class="search__homeBtn" @click="$router.push('/')">Home</el-button>
    </div>

    <div class="search__bar">
      <el-input
        v-model="keyword"
        placeholder="Search title..."
        clearable
        class="search__input"
        @keyup.enter="onSearch"
      />
      <el-button type="primary" :disabled="!keyword.trim()" @click="onSearch">Search</el-button>
    </div>

    <el-card v-for="p in list" :key="p.id" class="search__card" v-loading="loading">
      <div class="search-item">
        <div class="search-item__main">
          <router-link :to="`/post/${p.id}`" class="search-item__title">
            {{ p.title }}
          </router-link>
          <div class="search-item__meta">
            <span v-if="p.publishedAt">{{ p.publishedAt }}</span>
            <span v-if="p.category"> · {{ p.category.name }}</span>
            <span v-if="(p.tags?.length || 0) > 0"> · Tags: {{ (p.tags || []).map(t => t.name).join(', ') }}</span>
          </div>
          <div class="search-item__summary">{{ p.summary }}</div>
        </div>
        <div class="search-item__views">Views: {{ p.viewCount ?? 0 }}</div>
      </div>
    </el-card>

    <el-empty v-if="!loading && list.length === 0" description="No results" />

    <div class="search__pager">
      <el-pagination
        background
        layout="prev, pager, next"
        :current-page="pageNum"
        :page-size="pageSize"
        :total="total"
        @current-change="(p:number) => { pageNum = p; fetchList() }"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPosts, type PostListItemVO } from '../api/posts'

const route = useRoute()
const router = useRouter()

const keyword = ref('')
const pageNum = ref(1)
const pageSize = ref(10)

const loading = ref(false)
const list = ref<PostListItemVO[]>([])
const total = ref(0)

function syncFromRoute() {
  const q = route.query.q
  keyword.value = typeof q === 'string' ? q : ''
  pageNum.value = 1
}

function syncToRoute() {
  router.replace({
    path: '/search',
    query: {
      q: keyword.value.trim() || undefined,
    },
  })
}

async function fetchList() {
  const q = keyword.value.trim()
  if (!q) {
    list.value = []
    total.value = 0
    return
  }

  loading.value = true
  try {
    const res = await getPosts({ pageNum: pageNum.value, pageSize: pageSize.value, keyword: q })
    list.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function onSearch() {
  pageNum.value = 1
  syncToRoute()
  fetchList()
}

onMounted(() => {
  syncFromRoute()
  fetchList()
})

watch(
  () => route.query,
  () => {
    syncFromRoute()
    fetchList()
  },
)
</script>

<style scoped>
.search {
  padding: 16px 0;
}

.search__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.search__title {
  margin: 0;
}

.search__bar {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 12px;
}

.search__input {
  max-width: 320px;
}

.search__card {
  margin-bottom: 12px;
}

.search-item {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.search-item__main {
  min-width: 0;
}

.search-item__title {
  font-size: 18px;
  font-weight: 700;
  text-decoration: none;
}

.search-item__meta {
  color: var(--el-text-color-secondary);
  margin: 6px 0;
}

.search-item__summary {
  color: var(--el-text-color-regular);
}

.search-item__views {
  min-width: 80px;
  color: var(--el-text-color-secondary);
  text-align: right;
  white-space: nowrap;
}

.search__pager {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 560px) {
  .search__bar {
    flex-direction: column;
    align-items: stretch;
  }

  .search__input {
    max-width: none;
    width: 100%;
  }

  .search-item {
    flex-direction: column;
  }

  .search-item__views {
    text-align: left;
  }

  .search__pager {
    justify-content: center;
  }
}
</style>
