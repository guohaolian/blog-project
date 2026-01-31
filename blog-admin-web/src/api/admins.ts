import { request } from './request'

export interface AdminUserVO {
  id: number
  username: string
  displayName?: string
  status: number
  createdAt?: string
  updatedAt?: string
}

export function adminUserList() {
  return request.get<unknown, AdminUserVO[]>('/admin/admins')
}

export function adminUserCreate(data: { username: string; password: string; displayName?: string }) {
  return request.post<unknown, { id: number }>('/admin/admins', data)
}

export function adminUserResetPassword(id: number, data: { newPassword: string }) {
  return request.put<unknown, { reset: boolean }>(`/admin/admins/${id}/reset-password`, data)
}

export function adminUserUpdateStatus(id: number, data: { status: number }) {
  return request.put<unknown, { updated: boolean }>(`/admin/admins/${id}/status`, data)
}
