<template>
  <div style="padding: 16px; max-width: 900px; margin: 0 auto">
    <h2 style="margin-bottom: 12px">{{ site.siteName }}</h2>

    <div style="display: flex; gap: 12px; align-items: center; margin-bottom: 12px">
      <el-input v-model="keyword" placeholder="Search title..." style="max-width: 240px" clearable />
      <el-button type="primary" @click="fetchList">Search</el-button>
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
          </div>
          <div style="color: #555">{{ p.summary }}</div>
        </div>
        <div style="min-width: 80px; color: #888; text-align: right">Views: {{ p.viewCount ?? 0 }}</div>
      </div>
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
import { onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useSiteStore } from '../stores/site'
import { usePostsStore } from '../stores/posts'

const site = useSiteStore()
const posts = usePostsStore()
const { loading, list, total, pageNum, pageSize, keyword } = storeToRefs(posts)

async function fetchList() {
  await posts.fetch()
}

onMounted(async () => {
  await site.refresh()
  await fetchList()
})
</script>
