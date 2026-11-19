import { useId, useRef, useState } from 'react'

export default function SelectMenu({ label, value, options, onChange, disabled, placeholder, invalid }) {
  const [open, setOpen] = useState(false)
  const id = useId()
  const trigger = useRef(null)
  const items = useRef(null)
  const selected = options.find((option) => option.value === value)

  const displayLabel = selected?.label ?? placeholder

  function close() {
    setOpen(false)
    trigger.current?.focus()
  }

  function handleKeys(event) {
    if (event.key === 'Escape' && open) {
      event.preventDefault()
      close()
      return
    }
    if (event.key !== 'ArrowDown' && event.key !== 'ArrowUp') return
    event.preventDefault()
    const buttons = [...items.current.querySelectorAll('button')]
    const direction = event.key === 'ArrowDown' ? 1 : -1
    const index = buttons.indexOf(document.activeElement)
    if (!open) {
      setOpen(true)
      requestAnimationFrame(() => buttons[direction === 1 ? 0 : buttons.length - 1]?.focus())
    } else {
      const next = index === -1 ? (direction === 1 ? 0 : buttons.length - 1) : (index + direction + buttons.length) % buttons.length
      buttons[next]?.focus()
    }
  }

  return (
    <div className="select-menu" data-open={open && !disabled} onKeyDown={handleKeys}
      onBlur={(event) => { if (!event.currentTarget.contains(event.relatedTarget)) setOpen(false) }}>
      <button ref={trigger} className="select-trigger" type="button" disabled={disabled}
        aria-label={displayLabel ? label + ': ' + displayLabel : label} aria-invalid={invalid} aria-expanded={open && !disabled} aria-controls={id}
        onClick={() => setOpen(!open)}>
        {selected ? displayLabel : <span className="select-placeholder">{displayLabel}</span>}
        {!disabled && <span className="select-arrow" aria-hidden="true" />}
      </button>
      <ul id={id} ref={items} className="select-options" hidden={!open || disabled} aria-label={label}>
        {options.map((option) => <li key={option.value}>
          <button type="button" aria-pressed={option.value === value}
            onClick={() => { onChange(option.value); close() }}>
            {option.label}{option.value === value && <span aria-hidden="true">✓</span>}
          </button>
        </li>)}
      </ul>
    </div>
  )
}
