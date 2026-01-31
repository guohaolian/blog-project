<template>
  <div class="container" style="padding: 16px 0">
    <div style="display: flex; align-items: center; justify-content: space-between; gap: 12px; margin-bottom: 12px">
      <h2 style="margin: 0">Search</h2>
      <el-button text @click="$router.push('/')">Home</el-button>
    </div>

    <div style="display: flex; gap: 12px; align-items: center; margin-bottom: 12px">
      <el-input
        v-model="keyword"
        placeholder="Search title..."
        clearable
        style="max-width: 320px"
        @keyup.enter="onSearch"
      />
      <el-button type="primary" :disabled="!keyword.trim()" @click="onSearch">Search</el-button>
    </div>

    <el-card v-for="p in list" :key="p.id" style="margin-bottom: 12px" v-loading="loading">
      <div style="display: flex; justify-content: space-between; gap: 12px">
        <div>
          <router-link :to="`/post/${p.id}`" style="font-size: 18px; font-weight: 600; text-decoration: none">
            {{ p.title }}
          </router-link>
          <div style="color: #888; margin: 6px 0">
            <span v-if="p.publishedAt">{{ p.publishedAt }}</span>
            <span v-if="p.category"> · {{ p.category.name }}</span>
            <span v-if="(p.tags?.length || 0) > 0"> · Tags: {{ (p.tags || []).map(t => t.name).join(', ') }}</span>
          </div>
          <div style="color: #555">{{ p.summary }}</div>
        </div>
        <div style="min-width: 80px; color: #888; text-align: right">Views: {{ p.viewCount ?? 0 }}</div>
      </div>
    </el-card>

    <el-empty v-if="!loading && list.length === 0" description="No results" />

    <div style="margin-top: 12px; display: flex; justify-content: flex-end">
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
