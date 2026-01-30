<template>
  <div style="padding: 16px; max-width: 900px; margin: 0 auto">
    <div style="display: flex; align-items: center; justify-content: space-between; gap: 12px; margin-bottom: 12px">
      <h2 style="margin: 0">Tag Posts</h2>
      <el-button text @click="$router.push('/tags')">All Tags</el-button>
    </div>

    <el-card v-for="p in list" :key="p.id" style="margin-bottom: 12px" v-loading="loading">
      <router-link :to="`/post/${p.id}`" style="font-size: 18px; font-weight: 600; text-decoration: none">
        {{ p.title }}
      </router-link>
      <div style="color: #888; margin: 6px 0">
        <span v-if="p.publishedAt">{{ p.publishedAt }}</span>
        <span v-if="p.category"> · {{ p.category.name }}</span>
        <span v-if="(p.tags?.length || 0) > 0"> · Tags: {{ (p.tags || []).map(t => t.name).join(', ') }}</span>
      </div>
      <div style="color: #555">{{ p.summary }}</div>
    </el-card>

    <el-empty v-if="!loading && list.length === 0" description="No posts" />

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
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { getPosts, type PostListItemVO } from '../api/posts'

const route = useRoute()

const loading = ref(false)
const list = ref<PostListItemVO[]>([])
const total = ref(0)

const pageNum = ref(1)
const pageSize = ref(10)

async function fetchList() {
  loading.value = true
  try {
    const tagId = Number(route.params.id)
    const res = await getPosts({ pageNum: pageNum.value, pageSize: pageSize.value, tagId })
    list.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

onMounted(fetchList)
</script>
