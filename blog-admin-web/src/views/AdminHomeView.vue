<template>
  <div class="admin-dashboard">
    <h2 class="admin-dashboard__title">Dashboard</h2>

    <p v-if="auth.user" class="admin-dashboard__welcome">
      Welcome, {{ auth.user.displayName || auth.user.username }}.
    </p>
    <p v-else class="admin-dashboard__welcome">You're logged in.</p>

    <el-row :gutter="12" class="admin-dashboard__row" align="stretch">
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
            <el-descriptions-item label="Categories">
              <el-link type="primary" :underline="false" @click="router.push('/admin/categories')">
                {{ stats.categories }}
              </el-link>
            </el-descriptions-item>
            <el-descriptions-item label="Tags">
              <el-link type="primary" :underline="false" @click="router.push('/admin/tags')">
                {{ stats.tags }}
              </el-link>
            </el-descriptions-item>
            <el-descriptions-item label="Comments pending">
              <el-link type="primary" :underline="false" @click="goComments()">
                {{ stats.commentsPending }}
              </el-link>
            </el-descriptions-item>
            <el-descriptions-item label="Total views">
              <el-link type="primary" :underline="false">
                {{ stats.totalViews }}
              </el-link>
            </el-descriptions-item>
          </el-descriptions>

          <div v-if="statsError" style="margin-top: 8px; color: #F56C6C">
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
                    <div style="margin-top: 4px; font-size: 12px; color: #909399">
                      <span>{{ row.status }}</span>
                      <span v-if="row.updatedAt"> · {{ row.updatedAt }}</span>
                    </div>
                  </div>
                  <el-button size="small" @click="router.push(`/admin/posts/${row.id}/edit`)">Edit</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>

          <div v-if="recentError" style="margin-top: 8px; color: #F56C6C">
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
            <span style="color: #606266">Logout current session</span>
            <el-button type="danger" @click="logout">Logout</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="12" class="admin-dashboard__row" align="stretch">
      <el-col :xs="24" :md="12">
        <el-card shadow="hover" style="margin-bottom: 12px" v-loading="statsLoading">
          <template #header>
            <div style="display: flex; align-items: center; justify-content: space-between; gap: 12px">
              <span>Posts status</span>
              <el-button size="small" :loading="statsLoading" @click="loadStats">Refresh</el-button>
            </div>
          </template>

          <ECharts :option="postStatusOption" :loading="statsLoading" height="300px" :on-click="handlePostStatusClick" />
        </el-card>
      </el-col>

      <el-col :xs="24" :md="12">
        <el-card shadow="hover" style="margin-bottom: 12px" v-loading="statsLoading">
          <template #header>
            <div style="display: flex; align-items: center; justify-content: space-between; gap: 12px">
              <span>Content structure</span>
              <el-button size="small" :loading="statsLoading" @click="loadStats">Refresh</el-button>
            </div>
          </template>

          <ECharts
            :option="contentStructureOption"
            :loading="statsLoading"
            height="300px"
            :on-click="handleContentStructureClick"
          />
        </el-card>
      </el-col>

      <el-col :xs="24">
        <el-card shadow="hover" style="margin-bottom: 12px" v-loading="statsLoading">
          <template #header>
            <div style="display: flex; align-items: center; justify-content: space-between; gap: 12px">
              <span>KPI overview</span>
              <el-button size="small" :loading="statsLoading" @click="loadStats">Refresh</el-button>
            </div>
          </template>

          <ECharts :option="kpiOption" :loading="statsLoading" height="280px" :on-click="handleKpiClick" />
          <div style="margin-top: 6px; font-size: 12px; color: #909399">
            Tips: These metrics have different units; the chart is for a quick snapshot.
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { adminPostPage } from '../api/posts'
import type { AdminPostListItemVO } from '../api/posts'
import { adminDashboardStats } from '../api/dashboard'
import ECharts from '../components/charts/ECharts.vue'
import type { EChartsOption } from 'echarts'
import type { ECElementEvent } from 'echarts/core'
import { useAsyncTask } from '../utils/requestHelpers'

const router = useRouter()
const auth = useAuthStore()

const statsError = ref<string | null>(null)
const stats = reactive({
  total: 0,
  draft: 0,
  published: 0,
  categories: 0,
  tags: 0,
  commentsPending: 0,
  totalViews: 0,
})

const recentError = ref<string | null>(null)
const recent = ref<AdminPostListItemVO[]>([])

function goPosts(status?: string) {
  router.push({ path: '/admin/posts', query: { status: status || undefined } })
}

function goComments() {
  router.push({ path: '/admin/comments', query: { status: 'PENDING' } })
}

const { loading: statsLoading, run: loadStats } = useAsyncTask(
  async () => {
    const s = await adminDashboardStats()
    stats.total = s.total
    stats.draft = s.draft
    stats.published = s.published
    stats.categories = s.categories
    stats.tags = s.tags
    stats.commentsPending = s.commentsPending
    stats.totalViews = s.totalViews
    statsError.value = null
  },
  { defaultErrorMessage: 'Failed to load stats' },
)

const { loading: recentLoading, run: loadRecent } = useAsyncTask(
  async () => {
    const res = await adminPostPage({ pageNum: 1, pageSize: 5 })
    recent.value = res.list || []
    recentError.value = null
  },
  { defaultErrorMessage: 'Failed to load recent posts' },
)

function logout() {
  auth.logout()
  router.replace('/login')
}

onMounted(async () => {
  await Promise.all([loadStats(), loadRecent()])
})

const postStatusOption = computed<EChartsOption>(() => {
  const draft = stats.draft || 0
  const published = stats.published || 0
  const total = draft + published
  return {
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    graphic: [
      {
        type: 'text',
        left: 'center',
        top: '42%',
        style: {
          text: 'Total',
          textAlign: 'center',
          fill: '#909399',
          fontSize: 12,
          fontWeight: 500,
        },
      },
      {
        type: 'text',
        left: 'center',
        top: '50%',
        style: {
          text: String(total),
          textAlign: 'center',
          fill: '#303133',
          fontSize: 22,
          fontWeight: 700,
        },
      },
    ],
    series: [
      {
        name: 'Posts',
        type: 'pie',
        radius: ['45%', '70%'],
        avoidLabelOverlap: true,
        label: { formatter: '{b}: {c} ({d}%)' },
        data: [
          { value: published, name: 'Published' },
          { value: draft, name: 'Draft' },
        ],
      },
    ],
  }
})

const contentStructureOption = computed<EChartsOption>(() => {
  const categories = stats.categories || 0
  const tags = stats.tags || 0
  return {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: 24, right: 24, top: 20, bottom: 24, containLabel: true },
    xAxis: { type: 'value', minInterval: 1 },
    yAxis: { type: 'category', data: ['Categories', 'Tags'] },
    series: [
      {
        type: 'bar',
        data: [categories, tags],
        barWidth: 18,
        itemStyle: { borderRadius: [0, 6, 6, 0] },
      },
    ],
  }
})

const kpiOption = computed<EChartsOption>(() => {
  const totalPosts = stats.total || 0
  const totalViews = stats.totalViews || 0
  const pending = stats.commentsPending || 0

  return {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: 24, right: 24, top: 20, bottom: 24, containLabel: true },
    xAxis: {
      type: 'category',
      data: ['Posts', 'Views', 'Pending comments'],
      axisLabel: { interval: 0 },
    },
    yAxis: { type: 'value', minInterval: 1 },
    series: [
      {
        name: 'Count',
        type: 'bar',
        data: [totalPosts, totalViews, pending],
        barMaxWidth: 36,
        itemStyle: { borderRadius: [6, 6, 0, 0] },
      },
    ],
  }
})

function handlePostStatusClick(params: ECElementEvent) {
  const name = String((params as any)?.name || '')
  if (name === 'Draft') {
    goPosts('DRAFT')
    return
  }
  if (name === 'Published') {
    goPosts('PUBLISHED')
  }
}

function handleContentStructureClick(params: ECElementEvent) {
  const name = String((params as any)?.name || '')
  if (name === 'Categories') {
    router.push('/admin/categories')
    return
  }
  if (name === 'Tags') {
    router.push('/admin/tags')
  }
}

function handleKpiClick(params: ECElementEvent) {
  const name = String((params as any)?.name || '')
  // For bar series, name is usually the category name
  if (name === 'Posts') {
    router.push('/admin/posts')
    return
  }
  if (name === 'Views') {
    // no dedicated views page yet; fallback to posts list
    router.push('/admin/posts')
    return
  }
  if (name === 'Pending comments') {
    goComments()
  }
}
</script>

<style scoped>
.admin-dashboard {
  padding: 16px;
}

.admin-dashboard__title {
  margin: 0 0 12px;
}

.admin-dashboard__welcome {
  margin: 0 0 16px;
}

.admin-dashboard__row {
  margin-top: 12px;
}

.admin-dashboard__row:first-of-type {
  margin-top: 0;
}

/* 让同一行内的列高度一致（Element Plus el-row 默认是 flex） */
.admin-dashboard :deep(.el-col) {
  display: flex;
}

.admin-dashboard :deep(.el-card) {
  width: 100%;
}

/* 卡片 header 统一左右对齐 */
.admin-dashboard :deep(.el-card__header) {
  padding-top: 10px;
  padding-bottom: 10px;
}

/* 轻微压缩卡片内容空白，减少错位感 */
.admin-dashboard :deep(.el-card__body) {
  padding-top: 12px;
  padding-bottom: 12px;
}
</style>

