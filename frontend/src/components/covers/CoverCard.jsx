import { formatNumber } from '../../utils/formatNumber'

export default function CoverCard({ cover }) {
  return (
    <article className="cover-card">
      <div className="cover-art"><span>{cover.format}</span></div>
      <h2>{cover.title}</h2>
      <p>{cover.artist}</p>
      <div className="cover-stats">
        <span className="cover-likes" aria-label={'Оценки: ' + cover.likes}>
          <svg width="16" height="16" viewBox="0 0 24 24" aria-hidden="true"><path fill="currentColor" d="M12 21 3.6 12.7A5.1 5.1 0 0 1 11 5.6l1 1 1-1a5.1 5.1 0 0 1 7.4 7.1Z" /></svg>
          {formatNumber(cover.likes)}
        </span>
        <span aria-label={'Прослушивания: ' + cover.views}>
          <svg width="16" height="16" viewBox="0 0 24 24" aria-hidden="true"><path fill="currentColor" d="M12 3a9 9 0 0 0-9 9v6a2 2 0 0 0 2 2h3v-7H5v-1a7 7 0 1 1 14 0v1h-3v7h3a2 2 0 0 0 2-2v-6a9 9 0 0 0-9-9Z" /></svg>
          {formatNumber(cover.views)}
        </span>
      </div>
    </article>
  )
}
