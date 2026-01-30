import { request } from './request'

export interface AdminDashboardStatsVO {
  total: number
  draft: number
  published: number
}

export function adminDashboardStats() {
  return request.get<unknown, AdminDashboardStatsVO>('/admin/dashboard/stats')
}
