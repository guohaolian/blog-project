import { defineStore } from 'pinia'
import {
  DEFAULT_AUTO_WINDOW,
  type AutoThemeWindow,
  isNowInWindow,
  nextBoundaryMs,
} from '../utils/autoThemeWindow'

export type ThemeMode = 'light' | 'dark' | 'auto'

const STORAGE_KEY = 'blog-admin-web:theme'
const STORAGE_WINDOW_KEY = 'blog-admin-web:theme:auto-window'

function readStoredMode(): Exclude<ThemeMode, 'auto'> | null {
  if (typeof localStorage === 'undefined') return null
  const v = localStorage.getItem(STORAGE_KEY)
  return v === 'dark' || v === 'light' ? v : null
}

function writeStoredMode(mode: Exclude<ThemeMode, 'auto'>) {
  if (typeof localStorage === 'undefined') return
  try {
    localStorage.setItem(STORAGE_KEY, mode)
  } catch {
    // ignore
  }
}

function clearStoredMode() {
  if (typeof localStorage === 'undefined') return
  try {
    localStorage.removeItem(STORAGE_KEY)
  } catch {
    // ignore
  }
}

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

function writeStoredWindow(win: AutoThemeWindow) {
  if (typeof localStorage === 'undefined') return
  try {
    localStorage.setItem(STORAGE_WINDOW_KEY, JSON.stringify(win))
  } catch {
    // ignore
  }
}

export const useThemeStore = defineStore('theme', {
  state: () => ({
    mode: ('auto' as ThemeMode),
    autoWindow: readStoredWindow(),
    _autoTimer: null as ReturnType<typeof setTimeout> | null,
  }),
  getters: {
    resolvedMode: (s): Exclude<ThemeMode, 'auto'> => {
      if (s.mode === 'auto') return isNowInWindow(new Date(), s.autoWindow) ? 'dark' : 'light'
      return s.mode
    },
    isDark: (s) => (s.mode === 'auto' ? isNowInWindow(new Date(), s.autoWindow) : s.mode === 'dark'),
  },
  actions: {
    setMode(mode: ThemeMode) {
      this.mode = mode
      if (mode === 'auto') clearStoredMode()
      else writeStoredMode(mode)

      this.applyToDom()
      this._scheduleAuto()
    },
    setAutoWindow(win: AutoThemeWindow) {
      this.autoWindow = win
      writeStoredWindow(win)
      this.applyToDom()
      this._scheduleAuto()
    },
    toggle() {
      const cur = this.resolvedMode
      this.setMode(cur === 'dark' ? 'light' : 'dark')
    },
    applyToDom() {
      if (typeof document === 'undefined') return
      const root = document.documentElement
      root.classList.toggle('dark', this.resolvedMode === 'dark')
      root.style.colorScheme = this.resolvedMode
    },
    init() {
      const stored = readStoredMode()
      if (stored) this.mode = stored

      this.applyToDom()
      this._scheduleAuto()
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
