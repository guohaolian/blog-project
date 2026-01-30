import { request } from './request'

export interface CategoryVO {
  id: number
  name: string
}

export function adminCategoryList() {
  return request.get<unknown, CategoryVO[]>('/admin/categories')
}

export function adminCategoryCreate(data: { name: string }) {
  return request.post<unknown, number>('/admin/categories', data)
}

export function adminCategoryUpdate(id: number, data: { name: string }) {
  return request.put<unknown, void>(`/admin/categories/${id}`, data)
}

export function adminCategoryDelete(id: number) {
  return request.delete<unknown, void>(`/admin/categories/${id}`)
}
