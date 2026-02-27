<template>
  <div style="padding: 16px">
    <div style="display: flex; align-items: center; justify-content: space-between; gap: 12px; margin-bottom: 12px">
      <h2 style="margin: 0">Resources</h2>
      <div style="display:flex;gap:8px">
        <el-button size="small" @click="resetColumnOrder">Reset Columns</el-button>
        <el-button size="small" :loading="loading" @click="fetchList">Refresh</el-button>
      </div>
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

    <el-table ref="tableRef" :data="list" v-loading="loading" style="width: 100%" size="small">
      <el-table-column
        v-for="col in orderedColumns"
        :key="col.key"
        :prop="col.prop"
        :label="col.label"
        :width="col.width"
        :min-width="col.minWidth"
        :fixed="col.fixed"
        :align="col.align"
        :show-overflow-tooltip="col.showOverflowTooltip"
        :data-col-key="col.key"
      >
        <template v-if="col.slot === 'preview'" #default="{ row }">
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

        <template v-else-if="col.slot === 'size'" #default="{ row }">
          {{ formatSize(row.size) }}
        </template>

        <template v-else-if="col.slot === 'actions'" #default="{ row }">
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
import { onMounted, ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminResourceDelete, adminResourcePage, type FileResourceVO } from '../api/resources'
import { useAsyncTask, runWithErrorToast } from '../utils/requestHelpers'
import { useDraggableTableColumns, type DraggableTableColumn } from '../utils/useDraggableTableColumns'

const list = ref<FileResourceVO[]>([])
const total = ref(0)

const pageNum = ref(1)
const pageSize = ref(10)

const keyword = ref('')
const typeFilter = ref('')
const copyMode = ref<'relative' | 'absolute'>('relative')

const { loading, run: fetchList } = useAsyncTask(
  async () => {
    const res = await adminResourcePage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: keyword.value || undefined,
      contentTypePrefix: typeFilter.value || undefined,
    })
    list.value = res.list || []
    total.value = res.total || 0
  },
  { defaultErrorMessage: 'Failed to load resources' },
)

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

  const ok = await runWithErrorToast(
    () => adminResourceDelete(id),
    { defaultErrorMessage: 'Failed to delete resource' },
  )
  if (!ok) return

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

const tableRef = ref()

const columns = computed<DraggableTableColumn[]>(() => [
  { key: 'id', prop: 'id', label: 'ID', width: 80 },
  { key: 'preview', label: 'Preview', minWidth: 220, slot: 'preview' },
  { key: 'contentType', prop: 'contentType', label: 'Content-Type', width: 160 },
  { key: 'size', label: 'Size', width: 110, slot: 'size' },
  { key: 'createdAt', prop: 'createdAt', label: 'Created', width: 170 },
  { key: 'actions', label: 'Actions', width: 220, slot: 'actions' },
])

const { orderedColumns, resetOrder: resetColumnOrder } = useDraggableTableColumns(tableRef, columns as any, {
  storageKey: 'admin.table.columnsOrder.resources',
})

onMounted(fetchList)
</script>
