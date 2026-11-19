import { Link } from 'react-router'

export default function UploadCoverLink() {
  return (
    <Link className="button primary cover-upload-link" to="/upload">
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8"
        strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
        <path d="M12 15V3m-5 5 5-5 5 5M3 15v6h18v-6" />
      </svg>
      Загрузка кавера
    </Link>
  )
}