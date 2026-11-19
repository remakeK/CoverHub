export const minimumPasswordLength = 8

export function validateCredentials(data) {
  const errors = {}
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(data.email.trim())) {
    errors.email = 'Введите корректный email'
  }
  if (!data.password) errors.password = 'Введите пароль'
  return errors
}

export function validateRegistration(data) {
  const errors = validateCredentials(data)
  if (!data.name.trim()) errors.name = 'Введите имя'
  if (data.password.length < minimumPasswordLength) errors.password = 'Пароль должен содержать не менее 8 символов'
  if (!data.confirmPassword) errors.confirmPassword = 'Повторите пароль'
  else if (data.confirmPassword !== data.password) errors.confirmPassword = 'Пароли не совпадают'
  return errors
}
