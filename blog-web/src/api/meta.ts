import { request } from './request'

export interface CategoryVO {
  id: number
  name: string
}

export interface TagVO {
  id: number
  name: string
}

export function getCategories() {
  return request.get<unknown, CategoryVO[]>('/categories')
}

export function getTags() {
  return request.get<unknown, TagVO[]>('/tags')
}
