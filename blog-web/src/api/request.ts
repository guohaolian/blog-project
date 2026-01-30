import axios from 'axios'

export const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || '/api',
  timeout: 10_000,
})

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
  (err) => Promise.reject(err),
)
