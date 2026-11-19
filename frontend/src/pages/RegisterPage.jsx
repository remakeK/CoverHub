import { useState } from 'react'
import { Link, useNavigate } from 'react-router'
import { AuthContent } from '../components/auth/AuthLayout'
import FormField from '../components/ui/FormField'
import { register } from '../services/authService'
import { rememberAccountName } from '../utils/accountName'
import { validateRegistration } from '../utils/validators'

export default function RegisterPage() {
  const [data, setData] = useState({ name: '', email: '', password: '', confirmPassword: '' })
  const [errors, setErrors] = useState({})
  const [pending, setPending] = useState(false)
  const navigate = useNavigate()

  function field(name) {
    return {
      id: name,
      name,
      value: data[name],
      error: errors[name],
      onChange: (event) => {
        setData({ ...data, [name]: event.target.value })
        setErrors({ ...errors, [name]: undefined, server: undefined })
      },
    }
  }

  async function submit(event) {
    event.preventDefault()
    const validation = validateRegistration(data)
    setErrors(validation)
    if (Object.keys(validation).length || pending) return
    setPending(true)
    try {
      await register({ name: data.name.trim(), email: data.email.trim(), password: data.password })
      rememberAccountName(data.email, data.name.trim())
      navigate('/login', { replace: true, state: { registered: true } })
    } catch (error) {
      setErrors(error.status === 409 ? { email: 'Этот email уже зарегистрирован' } : { server: error.message })
    } finally {
      setPending(false)
    }
  }

  return (
    <AuthContent title="Регистрация" description="Создайте аккаунт, чтобы загружать свои каверы"
      footer={<>Уже есть аккаунт? <Link to="/login">Войти</Link></>}>
      <form className="auth-form" onSubmit={submit} noValidate>
        <FormField {...field('name')} label="Имя" placeholder="Имя" autoComplete="name" />
        <FormField {...field('email')} label="Email" type="email" placeholder="name@example.ru" autoComplete="email" />
        <FormField {...field('password')} label="Пароль" type="password" placeholder="Придумайте пароль"
          autoComplete="new-password" hint="Не короче 8 символов" />
        <FormField {...field('confirmPassword')} label="Повторите пароль" type="password" placeholder="Пароль" autoComplete="new-password" />
        {errors.server && <p className="field-error" role="alert">{errors.server}</p>}
        <button className="button primary auth-submit" disabled={pending}>{pending ? 'Создаём аккаунт…' : 'Создать аккаунт'}</button>
      </form>
    </AuthContent>
  )
}
