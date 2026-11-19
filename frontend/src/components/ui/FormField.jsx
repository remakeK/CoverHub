export default function FormField({ id, label, hint, error, ...attributes }) {
  const descriptionId = error || hint ? id + '-description' : undefined

  return (
    <div className="form-field">
      <label htmlFor={id}>{label}</label>
      <input id={id} aria-invalid={Boolean(error)} aria-describedby={descriptionId} {...attributes} />
      {(error || hint) && (
        <p id={descriptionId} className={error ? 'field-error' : 'field-hint'}>{error || hint}</p>
      )}
    </div>
  )
}
