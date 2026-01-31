<template>
  <div style="padding: 16px">
    <div style="display: flex; align-items: center; justify-content: space-between; gap: 12px; margin-bottom: 12px">
      <h2 style="margin: 0">Resources</h2>
      <el-button size="small" :loading="loading" @click="fetchList">Refresh</el-button>
    </div>

    <div style="display:flex;gap:12px;flex-wrap:wrap;margin-bottom:12px">
      <el-input
        v-model="keyword"
        placeholder="Search (url/original name)"
        clearable
        style="width: 260px"
        @clear="onFilterChange"
        @keyup.enter="onFilterChange"
      />
      <el-select v-model="typeFilter" placeholder="Type" style="width: 160px" @change="onFilterChange">
        <el-option label="All" value="" />
        <el-option label="Images" value="image/" />
      </el-select>
      <el-select v-model="copyMode" placeholder="Copy mode" style="width: 160px">
        <el-option label="Relative URL" value="relative" />
        <el-option label="Absolute URL" value="absolute" />
      </el-select>
      <el-button @click="onFilterChange">Apply</el-button>
    </div>

    <el-table :data="list" v-loading="loading" style="width: 100%" size="small">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="Preview" min-width="220">
        <template #default="{ row }">
          <div style="display:flex;align-items:center;gap:10px">
            <el-image
              v-if="isImage(row.contentType)"
              :src="toAbsUrl(row.url)"
              style="width: 64px; height: 40px; border-radius: 6px"
              fit="cover"
              :preview-src-list="[toAbsUrl(row.url)]"
              preview-teleported
            />
            <a :href="toAbsUrl(row.url)" target="_blank" rel="noreferrer">
              {{ row.originalName || row.url }}
            </a>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="contentType" label="Content-Type" width="160" />
      <el-table-column label="Size" width="110">
        <template #default="{ row }">
          {{ formatSize(row.size) }}
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="Created" width="170" />
      <el-table-column label="Actions" width="220">
        <template #default="{ row }">
          <el-button size="small" @click="copyLink(row.url)">Copy</el-button>
          <el-popconfirm title="Delete this resource?" @confirm="remove(row.id)">
            <template #reference>
              <el-button size="small" type="danger">Delete</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin-top: 12px; display: flex; justify-content: flex-end">
      <el-pagination
        background
        layout="total, sizes, prev, pager, next"
        :current-page="pageNum"
        :page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        @current-change="onPageChange"
        @size-change="onPageSizeChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminResourceDelete, adminResourcePage, type FileResourceVO } from '../api/resources'

const loading = ref(false)
const list = ref<FileResourceVO[]>([])
const total = ref(0)

const pageNum = ref(1)
const pageSize = ref(10)

const keyword = ref('')
const typeFilter = ref('')
const copyMode = ref<'relative' | 'absolute'>('relative')

function isImage(contentType?: string) {
  return !!contentType && contentType.startsWith('image/')
}

function formatSize(n?: number) {
  const v = Number(n || 0)
  if (!Number.isFinite(v) || v <= 0) return '-'
  if (v < 1024) return `${v} B`
  if (v < 1024 * 1024) return `${(v / 1024).toFixed(1)} KB`
  return `${(v / 1024 / 1024).toFixed(1)} MB`
}

function toAbsUrl(url: string) {
  const base = (import.meta.env.VITE_API_BASE || '/api').replace(/\/$/, '')
  if (url.startsWith('http')) return url

  // uploads are served from backend at /uploads/** and should NOT be prefixed with /api
  if (url.startsWith('/uploads/')) return url

  // other relative paths (api endpoints) can still use /api prefix
  if (url.startsWith('/')) return base + url
  return base + '/' + url
}

async function fetchList() {
  loading.value = true
  try {
    const res = await adminResourcePage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: keyword.value || undefined,
      contentTypePrefix: typeFilter.value || undefined,
    })
    list.value = res.list || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

function onFilterChange() {
  pageNum.value = 1
  fetchList()
}

function onPageChange(p: number) {
  pageNum.value = p
  fetchList()
}

function onPageSizeChange(ps: number) {
  pageSize.value = ps
  pageNum.value = 1
  fetchList()
}

async function remove(id: number) {
  await ElMessageBox.confirm(
    'Delete this resource? This will delete the file on server. Posts that reference this URL will NOT be updated automatically (images may become 404).',
    'Confirm',
    { type: 'warning' },
  )
  await adminResourceDelete(id)
  ElMessage.success('Deleted. Note: posts referencing this URL are not updated automatically.')
  fetchList()
}

async function copyLink(url: string) {
  try {
    const text = copyMode.value === 'absolute' ? toAbsUrl(url) : url
    await navigator.clipboard.writeText(text)
    ElMessage.success('Copied')
  } catch {
    ElMessage.error('Copy failed')
  }
}

onMounted(fetchList)
</script>
