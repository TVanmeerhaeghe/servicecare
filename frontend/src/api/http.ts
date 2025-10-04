import axios from 'axios'

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || '',
  withCredentials: false,
})

export function setAuthToken(token: string | null) {
  if (token) {
    api.defaults.headers.common['Authorization'] = `Bearer ${token}`
  } else {
    delete api.defaults.headers.common['Authorization']
  }
}

let onUnauthorized: (() => void) | null = null
export function setOnUnauthorizedHandler(fn: (() => void) | null) {
  onUnauthorized = fn
}

api.interceptors.response.use(
  (res) => res,
  (error) => {
    const status = error?.response?.status
    if (status === 401) {
      if (onUnauthorized) {
        try { onUnauthorized() } catch (e) { }
      }
    }
    return Promise.reject(error)
  }
)

export default api
