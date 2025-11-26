import './App.css'
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import ProtectedRoute from './routes/ProtectedRoute'
import Navbar from './components/Navbar'
import Home from './pages/Home'
import Login from './pages/Login'
import Register from './pages/Register'
import Books from './pages/Books'
import BookDetail from './pages/BookDetail'
import Search from './pages/Search'
import Profile from './pages/Profile'
import Favourites from './pages/Favourites'
import Borrowed from './pages/Borrowed'
import History from './pages/History'
import Dashboard from './pages/admin/Dashboard'
import AdminUsers from './pages/admin/Users'
import AdminBooks from './pages/admin/Books'
import AdminBorrows from './pages/admin/Borrows'
import AdminNotifications from './pages/admin/Notifications'
import Chat from './pages/Chat'

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Navbar />
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route element={<ProtectedRoute roles={["ROLE_USER", "ROLE_ADMIN"]} />}> 
            <Route path="/books" element={<Books />} />
            <Route path="/books/:id" element={<BookDetail />} />
            <Route path="/search" element={<Search />} />
            <Route path="/profile" element={<Profile />} />
            <Route path="/favourites" element={<Favourites />} />
            <Route path="/borrowed" element={<Borrowed />} />
            <Route path="/history" element={<History />} />
            <Route path="/chat" element={<Chat />} />
          </Route>
          <Route element={<ProtectedRoute roles={["ROLE_ADMIN"]} />}> 
            <Route path="/admin" element={<Dashboard />} />
            <Route path="/admin/users" element={<AdminUsers />} />
            <Route path="/admin/books" element={<AdminBooks />} />
            <Route path="/admin/borrows" element={<AdminBorrows />} />
            <Route path="/admin/notifications" element={<AdminNotifications />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}

export default App
