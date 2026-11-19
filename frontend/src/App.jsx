import { BrowserRouter } from 'react-router'
import Header from './components/layout/Header'
import AppRoutes from './routes'
import { AuthProvider } from './context/AuthContext'

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Header />
        <AppRoutes />
      </AuthProvider>
    </BrowserRouter>
  )
}
