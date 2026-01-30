import { request } from './request'

export interface AdminLoginReq {
  username: string
  password: string
}

export interface AdminLoginResp {
  token: string
}

export interface AdminMeResp {
  id: number
  username: string
  displayName?: string
}

export function adminLogin(data: AdminLoginReq) {
  return request.post<unknown, AdminLoginResp>('/admin/auth/login', data)
}

export function adminMe() {
  return request.get<unknown, AdminMeResp>('/admin/auth/me')
}
