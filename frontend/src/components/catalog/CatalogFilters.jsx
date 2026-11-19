import SelectMenu from '../ui/SelectMenu'

const performances = ['Вокал', 'Гитара', 'Фортепиано и клавишные', 'Барабаны и перкуссия', 'Другие инструменты']

const kinds = [['songs', 'Оригиналы'], ['covers', 'Каверы']]
const ratings = [['any', 'Любой'], ['positive', 'Положительный'], ['negative', 'Отрицательный']]
const dates = [
  { value: '7', label: 'За неделю' },
  { value: '30', label: 'За месяц' },
  { value: '365', label: 'За год' },
  { value: '', label: 'За всё время' },
]

export default function CatalogFilters({ value, onChange, onApply, onReset }) {
  const originals = value.kind === 'songs'

  function change(key, next) {
    onChange({ ...value, [key]: next })
  }

  function toggle(key, option) {
    const selected = value[key]
    change(key, selected.includes(option) ? selected.filter((item) => item !== option) : [...selected, option])
  }

  function option(key, id, label, type, checked, onSelect) {
    return <div className="filter-option" key={id}>
      <span className="filter-control">
        <input type={type} name={key} aria-label={label} checked={checked} onChange={onSelect} />
        {type === 'checkbox' && (
          <svg className="filter-checkmark" viewBox="0 0 20 20" aria-hidden="true">
            <path d="M3.65 10.05 9.4 16.95 16.4 3.1" pathLength="1" />
          </svg>
        )}
      </span>
      <span aria-hidden="true">{label}</span>
    </div>
  }

  function radio(key, id, label) {
    return option(key, id, label, 'radio', value[key] === id, () => change(key, id))
  }

  function radioGroup(key, choices) {
    return <div className="filter-radio-group" style={{ '--radio-index': choices.findIndex(([id]) => id === value[key]) }}>
      {choices.map(([id, label]) => radio(key, id, label))}
    </div>
  }

  function checkbox(key, label) {
    return option(key, label, label, 'checkbox', !originals && value[key].includes(label), () => toggle(key, label))
  }

  return (
    <form className="catalog-filters" onSubmit={(event) => { event.preventDefault(); onApply() }}>
      <h2>Фильтры</h2>
      <fieldset><legend>Что показывать</legend>{radioGroup('kind', kinds)}</fieldset>
      <fieldset disabled={originals}><legend>Формат</legend>{['Аудио', 'Видео'].map((label) => checkbox('formats', label))}</fieldset>
      <fieldset disabled={originals}><legend>Исполнение кавера</legend>{performances.map((label) => checkbox('performances', label))}</fieldset>
      <div className="date-filter">
        <span>Дата добавления</span>
        <SelectMenu label="Дата добавления" disabled={originals} value={originals ? '' : value.days}
          onChange={(next) => change('days', next)} options={dates} />
      </div>
      <fieldset disabled={originals}><legend>Рейтинг</legend>
        {radioGroup('rating', ratings)}
      </fieldset>
      <div className="filter-actions">
        <button className="button primary">Применить</button>
        <button className="button" type="button" onClick={onReset}>Сбросить</button>
      </div>
    </form>
  )
}
