import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function ProtectedRoute({ roles }) {
  const { isAuthenticated, roles: userRoles } = useAuth()
  if (!isAuthenticated) return <Navigate to="/login" replace />
  if (roles && roles.length > 0) {
    const ok = userRoles?.some(r => roles.includes(r))
    if (!ok) return <Navigate to="/" replace />
  }
  return <Outlet />
}

