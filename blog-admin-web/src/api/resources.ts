import { request } from './request'

export interface PageResult<T> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
}

export interface FileResourceVO {
  id: number
  url: string
  originalName?: string
  size?: number
  contentType?: string
  createdAt?: string
}

export function adminResourcePage(params: { pageNum: number; pageSize: number; keyword?: string; contentTypePrefix?: string }) {
  return request.get<unknown, PageResult<FileResourceVO>>('/admin/resources', { params })
}

export function adminResourceDelete(id: number) {
  return request.delete<unknown, { deleted: boolean }>(`/admin/resources/${id}`)
}
