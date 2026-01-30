<template>
  <div style="padding: 16px; max-width: 900px; margin: 0 auto">
    <h2 style="margin: 0 0 12px">About</h2>

    <el-card v-loading="loading">
      <pre style="white-space: pre-wrap; margin: 0">{{ about }}</pre>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getSiteSetting } from '../api/site'

const loading = ref(false)
const about = ref('')

onMounted(async () => {
  loading.value = true
  try {
    const s = await getSiteSetting()
    about.value = s.aboutContent || ''
  } finally {
    loading.value = false
  }
})
</script>
