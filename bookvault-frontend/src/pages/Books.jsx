import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import bookService from '../services/bookService'

export default function Books() {
  const [books, setBooks] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const load = async () => {
    setLoading(true)
    try {
      const data = await bookService.getBooks()
      setBooks(data || [])
      setError('')
    } catch (e) {
      setError(e?.response?.data?.message || 'Failed to load books')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
  }, [])

  return (
    <div style={{ padding: 20 }}>
      <h2>Danh sách sách</h2>
      <button onClick={load} disabled={loading} style={{ padding: 8 }}>{loading ? '...' : 'Refresh'}</button>
      {error && <div style={{ color: '#c62828' }}>{error}</div>}
      {books.length === 0 ? (
        <p>Không có sách</p>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(240px, 1fr))', gap: 16 }}>
          {books.map(b => (
            <div key={b.id} style={{ border: '1px solid #ddd', borderRadius: 8, padding: 12 }}>
              <h4>{b.title}</h4>
              <div>{b.author}</div>
              <div>ISBN: {b.isbn}</div>
              <Link to={`/books/${b.id}`}>Chi tiết</Link>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

