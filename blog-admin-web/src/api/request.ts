import axios from 'axios'
import { useAuthStore } from '../stores/auth'

export const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || '/api',
  timeout: 10_000,
})

request.interceptors.request.use((config) => {
  const auth = useAuthStore()
  if (auth.token) {
    config.headers = config.headers || {}
    config.headers.Authorization = `Bearer ${auth.token}`
  }
  return config
})

request.interceptors.response.use(
  (resp) => {
    const body = resp.data
    if (body && typeof body.code === 'number') {
      if (body.code === 0) return body.data
      if (body.code === 40100) {
        const auth = useAuthStore()
        auth.clearToken()
      }
      return Promise.reject(new Error(body.message || 'Request failed'))
    }
    return body
  },
  (err) => Promise.reject(err),
)
