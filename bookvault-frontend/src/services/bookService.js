import http from './http';
import { API } from '../config/api';

const getAuthHeaders = () => {
  const token = localStorage.getItem('token');
  return token ? { 'Authorization': `Bearer ${token}` } : {};
};

const bookService = {
  getBooks: async () => {
    const response = await http.get(`${API.CATALOG}/books`, {
      headers: getAuthHeaders()
    });
    return response.data;
  },

  getBook: async (id) => {
    const response = await http.get(`${API.CATALOG}/books/${id}`, {
      headers: getAuthHeaders()
    });
    return response.data;
  },

  createBook: async (book) => {
    const response = await http.post(`${API.CATALOG}/books`, book, {
      headers: {
        ...getAuthHeaders(),
        'Content-Type': 'application/json'
      }
    });
    return response.data;
  },

  updateBook: async (id, book) => {
    const response = await http.put(`${API.CATALOG}/books/${id}`, book, {
      headers: {
        ...getAuthHeaders(),
        'Content-Type': 'application/json'
      }
    });
    return response.data;
  },

  deleteBook: async (id) => {
    await http.delete(`${API.CATALOG}/books/${id}`, {
      headers: getAuthHeaders()
    });
  }
};

export default bookService;
