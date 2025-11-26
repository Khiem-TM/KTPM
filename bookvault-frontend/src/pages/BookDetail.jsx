import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import bookService from '../services/bookService'
import borrowingService from '../services/borrowingService'
import favouriteService from '../services/favouriteService'
import ratingService from '../services/ratingService'
import { useAuth } from '../context/AuthContext'

export default function BookDetail() {
  const { id } = useParams()
  const [book, setBook] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [points, setPoints] = useState(5)
  const [comment, setComment] = useState('')
  const { isAuthenticated } = useAuth()

  useEffect(() => {
    (async () => {
      setLoading(true)
      try {
        const data = await bookService.getBook(id)
        setBook(data)
        setError('')
      } catch (e) {
        setError(e?.response?.data?.message || 'Failed to load book')
      } finally {
        setLoading(false)
      }
    })()
  }, [id])

  const borrow = async () => {
    try {
      await borrowingService.createLoan({ bookId: Number(id), quantity: 1 })
    } catch {}
  }

  const addFav = async () => {
    try {
      await favouriteService.addFavourite({ userId: 1, bookId: Number(id) })
    } catch {}
  }

  const rate = async () => {
    try {
      await ratingService.rate({ bookId: Number(id), userId: 1, points, comment })
    } catch {}
  }

  if (!book) return <div style={{ padding: 20 }}>{loading ? '...' : error || 'Không tìm thấy sách'}</div>
  return (
    <div style={{ padding: 20 }}>
      <h2>{book.title}</h2>
      <div>{book.author}</div>
      <div>ISBN: {book.isbn}</div>
      <div>Số lượng: {book.quantity}</div>
      <div style={{ marginTop: 12 }}>
        <button onClick={borrow} style={{ marginRight: 8 }}>Mượn</button>
        {isAuthenticated && <button onClick={addFav}>Yêu thích</button>}
      </div>
      <div style={{ marginTop: 16 }}>
        <h4>Đánh giá</h4>
        <input type="number" min={1} max={10} value={points} onChange={e => setPoints(Number(e.target.value))} style={{ width: 80, padding: 6, marginRight: 8 }} />
        <input value={comment} onChange={e => setComment(e.target.value)} placeholder="Nhận xét" style={{ width: '50%', padding: 6, marginRight: 8 }} />
        <button onClick={rate}>Gửi</button>
      </div>
    </div>
  )
}
