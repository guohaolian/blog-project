import { request } from './request'

export interface CommentVO {
  id: number
  nickname: string
  content: string
  createdAt: string
}

export function getPostComments(postId: number) {
  return request.get<unknown, CommentVO[]>(`/posts/${postId}/comments`)
}

export function createPostComment(postId: number, data: { nickname: string; email?: string; content: string }) {
  return request.post<unknown, number>(`/posts/${postId}/comments`, data)
}
