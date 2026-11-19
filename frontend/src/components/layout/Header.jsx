import UploadCoverLink from '../covers/UploadCoverLink'
import { Link, NavLink } from 'react-router'
import { useAuth } from '../../context/authSession'
import { initialsFromName } from '../../utils/accountName'

export default function Header() {
  const { user, signOut } = useAuth()
  const initials = initialsFromName(user?.name || '')

  return (
    <header className="site-header">
      <div className="header-inner">
        <Link className="brand" to="/">CoverHub</Link>
        <nav className="header-nav" aria-label="Основная навигация">
          <NavLink to="/" end>Главная</NavLink>
          <NavLink to="/catalog">Каталог</NavLink>
        </nav>
        <div className="header-actions">
          {user ? <>
            <UploadCoverLink />
            <span className="header-account" title={user.email}>
              <span className="header-avatar" aria-hidden="true">{initials || '—'}</span>
              <span className="header-account-email">{user.name || user.email}</span>
            </span>
            <button className="button" type="button" onClick={signOut}>Выйти</button>
          </> : <>
            <Link className="button" to="/login">Войти</Link>
            <Link className="button primary" to="/register">Регистрация</Link>
          </>}
        </div>
      </div>
    </header>
  )
}
