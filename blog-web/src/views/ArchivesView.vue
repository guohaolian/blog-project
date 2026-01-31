<template>
  <div class="container" style="padding: 16px 0">
    <div style="display: flex; align-items: center; justify-content: space-between; gap: 12px; margin-bottom: 12px">
      <h2 style="margin: 0">Archives</h2>
      <el-button text @click="$router.push('/')">Home</el-button>
    </div>

    <el-card v-loading="loading">
      <el-empty v-if="!loading && groups.length === 0" description="No posts" />

      <div v-else>
        <div v-for="g in groups" :key="g.month" style="margin-bottom: 16px">
          <div style="display: flex; align-items: baseline; justify-content: space-between; gap: 12px">
            <h3 style="margin: 0">{{ g.month }}</h3>
            <span style="color: #888">{{ g.count }} posts</span>
          </div>

          <el-divider style="margin: 8px 0" />

          <div v-for="p in g.posts" :key="p.id" style="padding: 6px 0">
            <router-link :to="`/post/${p.id}`" style="text-decoration: none">
              {{ p.title }}
            </router-link>
            <span v-if="p.publishedAt" style="color: #888; margin-left: 8px">{{ p.publishedAt }}</span>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getArchives, type ArchiveMonthGroupVO } from '../api/archives.ts'

const loading = ref(false)
const groups = ref<ArchiveMonthGroupVO[]>([])

async function fetchArchives() {
  loading.value = true
  try {
    groups.value = await getArchives()
  } finally {
    loading.value = false
  }
}

onMounted(fetchArchives)
</script>
