import UploadCoverLink from '../components/covers/UploadCoverLink'

export default function MyCoversPage() {
  return (
    <main className="upload-main">
      <section className="upload-card" aria-labelledby="my-covers-title">
        <h1 id="my-covers-title">Мои каверы</h1>
        <p className="upload-subtitle">Список каверов пока не подключён к серверу.</p>
        <UploadCoverLink />
      </section>
    </main>
  )
}
