export type AutoThemeWindow = {
  start: string // HH:mm, inclusive
  end: string // HH:mm, exclusive
}

export const DEFAULT_AUTO_WINDOW: AutoThemeWindow = { start: '19:00', end: '07:00' }

export function parseHmToMinutes(hm: string): number {
  const parts = (hm || '').split(':')
  const h = Number(parts[0])
  const m = Number(parts[1])
  const hh = Number.isFinite(h) ? h : 0
  const mm = Number.isFinite(m) ? m : 0
  return Math.max(0, Math.min(23, hh)) * 60 + Math.max(0, Math.min(59, mm))
}

export function isNowInWindow(now: Date, win: AutoThemeWindow): boolean {
  const mins = now.getHours() * 60 + now.getMinutes()
  const start = parseHmToMinutes(win.start)
  const end = parseHmToMinutes(win.end)
  if (start === end) return true // whole day
  if (start < end) return mins >= start && mins < end
  // crosses midnight
  return mins >= start || mins < end
}

function nextFor(now: Date, targetMins: number) {
  const base = new Date(now)
  base.setSeconds(0, 0)
  base.setHours(0, 0, 0, 0)
  const t = new Date(base.getTime() + targetMins * 60 * 1000)
  if (t <= now) t.setDate(t.getDate() + 1)
  return t
}

export function nextBoundaryMs(now: Date, win: AutoThemeWindow): number {
  const start = parseHmToMinutes(win.start)
  const end = parseHmToMinutes(win.end)

  // If currently in dark window, next boundary is end; otherwise start.
  const inWin = isNowInWindow(now, win)
  const target = inWin ? end : start
  const t = nextFor(now, target)

  // add a small delay to ensure boundary is passed
  return Math.max(1000, t.getTime() - now.getTime() + 1000)
}

