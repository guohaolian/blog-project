<template>
  <div>
    <div style="border-bottom: 1px solid var(--el-border-color); padding: 10px 16px">
      <div style="max-width: 900px; margin: 0 auto; display: flex; align-items: center; justify-content: space-between; gap: 12px">
        <div style="font-weight: 700">{{ site.siteName }}</div>
        <div style="display: flex; gap: 8px">
          <el-button text @click="$router.push('/')">Home</el-button>
          <el-button text @click="$router.push('/categories')">Categories</el-button>
          <el-button text @click="$router.push('/tags')">Tags</el-button>
          <el-button text @click="$router.push('/about')">About</el-button>
          <el-button text @click="$router.push('/links')">Links</el-button>
        </div>
      </div>
    </div>

    <div v-if="site.siteNotice" style="border-bottom: 1px solid var(--el-border-color); padding: 8px 16px; background: var(--el-fill-color-light)">
      <div style="max-width: 900px; margin: 0 auto; color: var(--el-text-color-regular)">
        {{ site.siteNotice }}
      </div>
    </div>

    <router-view />
  </div>
</template>

<script setup lang="ts">
import { onMounted, onBeforeUnmount } from 'vue'
import { useSiteStore } from './stores/site'

const site = useSiteStore()

async function refreshAndApplySeo() {
  await site.refresh(true)

  // very basic meta tags (optional, but cheap and useful)
  const ensureMeta = (name: string) => {
    let el = document.querySelector(`meta[name="${name}"]`) as HTMLMetaElement | null
    if (!el) {
      el = document.createElement('meta')
      el.setAttribute('name', name)
      document.head.appendChild(el)
    }
    return el
  }

  if (site.seoKeywords) ensureMeta('keywords').setAttribute('content', site.seoKeywords)
  if (site.seoDescription) ensureMeta('description').setAttribute('content', site.seoDescription)
}

onMounted(async () => {
  // realtime refresh: keep site setting in sync (default 15s)
  site.startAutoRefresh()
  await refreshAndApplySeo()
})

onBeforeUnmount(() => {
  site.stopAutoRefresh()
})
</script>
