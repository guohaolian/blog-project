<template>
  <div>
    <div class="app-topbar" :class="{ 'is-scrolled': topbarScrolled }">
      <div class="app-topbar__main" style="border-bottom: 1px solid var(--el-border-color); padding: 10px 16px">
        <div class="container app-header">
          <div style="font-weight: 700">{{ site.siteName }}</div>
          <div class="app-nav">
            <el-button text @click="$router.push('/')">Home</el-button>
            <el-button text @click="$router.push('/archives')">Archives</el-button>
            <el-button text @click="$router.push('/categories')">Categories</el-button>
            <el-button text @click="$router.push('/tags')">Tags</el-button>
            <el-button text @click="$router.push('/about')">About</el-button>
            <el-button text @click="$router.push('/links')">Links</el-button>
          </div>
        </div>
      </div>

      <div
        v-if="site.siteNotice"
        class="app-topbar__notice"
        style="border-bottom: 1px solid var(--el-border-color); padding: 8px 16px"
      >
        <div class="container" style="color: var(--el-text-color-regular)">
          {{ site.siteNotice }}
        </div>
      </div>
    </div>

    <div class="app-page">
      <router-view />
    </div>

    <BackToTopFab :target="backToTopTarget" />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onBeforeUnmount, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import BackToTopFab from './components/BackToTopFab.vue'
import { useSiteStore } from './stores/site'

const site = useSiteStore()
const route = useRoute()

const backToTopTarget = computed(() => {
  // PostDetailView uses an inner scroll container
  if (String(route.name || '').toLowerCase().includes('post')) return '.post-detail__main'
  if (String(route.path || '').startsWith('/post/')) return '.post-detail__main'
  return ''
})

const topbarScrolled = ref(false)
const SCROLL_CHECK_PX = 4
const innerScrollEl = ref<HTMLElement | null>(null)

function updateTopbarScrolled() {
  const winTop = Number(document.documentElement.scrollTop || window.scrollY || 0)
  const innerTop = innerScrollEl.value ? innerScrollEl.value.scrollTop : 0
  topbarScrolled.value = (winTop > SCROLL_CHECK_PX) || (innerTop > SCROLL_CHECK_PX)
}

function updateTopbarHeightVar() {
  const topbar = document.querySelector('.app-topbar') as HTMLElement | null
  if (!topbar) return
  const h = Math.ceil(topbar.getBoundingClientRect().height)
  document.documentElement.style.setProperty('--app-topbar-h', `${h}px`)
}

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

function detachInnerScrollListener() {
  if (innerScrollEl.value) {
    innerScrollEl.value.removeEventListener('scroll', updateTopbarScrolled)
    innerScrollEl.value = null
  }
}

function attachInnerScrollListener() {
  detachInnerScrollListener()
  // PostDetailView (our only inner-scroll page right now)
  const inner = document.querySelector('.post-detail__main') as HTMLElement | null
  if (!inner) return
  innerScrollEl.value = inner
  innerScrollEl.value.addEventListener('scroll', updateTopbarScrolled, { passive: true })
}

onMounted(async () => {
  // realtime refresh: keep site setting in sync (default 15s)
  site.startAutoRefresh()
  await refreshAndApplySeo()

  // ensure layout calculations (e.g. PostDetailView scroll shell) use accurate topbar height
  updateTopbarHeightVar()
  window.addEventListener('resize', updateTopbarHeightVar)

  updateTopbarScrolled()
  window.addEventListener('scroll', updateTopbarScrolled, { passive: true })
  // best-effort: attach listener for inner scroll containers (post detail)
  setTimeout(() => {
    attachInnerScrollListener()
    updateTopbarScrolled()
  }, 0)
})

// notice / sitename may change height (wrap lines); keep CSS var in sync
watch(
  () => [site.siteNotice, site.siteName],
  () => {
    // wait DOM update
    setTimeout(() => {
      updateTopbarHeightVar()
      updateTopbarScrolled()
    }, 0)
  },
)

watch(
  () => route.fullPath,
  () => {
    // route change may change scroll container
    setTimeout(() => {
      attachInnerScrollListener()
      updateTopbarScrolled()
    }, 0)
  },
)

onBeforeUnmount(() => {
  site.stopAutoRefresh()
  window.removeEventListener('resize', updateTopbarHeightVar)
  window.removeEventListener('scroll', updateTopbarScrolled)
  detachInnerScrollListener()
})
</script>

<style scoped>
.app-topbar {
  position: sticky;
  top: 0;
  z-index: 2000;
  /* wrapper background: keep subtle transparency even at top */
  background: rgba(7, 54, 101, 0.35);
  transition: background-color 160ms ease, box-shadow 160ms ease;
}

/* blur (frosted glass) when the topbar is in scrolled state */
.app-topbar.is-scrolled {
  background: rgba(7, 54, 101, 0.35);
  backdrop-filter: saturate(1.4) blur(14px);
  -webkit-backdrop-filter: saturate(1.4) blur(14px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.10);
}

@supports not ((backdrop-filter: blur(1px)) or (-webkit-backdrop-filter: blur(1px))) {
  .app-topbar.is-scrolled {
    background: rgba(7, 54, 101, 0.35);
  }
}

.app-topbar__main {
  /* nav background with transparency */
  background: rgba(170, 190, 211, 0.95);
}

.app-topbar__notice {
  /* notice background: slightly tinted + transparent */
  background: rgba(171, 197, 236, 0.35);
}

.app-page {
  /* make sure fixed/sticky topbar doesn't overlap page content */
  padding-top: 0;
}

.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.app-nav {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: flex-end;
}
</style>
