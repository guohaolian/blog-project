import { request } from './request'

export interface TagVO {
  id: number
  name: string
}

export function adminTagList() {
  return request.get<unknown, TagVO[]>('/admin/tags')
}

export function adminTagCreate(data: { name: string }) {
  return request.post<unknown, number>('/admin/tags', data)
}

export function adminTagUpdate(id: number, data: { name: string }) {
  return request.put<unknown, void>(`/admin/tags/${id}`, data)
}

export function adminTagDelete(id: number) {
  return request.delete<unknown, void>(`/admin/tags/${id}`)
}
