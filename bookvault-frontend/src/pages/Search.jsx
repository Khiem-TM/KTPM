import { useEffect, useMemo, useState } from 'react'
import bookService from '../services/bookService'
import { Link } from 'react-router-dom'

export default function Search() {
  const [books, setBooks] = useState([])
  const [query, setQuery] = useState('')
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    ;(async () => {
      setLoading(true)
      try {
        const data = await bookService.getBooks()
        setBooks(data || [])
      } finally {
        setLoading(false)
      }
    })()
  }, [])

  const filtered = useMemo(() => {
    const q = query.trim().toLowerCase()
    if (!q) return books
    return books.filter(b => (b.title || '').toLowerCase().includes(q) || (b.author || '').toLowerCase().includes(q))
  }, [books, query])

  return (
    <div style={{ padding: 20 }}>
      <h2>Tìm kiếm sách</h2>
      <input value={query} onChange={e => setQuery(e.target.value)} placeholder="Từ khóa" style={{ width: '100%', padding: 8, marginBottom: 12 }} />
      {loading ? '...' : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(240px, 1fr))', gap: 16 }}>
          {filtered.map(b => (
            <div key={b.id} style={{ border: '1px solid #ddd', borderRadius: 8, padding: 12 }}>
              <h4>{b.title}</h4>
              <div>{b.author}</div>
              <Link to={`/books/${b.id}`}>Chi tiết</Link>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

