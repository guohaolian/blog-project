import { request } from './request'

export interface PageResult<T> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
}

export interface CategoryVO {
  id: number
  name: string
}

export interface TagVO {
  id: number
  name: string
}

export interface AdminPostListItemVO {
  id: number
  title: string
  status: string
  category?: CategoryVO
  publishedAt?: string | null
  updatedAt?: string | null
}

export interface AdminPostEditVO {
  id: number
  title: string
  summary?: string
  content: string
  coverUrl?: string
  categoryId?: number | null
  tagIds?: number[]
  status: string
}

export interface AdminPostCreateReq {
  title: string
  summary?: string
  content: string
  coverUrl?: string
  categoryId?: number | null
  tagIds?: number[]
}

export type AdminPostUpdateReq = AdminPostCreateReq

export function adminPostPage(params: {
  pageNum: number
  pageSize: number
  status?: string
  keyword?: string
}) {
  return request.get<unknown, PageResult<AdminPostListItemVO>>('/admin/posts', { params })
}

export function adminPostCreate(data: AdminPostCreateReq) {
  return request.post<unknown, number>('/admin/posts', data)
}

export function adminPostGet(id: number) {
  return request.get<unknown, AdminPostEditVO>(`/admin/posts/${id}`)
}

export function adminPostUpdate(id: number, data: AdminPostUpdateReq) {
  return request.put<unknown, void>(`/admin/posts/${id}`, data)
}

export function adminPostDelete(id: number) {
  return request.delete<unknown, void>(`/admin/posts/${id}`)
}

export function adminPostPublish(id: number) {
  return request.put<unknown, void>(`/admin/posts/${id}/publish`)
}

export function adminPostUnpublish(id: number) {
  return request.put<unknown, void>(`/admin/posts/${id}/unpublish`)
}

export function adminCategories() {
  return request.get<unknown, CategoryVO[]>('/admin/posts/meta/categories')
}

export function adminTags() {
  return request.get<unknown, TagVO[]>('/admin/posts/meta/tags')
}
