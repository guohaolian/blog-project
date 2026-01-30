<template>
  <div style="padding: 16px; max-width: 900px; margin: 0 auto">
    <h2 style="margin: 0 0 12px">Tags</h2>

    <el-card v-loading="loading">
      <el-empty v-if="!loading && list.length === 0" description="No tags" />
      <el-space v-else wrap>
        <el-button v-for="t in list" :key="t.id" @click="$router.push(`/tag/${t.id}`)">
          {{ t.name }}
        </el-button>
      </el-space>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getTags, type TagVO } from '../api/meta'

const loading = ref(false)
const list = ref<TagVO[]>([])

onMounted(async () => {
  loading.value = true
  try {
    list.value = await getTags()
  } finally {
    loading.value = false
  }
})
</script>
