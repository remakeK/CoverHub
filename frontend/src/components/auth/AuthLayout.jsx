import { NavLink, Outlet, useLocation } from 'react-router'

export default function AuthLayout() {
  const { pathname } = useLocation()
  const tab = pathname === '/register' ? 'register' : pathname === '/login' ? 'login' : 'none'

  return (
    <main className="auth-main">
      <section className="auth-card" aria-labelledby="auth-title">
        <nav className="auth-tabs" data-tab={tab} aria-label="Вход или регистрация">
          <NavLink to="/login">Вход</NavLink>
          <NavLink to="/register">Регистрация</NavLink>
        </nav>
        <Outlet />
      </section>
    </main>
  )
}

export function AuthContent({ title, description, children, footer }) {
  return <>
    <h1 id="auth-title">{title}</h1>
    <p className="auth-description">{description}</p>
    {children}
    {footer && <p className="auth-footer">{footer}</p>}
  </>
}
