const tracks = [
  ['Звезда по имени Солнце', 'Кино'],
  ['Династия', 'VILLAIN, madk1d'],
  ['Ты не бойся ночи', 'ENZRO'],
  ['Ride', 'twenty one pilots'],
  ['Dynamite', 'Taio Cruz'],
  ['Батареи', 'Нервы'],
  ['КУПЕР', 'SQWOZ BAB'],
  ['Малый повзрослел', 'Макс Корж'],
  ['Ламбада', 'T-Fest, Скриптонит'],
  ['Кукушка', 'Кино'],
  ['Выхода нет', 'Сплин'],
  ['Дождь', 'ДДТ'],
  ['Поворот', 'Машина времени'],
  ['Знаешь ли ты', 'МакSим'],
]

export const mockSongs = tracks.map(([title, artist], index) => ({ id: index + 1, title, artist }))
