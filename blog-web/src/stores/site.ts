import { defineStore } from 'pinia'
import { getSiteSetting, type SiteSettingVO } from '../api/site'

const DEFAULT_REFRESH_INTERVAL_MS = 15_000
const MIN_REFRESH_GAP_MS = 3_000

export const useSiteStore = defineStore('site', {
  state: () => ({
    loading: false,
    loaded: false,
    setting: null as SiteSettingVO | null,

    // realtime sync
    refreshIntervalMs: DEFAULT_REFRESH_INTERVAL_MS,
    lastRefreshAt: 0,
    _timer: null as ReturnType<typeof setInterval> | null,
  }),
  getters: {
    siteName: (s) => s.setting?.siteName || 'My Blog',
    siteNotice: (s) => s.setting?.siteNotice || '',
    seoTitle: (s) => s.setting?.seoTitle || '',
    seoKeywords: (s) => s.setting?.seoKeywords || '',
    seoDescription: (s) => s.setting?.seoDescription || '',
    footerText: (s) => s.setting?.footerText || '',
  },
  actions: {
    async refresh(force = false) {
      const now = Date.now()
      if (!force && now - this.lastRefreshAt < MIN_REFRESH_GAP_MS) return
      if (this.loading) return

      this.loading = true
      try {
        this.setting = await getSiteSetting()
        this.loaded = true
        this.lastRefreshAt = Date.now()
      } finally {
        this.loading = false
      }
    },

    startAutoRefresh(intervalMs?: number) {
      if (typeof intervalMs === 'number' && intervalMs > 0) {
        this.refreshIntervalMs = intervalMs
      }
      if (this._timer) return

      // first refresh immediately
      this.refresh(true)

      this._timer = setInterval(() => {
        // only refresh when tab is visible
        if (typeof document !== 'undefined' && document.visibilityState !== 'visible') return
        this.refresh(false)
      }, this.refreshIntervalMs)

      // refresh when user comes back
      if (typeof window !== 'undefined') {
        window.addEventListener('focus', this._onFocus, { passive: true })
      }
      if (typeof document !== 'undefined') {
        document.addEventListener('visibilitychange', this._onVisibilityChange, { passive: true })
      }
    },

    stopAutoRefresh() {
      if (this._timer) {
        clearInterval(this._timer)
        this._timer = null
      }
      if (typeof window !== 'undefined') {
        window.removeEventListener('focus', this._onFocus)
      }
      if (typeof document !== 'undefined') {
        document.removeEventListener('visibilitychange', this._onVisibilityChange)
      }
    },

    _onFocus() {
      const store = useSiteStore()
      store.refresh(true)
    },

    _onVisibilityChange() {
      if (document.visibilityState === 'visible') {
        const store = useSiteStore()
        store.refresh(true)
      }
    },
  },
})
