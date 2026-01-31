import { request } from './request'

export interface UploadResultVO {
  url: string
  originalName?: string
  size?: number
  contentType?: string
}

export function adminUploadImage(file: File) {
  const form = new FormData()
  form.append('file', file)

  return request.post<unknown, UploadResultVO>('/admin/upload/image', form, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}
