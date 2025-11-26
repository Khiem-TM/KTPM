import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Navbar() {
  const { isAuthenticated, roles, logout } = useAuth()
  const isAdmin = roles?.includes('ROLE_ADMIN')
  return (
    <nav className="navbar">
      <div className="nav-left">
        <Link to="/">BookVault</Link>
        <Link to="/books">Books</Link>
        <Link to="/search">Search</Link>
        {isAuthenticated && <Link to="/profile">Profile</Link>}
        {isAuthenticated && <Link to="/favourites">Favourites</Link>}
        {isAuthenticated && <Link to="/borrowed">Borrowed</Link>}
        {isAuthenticated && <Link to="/history">History</Link>}
        {isAuthenticated && <Link to="/chat">Chat</Link>}
        {isAdmin && <Link to="/admin">Admin</Link>}
      </div>
      <div className="nav-right">
        {isAuthenticated ? (
          <button onClick={logout}>Logout</button>
        ) : (
          <>
            <Link to="/login">Login</Link>
            <Link to="/register">Register</Link>
          </>
        )}
      </div>
    </nav>
  )
}

