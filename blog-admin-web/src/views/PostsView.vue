<template>
  <div style="padding: 16px">
    <div style="display: flex; gap: 12px; align-items: center; margin-bottom: 12px">
      <el-input v-model="keyword" placeholder="Search title..." style="max-width: 240px" clearable />
      <el-select v-model="status" placeholder="Status" style="width: 140px" clearable>
        <el-option label="DRAFT" value="DRAFT" />
        <el-option label="PUBLISHED" value="PUBLISHED" />
      </el-select>
      <el-button type="primary" @click="onSearch">Search</el-button>
      <div style="flex: 1" />
      <el-button type="success" @click="$router.push('/admin/posts/new')">New Post</el-button>
    </div>

    <el-table :data="list" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="Title" />
      <el-table-column prop="status" label="Status" width="120" />
      <el-table-column label="Category" width="160">
        <template #default="{ row }">
          {{ row.category?.name || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="updatedAt" label="Updated" width="180" />
      <el-table-column label="Actions" width="260">
        <template #default="{ row }">
          <el-button size="small" @click="$router.push(`/admin/posts/${row.id}/edit`)">Edit</el-button>
          <el-button
            v-if="row.status !== 'PUBLISHED'"
            size="small"
            type="primary"
            @click="publish(row.id)"
          >
            Publish
          </el-button>
          <el-button
            v-else
            size="small"
            type="warning"
            @click="unpublish(row.id)"
          >
            Unpublish
          </el-button>
          <el-button size="small" type="danger" @click="remove(row.id)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin-top: 12px; display: flex; justify-content: flex-end">
      <el-pagination
        background
        layout="prev, pager, next"
        :current-page="pageNum"
        :page-size="pageSize"
        :total="total"
        @current-change="onPageChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminPostDelete, adminPostPage, adminPostPublish, adminPostUnpublish } from '../api/posts'
import type { AdminPostListItemVO } from '../api/posts'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const list = ref<AdminPostListItemVO[]>([])
const total = ref(0)

const pageNum = ref(1)
const pageSize = ref(10)
const status = ref<string | undefined>()
const keyword = ref('')

function syncToQuery() {
  const q: Record<string, any> = {
    pageNum: pageNum.value !== 1 ? String(pageNum.value) : undefined,
    status: status.value || undefined,
    keyword: keyword.value || undefined,
  }

  // Keep URL in sync without pushing new history on every change.
  router.replace({ path: '/admin/posts', query: q })
}

function initFromQuery() {
  const q = route.query

  const pn = Number(q.pageNum)
  pageNum.value = Number.isFinite(pn) && pn > 0 ? pn : 1

  const st = typeof q.status === 'string' ? q.status : undefined
  status.value = st || undefined

  const kw = typeof q.keyword === 'string' ? q.keyword : ''
  keyword.value = kw
}

async function fetchList() {
  loading.value = true
  try {
    const res = await adminPostPage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      status: status.value,
      keyword: keyword.value || undefined,
    })
    list.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function onSearch() {
  pageNum.value = 1
  syncToQuery()
  fetchList()
}

function onPageChange(p: number) {
  pageNum.value = p
  syncToQuery()
  fetchList()
}

async function publish(id: number) {
  await adminPostPublish(id)
  ElMessage.success('Published')
  fetchList()
}

async function unpublish(id: number) {
  await adminPostUnpublish(id)
  ElMessage.success('Unpublished')
  fetchList()
}

async function remove(id: number) {
  await ElMessageBox.confirm('Delete this post?', 'Confirm', { type: 'warning' })
  await adminPostDelete(id)
  ElMessage.success('Deleted')
  fetchList()
}

onMounted(() => {
  initFromQuery()
  fetchList()
})
</script>
