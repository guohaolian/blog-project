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

export interface PostListItemVO {
  id: number
  title: string
  summary?: string
  coverUrl?: string
  category?: CategoryVO
  tags?: TagVO[]
  publishedAt?: string
  viewCount?: number
}

export interface PostDetailVO {
  id: number
  title: string
  summary?: string
  content: string
  coverUrl?: string
  category?: CategoryVO
  tags?: TagVO[]
  publishedAt?: string
  viewCount?: number
}

export interface HotPostVO {
  id: number
  title: string
  viewCount?: number
  publishedAt?: string
}

export function getPosts(params: {
  pageNum: number
  pageSize: number
  keyword?: string
  categoryId?: number
  tagId?: number
}) {
  return request.get<unknown, PageResult<PostListItemVO>>('/posts', { params })
}

export function getPostDetail(id: number) {
  return request.get<unknown, PostDetailVO>(`/posts/${id}`)
}

export function getHotPosts(params?: { limit?: number }) {
  return request.get<unknown, HotPostVO[]>('/posts/hot', { params })
}
