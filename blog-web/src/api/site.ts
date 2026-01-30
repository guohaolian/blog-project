import { request } from './request'

export interface SiteSettingVO {
  siteName: string
  siteNotice?: string | null
  aboutContent?: string | null
  linksJson?: string | null
  seoTitle?: string | null
  seoKeywords?: string | null
  seoDescription?: string | null
  footerText?: string | null
}

export function getSiteSetting() {
  return request.get<unknown, SiteSettingVO>('/site')
}

export interface SiteLinkItem {
  name: string
  url: string
}

export function parseLinksJson(linksJson?: string | null): SiteLinkItem[] {
  if (!linksJson) return []
  try {
    const v = JSON.parse(linksJson)
    if (!Array.isArray(v)) return []
    return v
      .map((x: any) => ({ name: String(x?.name || ''), url: String(x?.url || '') }))
      .filter((x: SiteLinkItem) => x.name && x.url)
  } catch {
    return []
  }
}
