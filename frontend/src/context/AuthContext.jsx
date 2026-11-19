import { useState } from 'react'
import { AuthContext } from './authSession'
import { logout } from '../services/authService'
import { readAccountName } from '../utils/accountName'

export function AuthProvider({ children }) {
  const [session, setSession] = useState(null)

  function signIn(tokens, email) {
    setSession({ tokens, user: { email, name: readAccountName(email) } })
  }


  async function signOut() {
    const refreshToken = session?.tokens.refreshToken
    setSession(null)
    if (refreshToken) {
      try {
        await logout(refreshToken)
      } catch {
        // The local session has already been cleared.
      }
    }
  }

  return (
    <AuthContext.Provider value={{ user: session?.user ?? null, signIn, signOut }}>
      {children}
    </AuthContext.Provider>
  )
}
