<template>
  <div class="container" style="padding: 16px 0">
    <h2 style="margin: 0 0 12px">Categories</h2>

    <el-card v-loading="loading">
      <el-empty v-if="!loading && list.length === 0" description="No categories" />
      <el-space v-else wrap>
        <el-button v-for="c in list" :key="c.id" @click="$router.push(`/category/${c.id}`)">
          {{ c.name }}
        </el-button>
      </el-space>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getCategories, type CategoryVO } from '../api/meta'

const loading = ref(false)
const list = ref<CategoryVO[]>([])

onMounted(async () => {
  loading.value = true
  try {
    list.value = await getCategories()
  } finally {
    loading.value = false
  }
})
</script>
