import axios, { AxiosError } from 'axios'
import { ElMessage } from 'element-plus'

export const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || '/api',
  timeout: 10_000,
})

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
    // Expect: { code, message, data, timestamp }
    if (body && typeof body.code === 'number') {
      if (body.code === 0) return body.data
      return Promise.reject(new Error(body.message || 'Request failed'))
    }
    return body
  },
  (err: AxiosError) => {
    const data: any = err.response?.data
    if (data && typeof data.code === 'number') {
      return Promise.reject(new Error(data.message || 'Request failed'))
    }

    if (err.code === 'ECONNABORTED') {
      toastNetworkOnce('Request timeout')
      return Promise.reject(new Error('Request timeout'))
    }

    const msg = err.message || 'Network error'
    toastNetworkOnce(msg)
    return Promise.reject(new Error(msg))
  },
)
