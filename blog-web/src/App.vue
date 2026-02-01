<template>
  <div>
    <div class="app-topbar" :class="{ 'is-scrolled': topbarScrolled }">
      <div class="app-topbar__main">
        <div class="container app-header">
          <div class="app-brand" @click="$router.push('/')">
            <div class="app-brand__name">{{ site.siteName }}</div>
          </div>
          <div class="app-nav">
            <el-button text class="app-nav__btn" @click="$router.push('/')">Home</el-button>
            <el-button text class="app-nav__btn" @click="$router.push('/archives')">Archives</el-button>
            <el-button text class="app-nav__btn" @click="$router.push('/categories')">Categories</el-button>
            <el-button text class="app-nav__btn" @click="$router.push('/tags')">Tags</el-button>
            <el-button text class="app-nav__btn" @click="$router.push('/about')">About</el-button>
            <el-button text class="app-nav__btn" @click="$router.push('/links')">Links</el-button>

            <div class="app-nav__spacer" />
            <el-tooltip content="Theme" placement="bottom">
              <div class="app-theme">
                <el-segmented
                  v-model="theme.mode"
                  :options="themeOptions"
                  size="small"
                  @change="(v: any) => theme.setMode(v)"
                />

                <div v-if="theme.mode === 'auto'" class="app-theme__window">
                  <el-tooltip content="Dark window start (HH:mm)" placement="bottom">
                    <el-input
                      v-model="autoStart"
                      size="small"
                      class="app-theme__time"
                      placeholder="19:00"
                      maxlength="5"
                      @blur="applyAutoWindow"
                      @keyup.enter="applyAutoWindow"
                    />
                  </el-tooltip>
                  <span class="app-theme__sep">~</span>
                  <el-tooltip content="Dark window end (HH:mm)" placement="bottom">
                    <el-input
                      v-model="autoEnd"
                      size="small"
                      class="app-theme__time"
                      placeholder="07:00"
                      maxlength="5"
                      @blur="applyAutoWindow"
                      @keyup.enter="applyAutoWindow"
                    />
                  </el-tooltip>
                </div>
              </div>
            </el-tooltip>
          </div>
        </div>
      </div>

      <div v-if="site.siteNotice" class="app-topbar__notice">
        <div class="container app-notice">
          {{ site.siteNotice }}
        </div>
      </div>
    </div>

    <div class="app-page">
      <router-view v-slot="{ Component }">
        <transition name="page" mode="out-in">
          <component :is="Component" :key="route.fullPath" />
        </transition>
      </router-view>
    </div>

    <BackToTopFab :target="backToTopTarget" />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onBeforeUnmount, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import BackToTopFab from './components/BackToTopFab.vue'
import { useSiteStore } from './stores/site'
import { useThemeStore } from './stores/theme'
import { Moon, Sunny, Monitor } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const site = useSiteStore()
const theme = useThemeStore()
const route = useRoute()

const themeOptions = [
  { label: 'Light', value: 'light', showsIcon: true, icon: Sunny },
  { label: 'Auto', value: 'auto', showsIcon: true, icon: Monitor },
  { label: 'Dark', value: 'dark', showsIcon: true, icon: Moon },
]

const autoStart = ref(theme.autoWindow.start)
const autoEnd = ref(theme.autoWindow.end)

function applyAutoWindow() {
  const start = (autoStart.value || '').trim()
  const end = (autoEnd.value || '').trim()
  if (!/^\d{2}:\d{2}$/.test(start) || !/^\d{2}:\d{2}$/.test(end)) {
    ElMessage.warning('Time format should be HH:mm (e.g. 19:00)')
    autoStart.value = theme.autoWindow.start
    autoEnd.value = theme.autoWindow.end
    return
  }
  theme.setAutoWindow({ start, end })
}

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
  z-index: 10;
}

.app-topbar__main {
  border-bottom: 1px solid var(--el-border-color);
  padding: 10px 0;
  background: rgba(255, 255, 255, 0.70);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  transition: box-shadow 160ms ease, background 160ms ease;
}

html.dark .app-topbar__main {
  background: rgba(18, 20, 24, 0.70);
}

.app-topbar.is-scrolled .app-topbar__main {
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.08);
}

.app-topbar__notice {
  border-bottom: 1px solid var(--el-border-color);
  padding: 8px 0;
  background: rgba(255, 255, 255, 0.62);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

html.dark .app-topbar__notice {
  background: rgba(18, 20, 24, 0.62);
}

.app-notice {
  color: var(--el-text-color-regular);
  font-size: 13px;
}

.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
}

.app-brand {
  display: flex;
  align-items: center;
  cursor: pointer;
  user-select: none;
}

.app-brand__name {
  font-weight: 800;
  letter-spacing: -0.02em;
  font-size: 16px;
  line-height: 1;
}

.app-nav {
  display: flex;
  align-items: center;
  gap: 2px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.app-nav__btn {
  border-radius: 10px;
}

.app-nav__spacer {
  width: 10px;
}

.app-theme {
  display: flex;
  align-items: center;
  gap: 8px;
}

.app-theme__window {
  display: flex;
  align-items: center;
  gap: 6px;
}

.app-theme__time {
  width: 74px;
}

.app-theme__sep {
  opacity: 0.7;
}

.app-page {
  padding-top: 14px;
}

@media (max-width: 760px) {
  .app-nav {
    width: 100%;
    justify-content: flex-start;
  }
  .app-header {
    align-items: flex-start;
  }
}
</style>
