<template>
  <div class="container" style="padding: 16px 0">
    <div style="display: flex; align-items: center; justify-content: space-between; gap: 12px; margin-bottom: 12px">
      <h2 style="margin: 0">Category Posts</h2>
      <el-button text @click="$router.push('/categories')">All Categories</el-button>
    </div>

    <div style="color:#888;font-size:12px;margin:-6px 0 10px">
      Page {{ pageNum }} / Total {{ total }}
    </div>

    <el-card v-for="p in list" :key="p.id" style="margin-bottom: 12px" v-loading="loading">
      <router-link :to="`/post/${p.id}`" style="font-size: 18px; font-weight: 600; text-decoration: none">
        {{ p.title }}
      </router-link>
      <div style="color: #888; margin: 6px 0">
        <span v-if="p.publishedAt">{{ p.publishedAt }}</span>
        <span v-if="p.category"> · {{ p.category.name }}</span>
      </div>
      <div style="color: #555">{{ p.summary }}</div>
    </el-card>

    <el-empty v-if="!loading && list.length === 0" description="No posts" />

    <div style="margin-top: 12px; display: flex; justify-content: flex-end">
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
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getPosts, type PostListItemVO } from '../api/posts'

const route = useRoute()

const loading = ref(false)
const list = ref<PostListItemVO[]>([])
const total = ref(0)

const pageNum = ref(1)
const pageSize = ref(10)

function onPageChange(p: number) {
  pageNum.value = p
  fetchList()
}

function onPageSizeChange(ps: number) {
  pageSize.value = ps
  pageNum.value = 1
  fetchList()
}

async function fetchList() {
  loading.value = true
  try {
    const categoryId = Number(route.params.id)
    const res = await getPosts({ pageNum: pageNum.value, pageSize: pageSize.value, categoryId })
    list.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

onMounted(fetchList)

watch(
  () => route.params.id,
  () => {
    pageNum.value = 1
    fetchList()
  },
)
</script>
