import { defineStore } from 'pinia'

export type ThemeMode = 'light' | 'dark' | 'auto'

const STORAGE_KEY = 'blog-web:theme'
const STORAGE_WINDOW_KEY = 'blog-web:theme:auto-window'

function getSystemPref(): Exclude<ThemeMode, 'auto'> {
  if (typeof window === 'undefined' || typeof window.matchMedia !== 'function') return 'light'
  return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light'
}

function readStoredMode(): ThemeMode | null {
  if (typeof localStorage === 'undefined') return null
  const v = localStorage.getItem(STORAGE_KEY)
  return v === 'dark' || v === 'light' || v === 'auto' ? v : null
}

export type AutoThemeWindow = {
  start: string // HH:mm, inclusive
  end: string   // HH:mm, exclusive
}

const DEFAULT_AUTO_WINDOW: AutoThemeWindow = { start: '19:00', end: '07:00' }

function readStoredWindow(): AutoThemeWindow {
  if (typeof localStorage === 'undefined') return DEFAULT_AUTO_WINDOW
  const raw = localStorage.getItem(STORAGE_WINDOW_KEY)
  try {
    const v = raw ? JSON.parse(raw) : null
    if (!v || typeof v !== 'object') return DEFAULT_AUTO_WINDOW
    const start = String((v as any).start || '')
    const end = String((v as any).end || '')
    if (!/^\d{2}:\d{2}$/.test(start) || !/^\d{2}:\d{2}$/.test(end)) return DEFAULT_AUTO_WINDOW
    return { start, end }
  } catch {
    return DEFAULT_AUTO_WINDOW
  }
}

function parseHmToMinutes(hm: string): number {
  const parts = (hm || '').split(':')
  const h = Number(parts[0])
  const m = Number(parts[1])
  const hh = Number.isFinite(h) ? h : 0
  const mm = Number.isFinite(m) ? m : 0
  return Math.max(0, Math.min(23, hh)) * 60 + Math.max(0, Math.min(59, mm))
}

function isNowInWindow(now: Date, win: AutoThemeWindow): boolean {
  const mins = now.getHours() * 60 + now.getMinutes()
  const start = parseHmToMinutes(win.start)
  const end = parseHmToMinutes(win.end)
  if (start === end) return true // whole day
  if (start < end) return mins >= start && mins < end
  // crosses midnight
  return mins >= start || mins < end
}

function nextBoundaryMs(now: Date, win: AutoThemeWindow): number {
  // calculate the next time the ideal theme (dark/light) may flip
  const start = parseHmToMinutes(win.start)
  const end = parseHmToMinutes(win.end)

  const nextFor = (targetMins: number) => {
    const base = new Date(now)
    base.setSeconds(0, 0)
    base.setHours(0, 0, 0, 0)
    const t = new Date(base.getTime() + targetMins * 60 * 1000)
    if (t <= now) t.setDate(t.getDate() + 1)
    return t
  }

  // If currently in dark window, next boundary is end; otherwise start.
  const inWin = isNowInWindow(now, win)
  const target = inWin ? end : start
  const t = nextFor(target)
  // add a small delay to ensure boundary is passed
  return Math.max(1000, t.getTime() - now.getTime() + 1000)
}

export const useThemeStore = defineStore('theme', {
  state: () => ({
    mode: (readStoredMode() || getSystemPref()) as ThemeMode,
    autoWindow: readStoredWindow() as AutoThemeWindow,
    _autoTimer: null as ReturnType<typeof setTimeout> | null,
  }),
  getters: {
    resolvedMode: (s): Exclude<ThemeMode, 'auto'> => {
      if (s.mode === 'auto') {
        return isNowInWindow(new Date(), s.autoWindow) ? 'dark' : 'light'
      }
      return s.mode
    },
    isDark: (s) => (s.mode === 'auto' ? isNowInWindow(new Date(), s.autoWindow) : s.mode === 'dark'),
  },
  actions: {
    setMode(mode: ThemeMode) {
      this.mode = mode
      try {
        localStorage.setItem(STORAGE_KEY, mode)
      } catch {
        // ignore
      }
      this.applyToDom()
      this._scheduleAuto()
    },
    setAutoWindow(win: AutoThemeWindow) {
      this.autoWindow = win
      try {
        localStorage.setItem(STORAGE_WINDOW_KEY, JSON.stringify(win))
      } catch {
        // ignore
      }
      this.applyToDom()
      this._scheduleAuto()
    },
    toggle() {
      // toggle resolved theme; keep current mode type
      const cur = this.resolvedMode
      this.setMode(cur === 'dark' ? 'light' : 'dark')
    },
    applyToDom() {
      if (typeof document === 'undefined') return

      // Element Plus uses the `dark` class on <html> for dark variables.
      const root = document.documentElement
      root.classList.toggle('dark', this.resolvedMode === 'dark')

      // Hint the browser for built-in controls/scrollbars.
      root.style.colorScheme = this.resolvedMode
    },
    init() {
      // Call once at app startup
      this.applyToDom()

      // Optional: if user hasn't stored preference, follow system changes.
      this._scheduleAuto()

      // If no stored mode => default to system preference changes.
      const stored = readStoredMode()
      if (!stored && typeof window !== 'undefined' && typeof window.matchMedia === 'function') {
        const mq = window.matchMedia('(prefers-color-scheme: dark)')
        const handler = () => {
          // only follow system if user still hasn't explicitly set mode
          if (this.mode === 'auto') return
          if (readStoredMode()) return
          this.setMode(mq.matches ? 'dark' : 'light')
        }

        try {
          mq.addEventListener('change', handler)
        } catch {
          mq.addListener(handler)
        }
      }
    },

    _scheduleAuto() {
      if (this._autoTimer) {
        clearTimeout(this._autoTimer)
        this._autoTimer = null
      }
      if (this.mode !== 'auto') return
      const ms = nextBoundaryMs(new Date(), this.autoWindow)
      this._autoTimer = setTimeout(() => {
        this.applyToDom()
        this._scheduleAuto()
      }, ms)
    },
  },
})
