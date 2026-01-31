<template>
  <div class="post-detail">
    <div class="post-detail__container" style="padding: 16px 0">
      <div class="post-detail__shell">
        <!-- left toc (independent column) -->
        <aside class="post-detail__left" v-if="toc.length > 0">
          <div class="post-detail__side">
            <div class="post-detail__sideInner">
              <el-card shadow="never">
                <template #header>
                  <div style="font-weight: 700">On this page</div>
                </template>

                <div class="toc">
                  <div
                    v-for="item in toc"
                    :key="item.id"
                    class="toc__item"
                    :class="[`toc__item--l${item.level}`, { 'toc__item--active': item.id === activeHeadingId }]"
                    @click="scrollToHeading(item.id)"
                  >
                    {{ item.title }}
                  </div>
                </div>
              </el-card>
            </div>
          </div>
        </aside>

        <!-- middle content (single width, everything aligned) -->
        <main class="post-detail__main" ref="mainScrollEl">
          <div class="post-detail__content">
            <el-button class="post-detail__back" type="primary" plain @click="back">
              <el-icon style="margin-right: 6px"><ArrowLeft /></el-icon>
              Back
            </el-button>

            <h2 class="post-detail__title">{{ post?.title }}</h2>
            <div class="post-detail__meta">
              <span v-if="post?.publishedAt">{{ post.publishedAt }}</span>
              <span v-if="post?.category"> · {{ post.category.name }}</span>
              <span v-if="(post?.tags?.length || 0) > 0">
                · Tags: {{ post?.tags?.map(t => t.name).join(', ') }}
              </span>
              <span> · Views: {{ post?.viewCount ?? 0 }}</span>
            </div>
          </div>

          <div class="post-detail__content">
            <el-card v-if="post" shadow="never">
              <img
                v-if="post.coverUrl"
                :src="post.coverUrl"
                alt="cover"
                style="width: 100%; max-height: 360px; object-fit: cover; border-radius: 6px; margin-bottom: 12px"
              />

              <div class="md" v-html="rendered" />
            </el-card>

            <el-empty v-else description="Loading..." />
          </div>

          <div class="post-detail__content" style="margin-top: 16px">
            <h3 style="margin: 0 0 8px">Comments</h3>

            <el-card style="margin-bottom: 12px" v-loading="commentsLoading">
              <el-empty v-if="!commentsLoading && comments.length === 0" description="No comments" />
              <div v-else>
                <div v-for="c in comments" :key="c.id" style="padding: 8px 0; border-bottom: 1px solid var(--el-border-color)">
                  <div style="display: flex; justify-content: space-between; gap: 12px">
                    <div style="font-weight: 600">{{ c.nickname }}</div>
                    <div style="color: #888; font-size: 12px">{{ c.createdAt }}</div>
                  </div>
                  <div style="margin-top: 6px; white-space: pre-wrap">{{ c.content }}</div>
                </div>
              </div>
            </el-card>

            <el-card>
              <template #header>
                <div style="display: flex; align-items: center; justify-content: space-between; gap: 12px">
                  <span>Leave a comment</span>
                </div>
              </template>

              <el-form :model="commentForm" label-width="90px">
                <el-form-item label="Nickname">
                  <el-input v-model="commentForm.nickname" maxlength="30" />
                </el-form-item>
                <el-form-item label="Email">
                  <el-input v-model="commentForm.email" maxlength="100" />
                </el-form-item>
                <el-form-item label="Content">
                  <el-input v-model="commentForm.content" type="textarea" :rows="4" maxlength="500" show-word-limit />
                </el-form-item>
              </el-form>

              <div style="display: flex; justify-content: flex-end">
                <el-button type="primary" :loading="commentSubmitting" @click="submitComment">Submit</el-button>
              </div>

              <div v-if="submitHint" style="margin-top: 8px; color: var(--el-text-color-secondary)">
                {{ submitHint }}
              </div>
            </el-card>
          </div>
        </main>

        <!-- right related (independent column) -->
        <aside class="post-detail__right" v-if="related.length > 0">
          <div class="post-detail__side">
            <div class="post-detail__sideInner">
              <el-card shadow="never">
                <template #header>
                  <div style="display:flex;justify-content:space-between;align-items:center;gap:12px">
                    <span>More in {{ post?.category?.name }}</span>
                  </div>
                </template>

                <div class="related">
                  <div
                    v-for="p in related"
                    :key="p.id"
                    class="related__item"
                    :class="{ 'related__item--active': p.id === post?.id }"
                    @click="goPost(p.id)"
                  >
                    {{ p.title }}
                  </div>
                </div>
              </el-card>
            </div>
          </div>
        </aside>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPostDetail, getPosts, type PostDetailVO, type PostListItemVO } from '../api/posts'
import { createPostComment, getPostComments, type CommentVO } from '../api/comments'
import { usePostsStore } from '../stores/posts'
import { useSiteStore } from '../stores/site'
import { enableMarkdownHighlight, renderMarkdownWithToc, type TocItem } from '../utils/markdown'
import { ArrowLeft } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const postsStore = usePostsStore()
const site = useSiteStore()

const post = ref<PostDetailVO | null>(null)

const toc = ref<TocItem[]>([])
const rendered = ref('')
const activeHeadingId = ref('')

const related = ref<PostListItemVO[]>([])

const commentsLoading = ref(false)
const comments = ref<CommentVO[]>([])

const commentSubmitting = ref(false)
const submitHint = ref('')
const commentForm = reactive({
  nickname: '',
  email: '',
  content: '',
})

const mainScrollEl = ref<HTMLElement | null>(null)

function rebuildMarkdown() {
  if (!post.value) {
    rendered.value = ''
    toc.value = []
    return
  }
  const { html, toc: t } = renderMarkdownWithToc(post.value.content || '')
  rendered.value = html
  toc.value = t
}

function scrollToHeading(id: string) {
  const el = document.getElementById(id)
  if (!el) return
  activeHeadingId.value = id
  // update hash for shareable deep link
  if (history.replaceState) {
    history.replaceState(null, '', `#${encodeURIComponent(id)}`)
  } else {
    location.hash = id
  }

  // In this page, only the middle column scrolls.
  // So we must scroll the main container instead of window.
  const container = mainScrollEl.value
  if (container) {
    const containerRect = container.getBoundingClientRect()
    const targetRect = el.getBoundingClientRect()
    const top = container.scrollTop + (targetRect.top - containerRect.top) - 12
    container.scrollTo({ top, behavior: 'smooth' })
  } else {
    el.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}

function goPost(id: number) {
  router.push(`/post/${id}`)
}

async function loadRelated() {
  const categoryId = post.value?.category?.id
  if (!categoryId) {
    related.value = []
    return
  }
  const res = await getPosts({ pageNum: 1, pageSize: 50, categoryId })
  const list = res.list || []
  // keep current post in the list for highlight; if there are no other posts, hide the sidebar
  if (list.length <= 1) {
    related.value = []
    return
  }
  related.value = list
}

async function loadComments(postId: number) {
  commentsLoading.value = true
  try {
    comments.value = await getPostComments(postId)
  } finally {
    commentsLoading.value = false
  }
}

async function submitComment() {
  if (!post.value) return
  if (!commentForm.nickname.trim() || !commentForm.content.trim()) {
    ElMessage.warning('Nickname and content are required')
    return
  }

  commentSubmitting.value = true
  submitHint.value = ''
  try {
    await createPostComment(post.value.id, {
      nickname: commentForm.nickname,
      email: commentForm.email || undefined,
      content: commentForm.content,
    })
    ElMessage.success('Submitted. Waiting for approval.')
    submitHint.value = 'Your comment is pending approval and will show up after admin approves it.'
    commentForm.content = ''
    // approved list won't change immediately, but we can refresh anyway
    await loadComments(post.value.id)
  } finally {
    commentSubmitting.value = false
  }
}

async function back() {
  // If you came from home list, refresh it to show updated viewCount.
  // (Best-effort: ignore errors)
  try {
    await postsStore.refresh()
  } catch {
    // ignore
  }
  router.back()
}

async function loadPost(id: number) {
  // enable highlight.js only for detail pages
  await enableMarkdownHighlight()
  post.value = await getPostDetail(id)
  rebuildMarkdown()

  // if URL has hash, scroll to it after DOM updates
  setTimeout(() => {
    const h = decodeURIComponent((location.hash || '').replace(/^#/, ''))
    if (h) {
      scrollToHeading(h)
    } else {
      activeHeadingId.value = toc.value[0]?.id || ''
    }
  }, 0)

  if (post.value?.title) {
    const siteName = site.siteName || 'My Blog'
    document.title = `${post.value.title} - ${siteName}`
  }

  await Promise.all([
    loadComments(id),
    loadRelated(),
  ])
}

onMounted(async () => {
  await loadPost(Number(route.params.id))
})

watch(
  () => route.params.id,
  async (id) => {
    const n = Number(id)
    if (!Number.isFinite(n)) return
    await loadPost(n)
  },
)

// re-init observer when content/toc changes
watch(
  () => rendered.value,
  () => {
    setTimeout(() => setupTocObserver(), 0)
  },
)

let tocObserver: IntersectionObserver | null = null

function setupTocObserver() {
  // clean previous
  if (tocObserver) {
    tocObserver.disconnect()
    tocObserver = null
  }
  if (!toc.value.length) return

  const ids = toc.value.map(i => i.id)
  const targets = ids.map(id => document.getElementById(id)).filter(Boolean) as HTMLElement[]
  if (!targets.length) return

  tocObserver = new IntersectionObserver(
    (entries) => {
      // pick the top-most visible heading
      const visible = entries
        .filter(e => e.isIntersecting)
        .sort((a, b) => (a.boundingClientRect.top - b.boundingClientRect.top))
      if (visible[0]) {
        const id = (visible[0].target as HTMLElement).id
        if (id && id !== activeHeadingId.value) {
          activeHeadingId.value = id
          if (history.replaceState) {
            history.replaceState(null, '', `#${encodeURIComponent(id)}`)
          }
        }
      }
    },
    {
      // start highlighting a bit before the heading reaches the top
      root: mainScrollEl.value,
      rootMargin: '-20% 0px -70% 0px',
      threshold: [0, 1],
    },
  )

  for (const el of targets) tocObserver.observe(el)
}
</script>

<style scoped>
/* default (fallback) value for app topbar height; will be overwritten by App.vue runtime */
.post-detail {
  --app-topbar-h: 120px;
}

/* Wide container for post detail page (not limited to global .container 1200px) */
.post-detail__container {
  max-width: 1500px;
  margin: 0 auto;
  padding: 0 16px;
}

/* 3 independent columns; center column doesn't depend on sidebars */
.post-detail__shell {
  display: flex;
  gap: 14px;
  align-items: flex-start;

  /* Sticky is computed within the scroll container; avoid accidental clipping */
  overflow: visible;

  /* Only middle column scrolls; shell itself is fixed height */
  height: calc(100vh - var(--app-topbar-h, 120px));
}

.post-detail__left,
.post-detail__right {
  flex: 0 0 auto;
  width: 260px;
}

.post-detail__main {
  flex: 1 1 auto;
  min-width: 0;

  /* make middle column the only scroll container */
  height: 100%;
  overflow: auto;
  padding-right: 6px; /* leave room for scrollbar */
}

.post-detail__side {
  height: 100%;
}

.post-detail__sideInner {
  height: 100%;
  overflow: hidden; /* left/right should not scroll */
}

/* Middle column scroll UX */
.post-detail__main::-webkit-scrollbar {
  width: 10px;
}

.post-detail__main::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.16);
  border-radius: 10px;
}

.post-detail__main::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 0, 0, 0.28);
}

@media (max-width: 1200px) {
  .post-detail__left {
    display: none;
  }
}

@media (max-width: 860px) {
  .post-detail__right {
    display: none;
  }
}

@media (max-width: 860px) {
  .post-detail__shell {
    height: auto;
  }
  .post-detail__main {
    height: auto;
    overflow: visible;
    padding-right: 0;
  }
}

.post-detail__title {
  margin: 8px 0 4px;
}

.post-detail__meta {
  color: #888;
  margin-bottom: 12px;
}


/* Make back button more visible */
.post-detail__back {
  margin-bottom: 10px;
  border-width: 2px;
  font-weight: 700;
}

.post-detail__back:hover {
  filter: brightness(0.98);
}

.post-detail__back:active {
  transform: translateY(1px);
}

.toc {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.toc__item {
  cursor: pointer;
  color: var(--el-text-color-regular);
  font-size: 13px;
  line-height: 1.4;
  padding: 2px 6px;
  border-radius: 6px;
}

.toc__item:hover {
  color: var(--el-color-primary);
}

.toc__item--active {
  background: var(--el-fill-color-light);
  color: var(--el-color-primary);
  font-weight: 700;
}

.toc__item--l2 {
  padding-left: 0;
}

.toc__item--l3 {
  padding-left: 12px;
}

.toc__item--l4 {
  padding-left: 24px;
}

.related {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.related__item {
  cursor: pointer;
  color: var(--el-text-color-regular);
  font-size: 13px;
  line-height: 1.4;
  padding: 2px 6px;
  border-radius: 6px;
}

.related__item:hover {
  color: var(--el-color-primary);
}

.related__item--active {
  color: var(--el-color-primary);
  font-weight: 700;
  background: var(--el-fill-color-light);
}

.md :deep(p) {
  margin: 0 0 12px;
}

.md :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: 12px 0;
}

.md :deep(th),
.md :deep(td) {
  border: 1px solid var(--el-border-color);
  padding: 8px;
  text-align: left;
}

.md :deep(th) {
  background: var(--el-fill-color-light);
}

.md :deep(input[type='checkbox']) {
  margin-right: 8px;
}

.md :deep(img) {
  max-width: 100%;
  height: auto;
}

.md :deep(pre) {
  background: var(--el-fill-color-light);
  padding: 12px;
  border-radius: 6px;
  overflow: auto;
}

.md :deep(code) {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New', monospace;
}
</style>
