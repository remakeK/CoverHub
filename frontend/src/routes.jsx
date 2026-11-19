import UploadCoverPage from './pages/UploadCoverPage'
import MyCoversPage from './pages/MyCoversPage'
import { Navigate, Route, Routes } from 'react-router'
import AuthLayout from './components/auth/AuthLayout'
import CatalogPage from './pages/CatalogPage'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import ForgotPasswordPage from './pages/ForgotPasswordPage'

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/catalog" replace />} />
      <Route path="/catalog" element={<CatalogPage />} />
      <Route path="/upload" element={<UploadCoverPage />} />
      <Route path="/upload/files" element={<UploadCoverPage files />} />
      <Route path="/my-covers" element={<MyCoversPage />} />
      <Route element={<AuthLayout />}>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/forgot-password" element={<ForgotPasswordPage />} />
      </Route>
      <Route path="*" element={<Navigate to="/catalog" replace />} />
    </Routes>
  )
}
