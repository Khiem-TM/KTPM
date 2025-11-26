import { useAuth } from '../context/AuthContext'
import { jwtDecode } from 'jwt-decode'
import mediaService from '../services/mediaService'

export default function Profile() {
  const { isAuthenticated, roles, logout } = useAuth()
  const token = localStorage.getItem('token')
  const payload = token ? (() => { try { return jwtDecode(token) } catch { return null } })() : null
  if (!isAuthenticated) return null
  const upload = async e => {
    const file = e.target.files?.[0]
    if (!file) return
    try { await mediaService.upload(file) } catch {}
  }
  return (
    <div style={{ padding: 20 }}>
      <h2>Hồ sơ</h2>
      <div>Tên người dùng: {payload?.sub || payload?.username || 'N/A'}</div>
      <div>Roles: {roles.join(', ')}</div>
      <div style={{ marginTop: 12 }}>
        <input type="file" onChange={upload} />
      </div>
      <button onClick={logout} style={{ marginTop: 12 }}>Logout</button>
    </div>
  )
}
