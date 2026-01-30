<template>
  <div style="padding: 16px">
    <div style="display: flex; gap: 12px; align-items: center; margin-bottom: 12px">
      <el-input v-model="keyword" placeholder="Search title..." style="max-width: 240px" clearable />
      <el-select v-model="status" placeholder="Status" style="width: 140px" clearable>
        <el-option label="DRAFT" value="DRAFT" />
        <el-option label="PUBLISHED" value="PUBLISHED" />
      </el-select>
      <el-button type="primary" @click="fetchList">Search</el-button>
      <div style="flex: 1" />
      <el-button type="success" @click="$router.push('/posts/new')">New Post</el-button>
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
          <el-button size="small" @click="$router.push(`/posts/${row.id}/edit`)">Edit</el-button>
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
        @current-change="(p:number) => { pageNum = p; fetchList() }"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminPostDelete, adminPostPage, adminPostPublish, adminPostUnpublish } from '../api/posts'
import type { AdminPostListItemVO } from '../api/posts'

const loading = ref(false)
const list = ref<AdminPostListItemVO[]>([])
const total = ref(0)

const pageNum = ref(1)
const pageSize = ref(10)
const status = ref<string | undefined>()
const keyword = ref('')

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

onMounted(fetchList)
</script>
