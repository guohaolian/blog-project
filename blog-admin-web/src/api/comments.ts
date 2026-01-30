import { request } from './request'

export interface PageResult<T> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
}

export interface AdminCommentVO {
  id: number
  postId: number
  postTitle: string
  nickname: string
  email?: string | null
  content: string
  status: string
  createdAt: string
}

export function adminCommentPage(params: {
  pageNum: number
  pageSize: number
  status?: string
  postId?: number
}) {
  return request.get<unknown, PageResult<AdminCommentVO>>('/admin/comments', { params })
}

export function adminCommentApprove(id: number) {
  return request.put<unknown, void>(`/admin/comments/${id}/approve`)
}

export function adminCommentReject(id: number) {
  return request.put<unknown, void>(`/admin/comments/${id}/reject`)
}

export function adminCommentDelete(id: number) {
  return request.delete<unknown, void>(`/admin/comments/${id}`)
}
