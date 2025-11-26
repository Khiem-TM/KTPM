import axios from 'axios'
import authService from './authService'

const http = axios.create()

http.interceptors.request.use(config => {
  const token = authService.getToken()
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

export default http
