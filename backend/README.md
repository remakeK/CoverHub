# Готовые эндпоинты

### авторизация
1. http://localhost:8080/auth/login (email, password)
2. http://localhost:8080/auth/register (name, email, password)
3. http://localhost:8080/auth/refresh (refreshToken)
4. http://localhost:8080/auth/logout (refreshToken)

### пользователи
1. http://localhost:8080/users/all **для тестов**
2. http://localhost:8080/users/{id} (id)

### Категория
1. `GET /api/categories`
2. `GET /api/categories/{id}` (id)

### Оригинальная песня
1. `GET /api/original-songs` (id)
2. `GET /api/original-songs/{id}` (id)

### Оригинальный артист
1. `GET /api/original-artists`
2. `GET /api/original-artists/{id}` (id)

### Кавер
1. `GET /api/covers`
2. `GET /api/covers/{id}`

### Входные данные
1. `id` — для получения кавера по id

### Исполнитель кавера
1. `GET /api/performers`
2. `GET /api/performers/{id}` (id)

### Комментарий к каверу
1. `GET /api/comments`
2. `GET /api/comments/{id}` (id)

### Оценка кавера
1. `GET /api/ratings`
2. `GET /api/ratings/{id}` (id)

# Сущности

## Пользователь
1. id
2. имя
3. почта
4. описание(может быть null)
5. аватарка(может быть null)
6. роль
7. хеш пароля
8. дата регистрации

## Оригинальная песня
1. id
2. название

## Оригинальный артист
1. id песни
2. имя

## Кавер
1. id
2. id оригинала
3. id категории
4. название
5. ссылка на видео(на сервере)
6. сслыка на аудио(на сервере)
7. ссылка на ютуб
8. дата загрузки
9. id пользователя, загрузившего кавер

5,6,7 - хотя бы одно из трех должно быть
## Исполнитель кавера
1. id кавера
2. id пользователя(может быть null)
3. имя исполнителя(может быть null)

2,3 - хотя бы одно из двух должно быть
## Категория
1. id
2. название

## Оценка кавера
1. id
2. id кавера
3. id пользователя
4. оценка

## Комментарий к каверу
1. id
2. id кавера
3. id пользователя
4. содержание
5. дата отправки
