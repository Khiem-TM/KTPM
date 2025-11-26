import { useState } from 'react'
import { useAuth } from '../context/AuthContext'
import { useNavigate } from 'react-router-dom'

export default function Login() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const onSubmit = async e => {
    e.preventDefault()
    setLoading(true)
    try {
      await login(username, password)
      navigate('/books')
    } catch (err) {
      setError(err?.response?.data?.message || 'Login failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div style={{ padding: 20, maxWidth: 400 }}>
      <h2>Đăng nhập</h2>
      {error && <div style={{ color: '#c62828' }}>{error}</div>}
      <form onSubmit={onSubmit}>
        <input value={username} onChange={e => setUsername(e.target.value)} placeholder="Username" style={{ width: '100%', padding: 8, marginBottom: 10 }} />
        <input type="password" value={password} onChange={e => setPassword(e.target.value)} placeholder="Password" style={{ width: '100%', padding: 8, marginBottom: 10 }} />
        <button type="submit" disabled={loading} style={{ width: '100%', padding: 10 }}>{loading ? '...' : 'Login'}</button>
      </form>
    </div>
  )
}

