import { request } from './request'

export interface ArchivePostVO {
  id: number
  title: string
  publishedAt?: string
}

export interface ArchiveMonthGroupVO {
  month: string
  count: number
  posts: ArchivePostVO[]
}

export function getArchives() {
  return request.get<unknown, ArchiveMonthGroupVO[]>('/posts/archives')
}
