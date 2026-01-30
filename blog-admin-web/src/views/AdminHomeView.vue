<template>
  <div style="padding: 16px">
    <h2 style="margin: 0 0 12px">Dashboard</h2>

    <p v-if="auth.user" style="margin: 0 0 16px">
      Welcome, {{ auth.user.displayName || auth.user.username }}.
    </p>
    <p v-else style="margin: 0 0 16px">You're logged in.</p>

    <el-row :gutter="12">
      <el-col :xs="24" :sm="12" :md="8">
        <el-card shadow="hover" style="margin-bottom: 12px" v-loading="statsLoading">
          <template #header>
            <div style="display: flex; align-items: center; justify-content: space-between; gap: 12px">
              <span>Quick Stats</span>
              <el-button size="small" :loading="statsLoading" @click="loadStats">Refresh</el-button>
            </div>
          </template>

          <el-descriptions :column="1" size="small" border>
            <el-descriptions-item label="Total posts">
              <el-link type="primary" :underline="false" @click="goPosts()">
                {{ stats.total }}
              </el-link>
            </el-descriptions-item>
            <el-descriptions-item label="Draft">
              <el-link type="primary" :underline="false" @click="goPosts('DRAFT')">
                {{ stats.draft }}
              </el-link>
            </el-descriptions-item>
            <el-descriptions-item label="Published">
              <el-link type="primary" :underline="false" @click="goPosts('PUBLISHED')">
                {{ stats.published }}
              </el-link>
            </el-descriptions-item>
          </el-descriptions>

          <div v-if="statsError" style="margin-top: 8px; color: var(--el-color-danger)">
            {{ statsError }}
            <div style="margin-top: 8px">
              <el-button size="small" type="primary" @click="router.push('/admin/posts')">Go to Posts</el-button>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="8">
        <el-card shadow="hover" style="margin-bottom: 12px">
          <template #header>Recent updates</template>

          <div style="display: flex; justify-content: flex-end; margin-bottom: 8px">
            <el-button size="small" :loading="recentLoading" @click="loadRecent">Refresh</el-button>
          </div>

          <el-table
            size="small"
            :data="recent"
            v-loading="recentLoading"
            empty-text="No posts"
            :show-header="false"
            style="width: 100%"
          >
            <el-table-column>
              <template #default="{ row }">
                <div style="display: flex; align-items: center; justify-content: space-between; gap: 12px">
                  <div style="min-width: 0">
                    <el-link
                      type="primary"
                      :underline="false"
                      @click="router.push(`/admin/posts/${row.id}/edit`)"
                      style="display: inline-block; max-width: 100%; white-space: nowrap; overflow: hidden; text-overflow: ellipsis"
                    >
                      {{ row.title || '(Untitled)' }}
                    </el-link>
                    <div style="margin-top: 4px; font-size: 12px; color: var(--el-text-color-secondary)">
                      <span>{{ row.status }}</span>
                      <span v-if="row.updatedAt"> · {{ row.updatedAt }}</span>
                    </div>
                  </div>
                  <el-button size="small" @click="router.push(`/admin/posts/${row.id}/edit`)">Edit</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>

          <div v-if="recentError" style="margin-top: 8px; color: var(--el-color-danger)">
            {{ recentError }}
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="8">
        <el-card shadow="hover" style="margin-bottom: 12px">
          <template #header>Posts</template>
          <div style="display: flex; gap: 8px; flex-wrap: wrap">
            <el-button type="primary" @click="router.push('/admin/posts')">Go to list</el-button>
            <el-button type="success" @click="router.push('/admin/posts/new')">New post</el-button>
          </div>
        </el-card>

        <el-card shadow="hover" style="margin-bottom: 12px">
          <template #header>Account</template>
          <div style="display: flex; align-items: center; justify-content: space-between; gap: 12px">
            <span style="color: var(--el-text-color-regular)">Logout current session</span>
            <el-button type="danger" @click="logout">Logout</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { adminPostPage } from '../api/posts'
import type { AdminPostListItemVO } from '../api/posts'
import { adminDashboardStats } from '../api/dashboard'

const router = useRouter()
const auth = useAuthStore()

const statsLoading = ref(false)
const statsError = ref<string | null>(null)
const stats = reactive({
  total: 0,
  draft: 0,
  published: 0,
})

const recentLoading = ref(false)
const recentError = ref<string | null>(null)
const recent = ref<AdminPostListItemVO[]>([])

function goPosts(status?: string) {
  router.push({ path: '/admin/posts', query: { status: status || undefined } })
}

async function loadStats() {
  statsLoading.value = true
  statsError.value = null
  try {
    const s = await adminDashboardStats()
    stats.total = s.total
    stats.draft = s.draft
    stats.published = s.published
  } catch (e) {
    statsError.value = 'Failed to load stats. Please check backend /admin/dashboard/stats.'
  } finally {
    statsLoading.value = false
  }
}

async function loadRecent() {
  recentLoading.value = true
  recentError.value = null
  try {
    const res = await adminPostPage({ pageNum: 1, pageSize: 5 })
    recent.value = res.list || []
  } catch {
    recentError.value = 'Failed to load recent posts.'
  } finally {
    recentLoading.value = false
  }
}

function logout() {
  auth.logout()
  router.replace('/login')
}

onMounted(async () => {
  await Promise.all([loadStats(), loadRecent()])
})
</script>
