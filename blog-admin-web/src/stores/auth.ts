import { defineStore } from 'pinia'
import type { AdminMeResp } from '../api/adminAuth'
import { adminLogin, adminMe } from '../api/adminAuth'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('BLOG_ADMIN_TOKEN') || '',
    user: null as AdminMeResp | null,
  }),
  getters: {
    isAuthed: (state) => !!state.token,
  },
  actions: {
    setToken(token: string) {
      this.token = token
      localStorage.setItem('BLOG_ADMIN_TOKEN', token)
    },
    clearToken() {
      this.token = ''
      this.user = null
      localStorage.removeItem('BLOG_ADMIN_TOKEN')
    },

    async login(username: string, password: string) {
      const resp = await adminLogin({ username, password })
      this.setToken(resp.token)
      return resp
    },

    async fetchMe() {
      if (!this.token) {
        this.user = null
        return null
      }
      const me = await adminMe()
      this.user = me
      return me
    },

    logout() {
      this.clearToken()
    },
  },
})
