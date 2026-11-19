export async function post(path, payload) {
  const baseUrl = (import.meta.env.VITE_API_URL || 'http://localhost:8080').replace(/\/$/, '')
  let response

  try {
    response = await fetch(baseUrl + path, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    })
  } catch {
    throw new Error('Не удалось подключиться к серверу. Попробуйте ещё раз.')
  }

  if (!response.ok) {
    const text = await response.text()
    let message = text
    if (response.headers.get('content-type')?.includes('json')) {
      try {
        const body = JSON.parse(text)
        message = body.message || body.detail || ''
      } catch {
        message = ''
      }
    }
    const error = new Error(response.status < 500 && message ? message : 'Не удалось выполнить запрос. Попробуйте ещё раз.')
    error.status = response.status
    throw error
  }

  if (response.status === 204) return null
  return response.headers.get('content-type')?.includes('json') ? response.json() : response.text()
}
