import { useState } from 'react'
import CatalogFilters from '../components/catalog/CatalogFilters'
import CatalogSort from '../components/catalog/CatalogSort'
import CoverCard from '../components/covers/CoverCard'
import OriginalTrackList from '../components/catalog/OriginalTrackList'
import { mockCovers } from '../data/mockCovers'
import { mockSongs } from '../data/mockSongs'
import { initialFilters } from '../utils/catalogFilters'

const pageSize = 8

const comparators = {
  popular: (a, b) => b.views + b.likes - a.views - a.likes,
  newest: (a, b) => a.daysOld - b.daysOld,
  views: (a, b) => b.views - a.views,
  likes: (a, b) => b.likes - a.likes,
}

export default function CatalogPage() {
  const [draft, setDraft] = useState(initialFilters)
  const [filters, setFilters] = useState(initialFilters)
  const [query, setQuery] = useState('')
  const [sort, setSort] = useState('popular')
  const [limit, setLimit] = useState(pageSize)
  const showingCovers = draft.kind === 'covers'
  const step = showingCovers ? pageSize : 9
  const search = query.trim().toLocaleLowerCase('ru')
  const results = (showingCovers ? mockCovers : mockSongs).filter((entry) => {
    if (search && ![entry.title, entry.artist].some((value) => value.toLocaleLowerCase('ru').includes(search))) return false
    if (!showingCovers) return true
    if (filters.days && entry.daysOld > Number(filters.days)) return false
    if (filters.formats.length && !filters.formats.includes(entry.format)) return false
    if (filters.performances.length && !filters.performances.includes(entry.performance)) return false
    return filters.rating === 'any' || (filters.rating === 'positive' ? entry.rating > 0 : entry.rating < 0)
  })
  if (showingCovers) results.sort(comparators[sort])
  const visible = results.slice(0, limit)

  function reset() {
    setDraft(initialFilters)
    setFilters(initialFilters)
    setQuery('')
    setSort('popular')
    setLimit(pageSize)
  }

  return (
    <main className="catalog">
      <aside>
        <CatalogFilters value={draft} onChange={(value) => { setDraft(value); if (value.kind !== draft.kind) setLimit(value.kind === 'covers' ? pageSize : 9) }}
          onApply={() => { setFilters(draft); setLimit(step) }} onReset={reset} />
      </aside>
      <section className="catalog-results" aria-labelledby="catalog-title">
        <h1 id="catalog-title">Каталог</h1>
        <p className="catalog-count" role="status">Найдено {results.length}</p>
        <div className="catalog-toolbar">
          <label className="search-box">
            <input type="search" aria-label="Поиск по каталогу" placeholder="Поиск по каталогу" value={query}
              onChange={(event) => { setQuery(event.target.value); setLimit(step) }} />
            <svg width="21" height="21" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6" aria-hidden="true">
              <circle cx="10" cy="10" r="6" /><path d="m14.5 14.5 5 5" />
            </svg>
          </label>
          <CatalogSort disabled={!showingCovers} value={sort} onChange={(value) => { setSort(value); setLimit(pageSize) }} />
        </div>
        {visible.length === 0 ? (
          <div className="catalog-empty">
            <svg width="48" height="48" viewBox="0 0 48 48" fill="none" stroke="currentColor" strokeWidth="2" aria-hidden="true">
              <circle cx="24" cy="24" r="19" /><path d="M16 32q8-9 16 0M17 19h3m8 0h3" />
            </svg>
            <h2>Ничего не найдено</h2>
            <p>Попробуйте изменить запрос или сбросить фильтры.</p>
            <button type="button" className="button" onClick={reset}>Сбросить фильтры</button>
          </div>
        ) : showingCovers ? (
          <div className="cover-grid">{visible.map((cover) => <CoverCard key={cover.id} cover={cover} />)}</div>
        ) : <OriginalTrackList tracks={visible} />}
        {limit < results.length && (
          <div className="show-more"><button className="button" onClick={() => setLimit(limit + step)}>Показать ещё</button></div>
        )}
      </section>
    </main>
  )
}
