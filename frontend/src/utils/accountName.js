const nameKey = (email) => 'coverhub:account-name:' + email.trim().toLowerCase()

export function rememberAccountName(email, name) {
  try {
    localStorage.setItem(nameKey(email), name)
  } catch {
    // Registration still succeeds when browser storage is unavailable.
  }
}

export function readAccountName(email) {
  try {
    return localStorage.getItem(nameKey(email)) || ''
  } catch {
    return ''
  }
}

export function initialsFromName(name) {
  return name.trim().split(/\s+/u).slice(0, 2)
    .map((word) => Array.from(word)[0] || '').join('').toLocaleUpperCase()
}
