import { createContext, useContext, useEffect, useMemo, useState } from 'react'
import { jwtDecode } from 'jwt-decode'
import authService from '../services/authService'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [token, setToken] = useState(null)
  const [roles, setRoles] = useState([])

  useEffect(() => {
    const t = authService.getToken()
    if (t) {
      setToken(t)
      try {
        const payload = jwtDecode(t)
        const r = payload?.roles || payload?.authorities || []
        setRoles(Array.isArray(r) ? r : [r].filter(Boolean))
      } catch {}
    }
  }, [])

  const login = async (username, password) => {
    const data = await authService.login(username, password)
    const t = data?.accessToken || authService.getToken()
    if (t) {
      setToken(t)
      try {
        const payload = jwtDecode(t)
        const r = payload?.roles || payload?.authorities || []
        setRoles(Array.isArray(r) ? r : [r].filter(Boolean))
      } catch {}
    }
    return data
  }

  const register = async (username, email, password) => {
    const data = await authService.register(username, email, password)
    const t = data?.accessToken || authService.getToken()
    if (t) {
      setToken(t)
      try {
        const payload = jwtDecode(t)
        const r = payload?.roles || payload?.authorities || []
        setRoles(Array.isArray(r) ? r : [r].filter(Boolean))
      } catch {}
    }
    return data
  }

  const logout = () => {
    authService.logout()
    setToken(null)
    setRoles([])
  }

  const value = useMemo(() => ({ token, roles, login, register, logout, isAuthenticated: !!token }), [token, roles])
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  return useContext(AuthContext)
}

