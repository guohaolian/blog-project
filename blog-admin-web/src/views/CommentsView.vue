<template>
  <div style="padding: 16px">
    <div style="display: flex; align-items: center; justify-content: space-between; gap: 12px; margin-bottom: 12px">
      <h2 style="margin: 0">Comments</h2>
      <el-button size="small" :loading="loading" @click="fetchList">Refresh</el-button>
    </div>

    <div style="display: flex; gap: 12px; flex-wrap: wrap; margin-bottom: 12px">
      <el-select v-model="query.status" placeholder="Status" clearable style="width: 160px" @change="onFilterChange">
        <el-option label="PENDING" value="PENDING" />
        <el-option label="APPROVED" value="APPROVED" />
        <el-option label="REJECTED" value="REJECTED" />
      </el-select>

      <el-input
        v-model="postIdText"
        placeholder="Post ID"
        clearable
        style="width: 160px"
        @clear="onFilterChange"
        @keyup.enter="onFilterChange"
      />

      <el-button @click="onFilterChange">Apply</el-button>
    </div>

    <el-table :data="list" v-loading="loading" style="width: 100%" size="small">
      <el-table-column prop="id" label="ID" width="50" />
      <el-table-column label="Post" min-width="140">
        <template #default="{ row }">
          <div style="font-weight: 600">#{{ row.postId }} {{ row.postTitle || '' }}</div>
        </template>
      </el-table-column>
      <el-table-column prop="nickname" label="Nickname" width="110" />
      <el-table-column prop="email" label="Email" width="180" />
      <el-table-column prop="status" label="Status" width="110" />
      <el-table-column prop="createdAt" label="Created" width="170" />
      <el-table-column label="Content" min-width="250">
        <template #default="{ row }">
          <div style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 520px">
            {{ row.content }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="Actions" width="290">
        <template #default="{ row }">
          <el-button size="small" type="success" :disabled="row.status === 'APPROVED'" @click="approve(row.id)">
            Approve
          </el-button>
          <el-button size="small" type="warning" :disabled="row.status === 'REJECTED'" @click="reject(row.id)">
            Reject
          </el-button>
          <el-popconfirm title="Delete this comment?" @confirm="remove(row.id)">
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
        layout="prev, pager, next"
        :current-page="query.pageNum"
        :page-size="query.pageSize"
        :total="total"
        @current-change="(p:number) => { query.pageNum = p; fetchList() }"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import {
  adminCommentApprove,
  adminCommentDelete,
  adminCommentPage,
  adminCommentReject,
  type AdminCommentVO,
} from '../api/comments'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const list = ref<AdminCommentVO[]>([])
const total = ref(0)

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  status: '' as string | undefined,
  postId: undefined as number | undefined,
})

const postIdText = ref('')

function syncFromRoute() {
  const s = route.query.status
  query.status = typeof s === 'string' && s.trim() ? s.trim() : undefined

  const pid = route.query.postId
  if (typeof pid === 'string' && pid.trim()) {
    const n = Number(pid)
    query.postId = Number.isFinite(n) ? n : undefined
  } else {
    query.postId = undefined
  }
  postIdText.value = query.postId ? String(query.postId) : ''

  // reset pagination when coming from dashboard filters
  query.pageNum = 1
}

function syncToRoute() {
  router.replace({
    path: '/admin/comments',
    query: {
      status: query.status || undefined,
      postId: query.postId != null ? String(query.postId) : undefined,
    },
  })
}

function onFilterChange() {
  query.pageNum = 1
  query.postId = postIdText.value ? Number(postIdText.value) : undefined
  syncToRoute()
  fetchList()
}

async function fetchList() {
  loading.value = true
  try {
    const res = await adminCommentPage({
      pageNum: query.pageNum,
      pageSize: query.pageSize,
      status: query.status || undefined,
      postId: query.postId,
    })
    list.value = res.list || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

async function approve(id: number) {
  await adminCommentApprove(id)
  ElMessage.success('Approved')
  await fetchList()
}

async function reject(id: number) {
  await adminCommentReject(id)
  ElMessage.success('Rejected')
  await fetchList()
}

async function remove(id: number) {
  await adminCommentDelete(id)
  ElMessage.success('Deleted')
  await fetchList()
}

onMounted(() => {
  syncFromRoute()
  fetchList()
})

watch(
  () => route.query,
  () => {
    // If query changed externally (e.g., from Dashboard), apply it.
    syncFromRoute()
    fetchList()
  },
)
</script>
