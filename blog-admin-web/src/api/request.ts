import axios, { AxiosError } from 'axios'
import { useAuthStore } from '../stores/auth'
import { ElMessage } from 'element-plus'
import { router } from '../router'

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

function handleUnauthorizedOnce(message?: string) {
  const auth = useAuthStore()
  // only notify when the user previously had a token
  if (auth.token) {
    ElMessage.warning(message || '登录已过期，请重新登录')
  }
  auth.clearToken()

  // Force redirect immediately (route guards only run on navigation).
  const redirect = router.currentRoute.value.fullPath
  if (router.currentRoute.value.path !== '/login') {
    router.replace({ path: '/login', query: { redirect } })
  }
}

let lastNetToastAt = 0
function toastNetworkOnce(message: string) {
  const now = Date.now()
  if (now - lastNetToastAt < 1500) return
  lastNetToastAt = now
  ElMessage.error(message)
}

request.interceptors.response.use(
  (resp) => {
    const body = resp.data
    if (body && typeof body.code === 'number') {
      if (body.code === 0) return body.data
      if (body.code === 40100) {
        handleUnauthorizedOnce(body.message)
      }
      return Promise.reject(new Error(body.message || 'Request failed'))
    }
    return body
  },
  (err: AxiosError) => {
    const data: any = err.response?.data
    if (data && typeof data.code === 'number') {
      if (data.code === 40100) {
        handleUnauthorizedOnce(data.message)
      }
      return Promise.reject(new Error(data.message || 'Request failed'))
    }

    // Timeout
    if (err.code === 'ECONNABORTED') {
      toastNetworkOnce('请求超时，请稍后重试')
      return Promise.reject(new Error('Request timeout'))
    }

    // True network errors (offline/CORS/DNS/connection refused): no response
    if (!err.response) {
      const msg = err.message || 'Network error'
      toastNetworkOnce(msg)
      return Promise.reject(new Error(msg))
    }

    // HTTP error without standard ApiResponse
    const status = err.response.status
    const msg = `HTTP ${status}`
    toastNetworkOnce(msg)
    return Promise.reject(new Error(msg))
  },
)
