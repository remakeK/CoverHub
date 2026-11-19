import { Link } from 'react-router'
import { AuthContent } from '../components/auth/AuthLayout'

export default function ForgotPasswordPage() {
  return <AuthContent title="Восстановление пароля" description="Восстановление пароля пока недоступно."
    footer={<Link to="/login">Вернуться ко входу</Link>} />
}
