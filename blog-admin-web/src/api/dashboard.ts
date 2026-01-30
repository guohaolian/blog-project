import { request } from './request'

export interface AdminDashboardStatsVO {
  total: number
  draft: number
  published: number
  categories: number
  tags: number
  commentsPending: number
  totalViews: number
}

export function adminDashboardStats() {
  return request.get<unknown, AdminDashboardStatsVO>('/admin/dashboard/stats')
}
