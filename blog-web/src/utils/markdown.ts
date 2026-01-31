import MarkdownIt from 'markdown-it'
import taskLists from 'markdown-it-task-lists'

export type TocItem = {
  level: number
  id: string
  title: string
}

function slugify(s: string) {
  return (s || '')
    .trim()
    .toLowerCase()
    .replace(/<[^>]+>/g, '')
    .replace(/[^\p{L}\p{N}]+/gu, '-')
    .replace(/^-+|-+$/g, '')
}

function ensureUniqueSlug(base: string, used: Map<string, number>) {
  const key = base || 'section'
  const n = used.get(key) || 0
  used.set(key, n + 1)
  return n === 0 ? key : `${key}-${n + 1}`
}

function escapeHtml(s: string) {
  // minimal escape for code blocks
  return s
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')
}

let md: MarkdownIt | null = null

function createMd(highlight?: (str: string, lang: string) => string) {
  const instance: MarkdownIt = new MarkdownIt({
    html: false,
    linkify: true,
    breaks: true,
    highlight,
  })

  instance.use(taskLists, { enabled: true, label: true, labelAfter: true })

  // headings: add id for anchor + collect TOC items (if env.__toc exists)
  const defaultHeadingOpen = instance.renderer.rules.heading_open
  instance.renderer.rules.heading_open = (tokens, idx, options, env: any, self) => {
    const token = tokens[idx]
    if (!token) {
      return defaultHeadingOpen ? defaultHeadingOpen(tokens, idx, options, env, self) : self.renderToken(tokens, idx, options)
    }

    const level = Number((token.tag || '').replace('h', '')) || 0

    // The inline token containing the heading text is right after heading_open
    const inline = tokens[idx + 1]
    const title = inline && (inline as any).type === 'inline' ? ((inline as any).content as string) : ''

    // if env provides slugger, keep stable ids between render + toc
    const slugger: ((title: string) => string) | undefined = env && env.__slugger
    const id = slugger ? slugger(title) : ensureUniqueSlug(slugify(title), new Map())

    if (id) {
      token.attrSet('id', id)
    }

    if (env && Array.isArray(env.__toc) && level >= 2 && level <= 4) {
      env.__toc.push({ level, id, title })
    }

    return defaultHeadingOpen ? defaultHeadingOpen(tokens, idx, options, env, self) : self.renderToken(tokens, idx, options)
  }

  const defaultImageRender = instance.renderer.rules.image
  instance.renderer.rules.image = (
    tokens: any[],
    idx: number,
    options: any,
    env: any,
    self: any,
  ): string => {
    const token = tokens[idx]
    if (token) {
      token.attrJoin('style', 'max-width:100%;height:auto;')
    }
    return defaultImageRender
      ? defaultImageRender(tokens, idx, options, env, self)
      : self.renderToken(tokens, idx, options)
  }

  return instance
}

function getMd() {
  if (!md) {
    // default renderer without code highlight (fast & small)
    md = createMd(undefined)
  }
  return md
}

// optional: call this on pages that need highlight
export async function enableMarkdownHighlight() {
  const hljs = (await import('highlight.js')).default
  await import('highlight.js/styles/github-dark.css')

  const highlight = (str: string, lang: string): string => {
    try {
      if (lang && hljs.getLanguage(lang)) {
        return `<pre class="hljs"><code>${hljs.highlight(str, { language: lang, ignoreIllegals: true }).value}</code></pre>`
      }
      return `<pre class="hljs"><code>${hljs.highlightAuto(str).value}</code></pre>`
    } catch {
      return `<pre class="hljs"><code>${escapeHtml(str)}</code></pre>`
    }
  }

  md = createMd(highlight)
}

export function renderMarkdown(content: string) {
  return getMd().render(content || '')
}

export function renderMarkdownWithToc(content: string): { html: string; toc: TocItem[] } {
  const used = new Map<string, number>()
  const env: any = {
    __toc: [] as TocItem[],
    __slugger: (title: string) => ensureUniqueSlug(slugify(title), used),
  }

  const html = getMd().render(content || '', env)
  return { html, toc: env.__toc as TocItem[] }
}
