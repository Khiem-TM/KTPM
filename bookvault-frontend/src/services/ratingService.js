import http from './http'
import { API } from '../config/api'

const ratingService = {
  list: async ({ bookId, page = 0, size = 10 }) => {
    const res = await http.get(`${API.CATALOG}/books/${bookId}/ratings`, { params: { page, size } })
    return res.data
  },
  rate: async ({ bookId, userId, points, comment }) => {
    const res = await http.post(`${API.CATALOG}/books/${bookId}/ratings`, { userId, points, comment })
    return res.data
  }
}

export default ratingService
