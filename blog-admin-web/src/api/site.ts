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

export function adminGetSite() {
  return request.get<unknown, SiteSettingVO>('/admin/site')
}

export function adminUpdateSite(data: SiteSettingVO) {
  return request.put<unknown, void>('/admin/site', data)
}
