export default function OriginalTrackList({ tracks }) {
  return (
    <ul className="original-tracks" aria-label="Оригинальные треки">
      {tracks.map((track) => (
        <li key={track.id}>
          <strong>{track.title}</strong>
          <span>{track.artist}</span>
        </li>
      ))}
    </ul>
  )
}
