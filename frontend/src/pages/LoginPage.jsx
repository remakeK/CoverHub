import { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router'
import { AuthContent } from '../components/auth/AuthLayout'
import FormField from '../components/ui/FormField'
import { useAuth } from '../context/authSession'
import { login } from '../services/authService'
import { validateCredentials } from '../utils/validators'

export default function LoginPage() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [errors, setErrors] = useState({})
  const [pending, setPending] = useState(false)
  const { signIn } = useAuth()
  const navigate = useNavigate()
  const { state } = useLocation()

  async function submit(event) {
    event.preventDefault()
    const validation = validateCredentials({ email, password })
    setErrors(validation)
    if (Object.keys(validation).length || pending) return
    setPending(true)
    try {
      const tokens = await login({ email: email.trim(), password })
      if (!tokens?.accessToken) throw new Error('Сервер не вернул данные сессии.')
      signIn(tokens, email.trim())
      navigate('/catalog', { replace: true })
    } catch (error) {
      setErrors(error.status === 401 ? { password: 'Неверный email или пароль' } : { server: error.message })
    } finally {
      setPending(false)
    }
  }

  return (
    <AuthContent title="Вход" description="Войдите, чтобы ставить оценки и загружать каверы"
      footer={<>Нет аккаунта? <Link to="/register">Зарегистрироваться</Link></>}>
      {state?.registered && <p className="form-success" role="status">Аккаунт создан. Теперь войдите.</p>}
      <form className="auth-form" onSubmit={submit} noValidate>
        <FormField id="email" name="email" label="Email" type="email" autoComplete="username" placeholder="name@example.ru"
          value={email} error={errors.email} onChange={(event) => { setEmail(event.target.value); setErrors({}) }} />
        <FormField id="password" name="password" label="Пароль" type="password" autoComplete="current-password" placeholder="Пароль"
          value={password} error={errors.password} onChange={(event) => { setPassword(event.target.value); setErrors({}) }} />
        <Link className="forgot-link" to="/forgot-password">Забыли пароль?</Link>
        {errors.server && <p className="field-error" role="alert">{errors.server}</p>}
        <button className="button primary auth-submit" disabled={pending}>{pending ? 'Входим…' : 'Войти'}</button>
      </form>
    </AuthContent>
  )
}
