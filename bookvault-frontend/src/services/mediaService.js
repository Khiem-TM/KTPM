import http from './http'
import { API } from '../config/api'

const mediaService = {
  upload: async file => {
    const form = new FormData()
    form.append('file', file)
    const res = await http.post(`${API.MEDIA}/upload`, form, { headers: { 'Content-Type': 'multipart/form-data' } })
    return res.data
  }
}

export default mediaService

