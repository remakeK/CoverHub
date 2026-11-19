import SelectMenu from '../ui/SelectMenu'

const options = [
  { value: 'newest', label: 'Сначала новые' },
  { value: 'views', label: 'По прослушиваниям' },
  { value: 'likes', label: 'По лайкам' },
  { value: 'popular', label: 'Сначала популярные' },
]

export default function CatalogSort({ value, onChange, disabled }) {
  return <SelectMenu label="Сортировка" disabled={disabled} value={disabled ? 'popular' : value}
    onChange={onChange} options={options} />
}
