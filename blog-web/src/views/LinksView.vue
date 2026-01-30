<template>
  <div style="padding: 16px; max-width: 900px; margin: 0 auto">
    <h2 style="margin: 0 0 12px">Links</h2>

    <el-card v-loading="loading">
      <el-empty v-if="!loading && links.length === 0" description="No links" />
      <el-table v-else :data="links" size="small" style="width: 100%">
        <el-table-column prop="name" label="Name" width="200" />
        <el-table-column label="URL">
          <template #default="{ row }">
            <a :href="row.url" target="_blank" rel="noreferrer">{{ row.url }}</a>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getSiteSetting, parseLinksJson, type SiteLinkItem } from '../api/site'

const loading = ref(false)
const links = ref<SiteLinkItem[]>([])

onMounted(async () => {
  loading.value = true
  try {
    const s = await getSiteSetting()
    links.value = parseLinksJson(s.linksJson)
  } finally {
    loading.value = false
  }
})
</script>
