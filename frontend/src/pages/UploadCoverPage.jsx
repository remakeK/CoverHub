import { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router'
import SelectMenu from '../components/ui/SelectMenu'
import { mockSongs } from '../data/mockSongs'

const performanceOptions = ['Вокал', 'Гитара', 'Фортепиано и клавишные', 'Барабаны и перкуссия', 'Другие инструменты']
  .map((label) => ({ value: label, label }))
const songOptions = mockSongs.map((song) => ({ value: String(song.id), label: song.title + ' · ' + song.artist }))

function UploadZone({ image = false }) {
  const [file, setFile] = useState(null)
  const accept = image ? 'image/*' : 'audio/*,video/*'

  function choose(next) {
    if (next && accept.split(',').some((type) => next.type.startsWith(type.slice(0, -1)))) setFile(next)
  }

  return (
    <div className="upload-zone" onDragOver={(event) => event.preventDefault()}
      onDrop={(event) => { event.preventDefault(); choose(event.dataTransfer.files[0]) }}>
      <div className="upload-icon" aria-hidden="true">
        <svg viewBox="0 0 24 24" width="22" height="22" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
          {image
            ? <><rect x="3" y="4" width="18" height="16" rx="3" /><circle cx="9" cy="9" r="1.5" /><path d="m4 17 5-4 4 3 4-5 4 4" /></>
            : <><path d="M6 3h8l4 4v14H6zM14 3v5h4" /><path d="m10 10 5 3-5 3z" /></>}
        </svg>
      </div>
      <h2>{image ? 'Обложка кавера' : 'Файл кавера'}</h2>
      <p aria-live="polite">{file ? file.name : image ? 'Перетащите изображение или выберите файл' : 'Добавьте аудио или видео'}</p>
      <label className="button upload-file-picker">
        <span>{file ? 'Заменить файл' : 'Выбрать файл'}</span>
        <input type="file" accept={accept} aria-label={image ? 'Выбрать обложку кавера' : 'Выбрать файл кавера'}
          onChange={(event) => choose(event.target.files[0])} />
      </label>
    </div>
  )
}

function CoverDetails() {
  const navigate = useNavigate()
  const { state } = useLocation()
  const initial = state?.details
  const [performance, setPerformance] = useState(initial?.performance ?? '')
  const [song, setSong] = useState(initial?.song ?? '')
  const [submitted, setSubmitted] = useState(false)

  function next(event) {
    event.preventDefault()
    const details = Object.fromEntries(new FormData(event.currentTarget))
    details.title = details.title.trim()
    if (!details.title) return
    setSubmitted(true)
    if (!performance || !song) return
    navigate('/upload/files', { state: { details: { ...details, performance, song } } })
  }

  return (
    <form className="cover-details" onSubmit={next}>
      <label>Название кавера<input name="title" required placeholder="Название кавера" defaultValue={initial?.title} /></label>
      <div className="cover-select-field">
        <span>Исполнение</span>
        <SelectMenu label="Исполнение" placeholder="Выберите исполнение" options={performanceOptions}
          value={performance} onChange={setPerformance} invalid={submitted && !performance} />
        {submitted && !performance && <span className="cover-select-error" role="alert">Выберите исполнение.</span>}
      </div>
      <label className="upload-wide">Исполнитель<input name="artist" required placeholder="Имя исполнителя" defaultValue={initial?.artist} /></label>
      <div className="cover-select-field upload-wide">
        <span>Оригинальная песня</span>
        <SelectMenu label="Оригинальная песня" placeholder="Выберите оригинальную песню" options={songOptions}
          value={song} onChange={setSong} invalid={submitted && !song} />
        {submitted && !song && <span className="cover-select-error" role="alert">Выберите оригинальную песню.</span>}
      </div>
      <label className="upload-wide">Описание<textarea name="description" placeholder="Расскажите о кавере" defaultValue={initial?.description} /></label>
      <div className="upload-actions upload-wide">
        <button className="button primary" type="submit">Далее</button>
        <Link className="button" to="/catalog">Отмена</Link>
        <span>В демоверсии черновик не сохраняется.</span>
      </div>
    </form>
  )
}

function CoverFiles() {
  const { state } = useLocation()
  const [source, setSource] = useState('file')
  const [youtubeUrl, setYoutubeUrl] = useState('')
  const byLink = source === 'link'

  return <>
    <div className="upload-subtitle"><span>{state?.details?.title || 'Выхода нет (акустика)'}</span><span className="upload-status">Черновик</span></div>
    <div className="upload-source" role="group" aria-label="Источник кавера">
      <button type="button" aria-pressed={!byLink} onClick={() => setSource('file')}>Файл</button>
      <button type="button" aria-pressed={byLink} onClick={() => setSource('link')}>По ссылке</button>
    </div>
    <div hidden={byLink} className="upload-drop-grid"><UploadZone /><UploadZone image /></div>
    {byLink && (
      <div className="upload-link-panel">
        <h2>Ссылка на кавер</h2>
        <label>Ссылка на YouTube<input type="url" value={youtubeUrl} onChange={(event) => setYoutubeUrl(event.target.value)} placeholder="https://www.youtube.com/watch?v=…" /></label>
        <p>Обложка подтянется автоматически из YouTube.</p>
      </div>
    )}
    <div className="upload-actions upload-footer">
      <Link className="button" to="/my-covers">Выйти</Link>
      <button className="button primary upload-publish" type="button" disabled>Опубликовать</button>
    </div>
  </>
}

export default function UploadCoverPage({ files = false }) {
  const { state } = useLocation()
  return (
    <main className="upload-main">
      <section className="upload-card" aria-labelledby="upload-title">
        <div className="upload-step">
          <span>Шаг {files ? 2 : 1} из 2</span>
          <span aria-hidden="true" className="upload-step-bars" data-second-step={files} data-animate={files && Boolean(state?.details)}>
            <i /><i />
          </span>
        </div>
        <h1 id="upload-title">{files ? 'Загрузка файлов' : 'Загрузка кавера'}</h1>
        {files ? <CoverFiles /> : <><p className="upload-subtitle">Данные кавера</p><CoverDetails /></>}
      </section>
    </main>
  )
}
