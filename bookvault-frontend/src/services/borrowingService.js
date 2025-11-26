import http from './http'
import { API } from '../config/api'

const borrowingService = {
  createLoan: async ({ bookId, quantity }) => {
    const res = await http.post(`${API.BORROWING}/loans`, { bookId, quantity })
    return res.data
  },
  returnLoan: async id => {
    const res = await http.post(`${API.BORROWING}/loans/${id}/return`)
    return res.data
  }
}

export default borrowingService

