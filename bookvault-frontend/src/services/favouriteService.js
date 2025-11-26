import http from './http'
import { API } from '../config/api'

const favouriteService = {
  list: async ({ userId, page = 0, size = 10 }) => {
    const res = await http.get(`${API.IAM}/favourites`, { params: { userId, page, size } })
    return res.data
  },
  addFavourite: async ({ userId, bookId }) => {
    const res = await http.post(`${API.IAM}/favourites`, { userId, bookId })
    return res.data
  },
  removeFavourite: async ({ userId, bookId }) => {
    const res = await http.delete(`${API.IAM}/favourites/${bookId}`, { params: { userId } })
    return res.data
  }
}

export default favouriteService
