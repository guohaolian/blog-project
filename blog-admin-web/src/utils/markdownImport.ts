export interface MarkdownImportImageResult {
  /** original raw path in markdown (inside () or src="") */
  raw: string
  /** normalized path used for lookup (raw without query/hash) */
  normalized: string
  /** resolved File from the provided FileList */
  file?: File
  /** uploaded url */
  url?: string
  /** error message if failed */
  error?: string
}

export interface ImportMarkdownOptions {
  /** All files user selected/dropped together with the markdown file (typically including images). */
  assets?: FileList | File[]
  /** Upload function. Should return the accessible URL of the uploaded image. */
  uploadImage: (file: File) => Promise<string>
  /** Max upload concurrency. Default 3. */
  concurrency?: number
  /** Called as progress updates happen. */
  onProgress?: (p: { total: number; done: number; ok: number; failed: number; current?: string }) => void
  /** Abort signal. */
  signal?: AbortSignal
}

export interface ImportMarkdownResult {
  markdown: string
  images: MarkdownImportImageResult[]
}

const MD_IMAGE_RE = /!\[[^\]]*\]\(([^)]+)\)/g
const HTML_IMAGE_RE = /<img\b[^>]*\bsrc\s*=\s*("([^"]+)"|'([^']+)'|([^\s>]+))[^>]*>/gi

function stripAngleBrackets(s: string) {
  const t = s.trim()
  if (t.startsWith('<') && t.endsWith('>')) return t.slice(1, -1)
  return t
}

function stripQueryAndHash(s: string) {
  const idxQ = s.indexOf('?')
  const idxH = s.indexOf('#')
  const idx =
    idxQ === -1 ? idxH : idxH === -1 ? idxQ : Math.min(idxQ, idxH)
  return idx === -1 ? s : s.slice(0, idx)
}

function isProbablyRemoteUrl(path: string) {
  const p = path.trim().toLowerCase()
  return (
    p.startsWith('http://') ||
    p.startsWith('https://') ||
    p.startsWith('data:') ||
    p.startsWith('file://')
  )
}

function pickBestLocalPathCandidate(p: string) {
  // Normalize slashes early
  let x = p.replace(/\\/g, '/')

  // If it's a Windows absolute path like C:/a/b/c.png, try to match from common folders.
  // We can't access the absolute path directly in browser sandbox, so we match by tail segments.
  const parts = x.split('/').filter(Boolean)
  if (parts.length === 0) return ''

  // Prefer from images/ or assets/ if present
  const idxImages = parts.lastIndexOf('images')
  if (idxImages !== -1 && idxImages < parts.length - 1) return parts.slice(idxImages).join('/')
  const idxAssets = parts.lastIndexOf('assets')
  if (idxAssets !== -1 && idxAssets < parts.length - 1) return parts.slice(idxAssets).join('/')

  // Otherwise just basename
  return parts[parts.length - 1]
}

function normalizeAssetKey(path: string) {
  let p = stripAngleBrackets(path)
  p = stripQueryAndHash(p)

  // markdown image syntax allows optional title: ![](path "title")
  // we only need the first token as the path.
  p = p.trim().split(/\s+/)[0] || ''

  // normalize slashes
  p = p.replace(/\\/g, '/')

  // Windows absolute path (C:/... or C:...): reduce to a matchable tail.
  if (/^[a-zA-Z]:\//.test(p) || /^[a-zA-Z]:$/.test(p) || /^[a-zA-Z]:/.test(p)) {
    return pickBestLocalPathCandidate(p)
  }

  // remove leading ./
  p = p.replace(/^\.\//, '')
  // collapse leading ../ segments (we can't resolve outside browser sandbox; best-effort match)
  while (p.startsWith('../')) p = p.slice(3)

  return p
}

function buildAssetMap(assets?: FileList | File[]) {
  const m = new Map<string, File>()
  if (!assets) return m
  const list = Array.isArray(assets) ? assets : Array.from(assets)
  for (const f of list) {
    const anyF: any = f as any
    const rel: string | undefined = anyF.webkitRelativePath

    const nameKey = f.name
    if (!m.has(nameKey)) m.set(nameKey, f)

    if (rel) {
      const norm = rel.replace(/\\/g, '/').replace(/^\//, '')
      if (!m.has(norm)) m.set(norm, f)

      const parts = norm.split('/').filter(Boolean)
      const base = parts[parts.length - 1]
      if (base && !m.has(base)) m.set(base, f)

      // also map without the first directory level, in case user picked a parent folder
      // and markdown references like images/a.png but webkitRelativePath is folder/images/a.png
      if (parts.length >= 2) {
        const noTop = parts.slice(1).join('/')
        if (!m.has(noTop)) m.set(noTop, f)
      }

      // if path contains "images/" or "assets/", map from that segment
      const idxImages = parts.indexOf('images')
      if (idxImages !== -1 && idxImages < parts.length - 1) {
        const fromImages = parts.slice(idxImages).join('/')
        if (!m.has(fromImages)) m.set(fromImages, f)
      }
      const idxAssets = parts.indexOf('assets')
      if (idxAssets !== -1 && idxAssets < parts.length - 1) {
        const fromAssets = parts.slice(idxAssets).join('/')
        if (!m.has(fromAssets)) m.set(fromAssets, f)
      }
    }
  }
  return m
}

function extractImagePaths(markdown: string) {
  const raws: string[] = []

  for (const m of markdown.matchAll(MD_IMAGE_RE)) {
    const raw = (m[1] || '').trim()
    if (raw) raws.push(raw)
  }

  for (const m of markdown.matchAll(HTML_IMAGE_RE)) {
    const raw = (m[2] || m[3] || m[4] || '').trim()
    if (raw) raws.push(raw)
  }

  return raws
}

async function mapLimit<T, R>(
  items: T[],
  concurrency: number,
  fn: (item: T, idx: number) => Promise<R>,
) {
  const results: R[] = new Array(items.length) as any
  let next = 0

  const workers = new Array(Math.max(1, concurrency)).fill(0).map(async () => {
    while (true) {
      const i = next++
      if (i >= items.length) return
      const item = items[i] as T
      results[i] = await fn(item, i)
    }
  })

  await Promise.all(workers)
  return results
}

function escapeRegExp(s: string) {
  return s.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
}

function replaceAllCompat(input: string, search: string, replacement: string) {
  if (!search) return input
  return input.replace(new RegExp(escapeRegExp(search), 'g'), replacement)
}

function extractMdTargetTokens(raw: string) {
  // raw is inside () in markdown image syntax, may contain optional title.
  //
  // Examples:
  // - images/a.png
  // - <images/a.png>
  // - images/a.png "title"
  // - images/a.png 'title'
  //
  // We produce multiple candidates to maximize replacement hits.
  const trimmed = raw.trim()
  const noBrackets = stripAngleBrackets(trimmed)
  const noQH = stripQueryAndHash(noBrackets)
  const firstToken = noQH.split(/\s+/)[0] || ''

  // Also create a slash-normalized variant in case markdown contains backslashes.
  const slashNorm = firstToken.replace(/\\/g, '/')

  return {
    trimmed,
    noBrackets,
    noQH,
    firstToken,
    slashNorm,
  }
}

function replaceImgSrcCompat(markdown: string, search: string, replacement: string) {
  if (!search) return markdown
  // replace src="search" or src='search' or src=search (unquoted)
  const esc = escapeRegExp(search)
  return markdown
    .replace(new RegExp(`(<img\\b[^>]*\\bsrc\\s*=\\s*\")${esc}(\"[^>]*>)`, 'gi'), `$1${replacement}$2`)
    .replace(new RegExp(`(<img\\b[^>]*\\bsrc\\s*=\\s*')${esc}('[^>]*>)`, 'gi'), `$1${replacement}$2`)
    .replace(new RegExp(`(<img\\b[^>]*\\bsrc\\s*=\\s*)${esc}((?:\\s|>|/)[^>]*>)`, 'gi'), `$1${replacement}$2`)
}

export async function importMarkdownWithUploads(
  markdown: string,
  options: ImportMarkdownOptions,
): Promise<ImportMarkdownResult> {
  const concurrency = options.concurrency ?? 3
  const assetMap = buildAssetMap(options.assets)

  const rawPaths = extractImagePaths(markdown)
  // de-dup by normalized path to avoid uploading same image multiple times
  const uniqueNorm = new Map<string, string>() // norm -> raw(first)

  for (const raw of rawPaths) {
    if (isProbablyRemoteUrl(raw)) continue
    const norm = normalizeAssetKey(raw)
    if (!norm) continue
    if (!uniqueNorm.has(norm)) uniqueNorm.set(norm, raw)
  }

  const norms = Array.from(uniqueNorm.keys())
  const total = norms.length

  let done = 0
  let ok = 0
  let failed = 0

  const results = await mapLimit(norms, concurrency, async (norm) => {
    if (options.signal?.aborted) throw new DOMException('Aborted', 'AbortError')

    const raw = uniqueNorm.get(norm)!
    options.onProgress?.({ total, done, ok, failed, current: norm })

    const r: MarkdownImportImageResult = {
      raw,
      normalized: norm,
    }

    const base = norm.split('/').pop() || ''
    const file = assetMap.get(norm) || (base ? assetMap.get(base) : undefined)
    if (!file) {
      r.error = `Image file not found in selected folder: ${norm} (try picking a parent folder that includes the images directory)`
      failed++
      done++
      options.onProgress?.({ total, done, ok, failed, current: norm })
      return r
    }

    r.file = file
    try {
      r.url = await options.uploadImage(file)
      ok++
    } catch (e: any) {
      r.error = e?.message || String(e)
      failed++
    } finally {
      done++
      options.onProgress?.({ total, done, ok, failed, current: norm })
    }
    return r
  })

  // Replace all occurrences for each uploaded image.
  let out = markdown
  for (const r of results) {
    if (!r.url) continue

    const tokens = extractMdTargetTokens(r.raw)

    // Try replacing markdown image target with multiple candidates.
    // 1) as-is (raw inside parentheses)
    out = replaceAllCompat(out, tokens.trimmed, r.url)

    // 2) strip <> and query/hash and title
    if (tokens.noBrackets !== tokens.trimmed) out = replaceAllCompat(out, tokens.noBrackets, r.url)
    if (tokens.noQH !== tokens.noBrackets) out = replaceAllCompat(out, tokens.noQH, r.url)
    if (tokens.firstToken && tokens.firstToken !== tokens.noQH) out = replaceAllCompat(out, tokens.firstToken, r.url)
    if (tokens.slashNorm && tokens.slashNorm !== tokens.firstToken) out = replaceAllCompat(out, tokens.slashNorm, r.url)

    // 3) normalized path (our key)
    if (r.normalized && r.normalized !== tokens.firstToken) out = replaceAllCompat(out, r.normalized, r.url)

    // 4) basename
    const base = r.normalized.split('/').pop()
    if (base && base !== r.normalized) out = replaceAllCompat(out, base, r.url)

    // Also handle HTML <img src="..."> occurrences explicitly.
    out = replaceImgSrcCompat(out, tokens.trimmed, r.url)
    out = replaceImgSrcCompat(out, tokens.noBrackets, r.url)
    out = replaceImgSrcCompat(out, tokens.noQH, r.url)
    if (tokens.firstToken) out = replaceImgSrcCompat(out, tokens.firstToken, r.url)
    if (tokens.slashNorm) out = replaceImgSrcCompat(out, tokens.slashNorm, r.url)
    if (r.normalized) out = replaceImgSrcCompat(out, r.normalized, r.url)
    if (base) out = replaceImgSrcCompat(out, base, r.url)
  }

  return { markdown: out, images: results }
}
