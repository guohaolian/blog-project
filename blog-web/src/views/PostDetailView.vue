<template>
  <div
    class="post-detail"
    :class="{
      'post-detail--single': forceSingleColumn,
      'post-detail--no-sides': !hasAnySidebar,
      'post-detail--has-left': hasLeftSidebar,
      'post-detail--has-right': hasRightSidebar,
    }"
  >
    <div class="post-detail__container" ref="containerEl">
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
            <div class="post-hero">
              <div class="post-hero__top">
                <el-button class="post-detail__back" type="primary" plain @click="back">
                  <el-icon style="margin-right: 6px"><ArrowLeft /></el-icon>
                  Back
                </el-button>
              </div>

              <h2 class="post-detail__title">{{ post?.title }}</h2>
              <div class="post-detail__meta">
                <span v-if="post?.publishedAt">{{ post.publishedAt }}</span>
                <span v-if="post?.category"> · {{ post.category.name }}</span>
                <span v-if="(post?.tags?.length || 0) > 0">
                  · Tags: {{ post?.tags?.map(t => t.name).join(', ') }}
                </span>
                <span> · Views: {{ post?.viewCount ?? 0 }}</span>
              </div>

              <div v-if="post?.coverUrl" class="post-hero__cover">
                <img :src="post.coverUrl" alt="cover" />
              </div>
            </div>
          </div>

          <div class="post-detail__content">
            <el-card v-if="post" shadow="never">
              <el-alert
                v-if="mdImageBrokenCount > 0"
                type="warning"
                :title="`${mdImageBrokenCount} image(s) failed to load (maybe deleted).`"
                show-icon
                class="post-detail__alert"
              />

              <div class="md" v-html="rendered" />
            </el-card>

            <el-empty v-else description="Loading..." />
          </div>

          <div class="post-detail__content" style="margin-top: 16px">
            <h3 class="post-detail__sectionTitle">Comments</h3>

            <el-card class="post-detail__comments" v-loading="commentsLoading" shadow="never">
              <el-empty v-if="!commentsLoading && comments.length === 0" description="No comments" />
              <div v-else>
                <div v-for="c in comments" :key="c.id" class="comment">
                  <div class="comment__top">
                    <div class="comment__name">{{ c.nickname }}</div>
                    <div class="comment__time">{{ c.createdAt }}</div>
                  </div>
                  <div class="comment__content">{{ c.content }}</div>
                </div>
              </div>
            </el-card>

            <el-card shadow="never">
              <template #header>
                <div class="post-detail__cardHeader">
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

              <div class="post-detail__actions">
                <el-button type="primary" :loading="commentSubmitting" @click="submitComment">Submit</el-button>
              </div>

              <div v-if="submitHint" class="post-detail__hint">
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
import { computed, nextTick, onMounted, onBeforeUnmount, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPostDetail, getPosts, type PostDetailVO, type PostListItemVO } from '../api/posts'
import { createPostComment, getPostComments, type CommentVO } from '../api/comments'
import { usePostsStore } from '../stores/posts'
import { useSiteStore } from '../stores/site'
import { ArrowLeft } from '@element-plus/icons-vue'

// markdown utils are lazy-loaded so markdown/highlight libs don't bloat the initial bundle
import type { TocItem } from '../utils/markdown'

const route = useRoute()
const router = useRouter()
const postsStore = usePostsStore()
const site = useSiteStore()

const post = ref<PostDetailVO | null>(null)

const toc = ref<TocItem[]>([])
const rendered = ref('')
const mdImageBrokenCount = ref(0)
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
const containerEl = ref<HTMLElement | null>(null)

// Container-width responsive switching
const forceSingleColumn = ref(false)
let ro: ResizeObserver | null = null

const hasLeftSidebar = computed(() => (toc.value?.length || 0) > 0)
const hasRightSidebar = computed(() => (related.value?.length || 0) > 0)
const hasAnySidebar = computed(() => hasLeftSidebar.value || hasRightSidebar.value)
const sidebarCount = computed(() => (hasLeftSidebar.value ? 1 : 0) + (hasRightSidebar.value ? 1 : 0))

function updateResponsiveByContainerWidth() {
  // If there's no sidebar at all, keep the centered single-column layout.
  if (!hasAnySidebar.value) {
    forceSingleColumn.value = false
    return
  }

  const el = containerEl.value
  if (!el) return
  const w = el.getBoundingClientRect().width

  // Requirement:
  // - Desktop: show sidebars + main(900px).
  // - When container becomes too narrow to keep: main(900) + sideMin*count + gap*count,
  //   hide sidebars and show only main.
  const sideMin = 180
  const mainW = 900
  const gap = 14
  const count = sidebarCount.value
  const minRequired = mainW + sideMin * count + gap * count

  forceSingleColumn.value = w < minRequired
}

let mdApi: null | {
  enableMarkdownHighlight: () => Promise<void>
  renderMarkdownWithToc: (content: string) => { html: string; toc: TocItem[] }
} = null

async function ensureMarkdownApi() {
  if (mdApi) return mdApi
  const m = await import('../utils/markdown')
  mdApi = {
    enableMarkdownHighlight: m.enableMarkdownHighlight,
    renderMarkdownWithToc: m.renderMarkdownWithToc,
  }
  return mdApi
}

async function rebuildMarkdown() {
  if (!post.value) {
    rendered.value = ''
    toc.value = []
    mdImageBrokenCount.value = 0
    return
  }

  const api = await ensureMarkdownApi()
  const { html, toc: t } = api.renderMarkdownWithToc(post.value.content || '')
  rendered.value = html
  toc.value = t
}

function bindMarkdownImageFallback() {
  mdImageBrokenCount.value = 0
  const container = document.querySelector('.post-detail .md') as HTMLElement | null
  if (!container) return

  const imgs = Array.from(container.querySelectorAll('img')) as HTMLImageElement[]
  for (const img of imgs) {
    // avoid double binding
    if ((img as any).__boundImgErr) continue
    ;(img as any).__boundImgErr = true

    img.addEventListener('error', () => {
      // hide broken image to avoid ugly broken icon
      if (img.style.display !== 'none') {
        img.style.display = 'none'
        mdImageBrokenCount.value += 1
      }
    })
  }
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
  try {
    post.value = await getPostDetail(id)
  } catch (e: any) {
    // getPostDetail rejects with Error(message) via axios interceptor
    ElMessage.error(e?.message || 'Failed to load post')
    post.value = null
    rendered.value = ''
    toc.value = []
    // Navigate away so user doesn't stay on a blank detail page.
    await router.replace('/')
    return
  }

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
  // enable highlight only on this page
  try {
    const api = await ensureMarkdownApi()
    await api.enableMarkdownHighlight()
  } catch {
    // ignore highlight failures (markdown should still render)
  }

  // Container-width responsive switching (only meaningful when sidebars exist)
  try {
    ro = new ResizeObserver(() => updateResponsiveByContainerWidth())
    if (containerEl.value) ro.observe(containerEl.value)
    updateResponsiveByContainerWidth()
  } catch {
    // ResizeObserver unsupported
  }

  await loadPost(Number(route.params.id))
})

onBeforeUnmount(() => {
  if (ro) {
    ro.disconnect()
    ro = null
  }
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
    // bind after DOM updated
    nextTick(() => bindMarkdownImageFallback())
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
.post-detail{
  --app-topbar-h:120px;

  /* layout constants (keep in sync with grid + container styles) */
  --pd-side-min: 180px;
  --pd-main-width: 900px;
  --pd-gap: 14px;
  --pd-container-pad: 18px;

  /* If viewport can't fit: leftMin + main + rightMin + gaps + padding, hide sidebars */
  --pd-breakpoint-hide-sides: calc(
    (var(--pd-side-min) * 2) +
    var(--pd-main-width) +
    (var(--pd-gap) * 2) +
    (var(--pd-container-pad) * 2)
  );
}

/* No sidebars at all: use a centered single column that fills available width up to 900px. */
.post-detail--no-sides .post-detail__shell {
  grid-template-columns: 1fr;
  justify-content: center;
}

.post-detail--no-sides .post-detail__main {
  width: 100%;
  max-width: var(--pd-main-width);
  margin: 0 auto;
  padding-right: 0;
}

/* Force single column based on container width (ResizeObserver). */
.post-detail--single .post-detail__left,
.post-detail--single .post-detail__right {
  display: none;
}

.post-detail--single .post-detail__shell {
  grid-template-columns: 1fr;
  justify-content: center;
}

.post-detail--single .post-detail__main {
  width: 100%;
  max-width: var(--pd-main-width);
  margin: 0 auto;
  padding-right: 0;
}

/* Responsive strategy:
   - Desktop: grid template depends on which sidebars exist.
   - Side columns can shrink (down to --pd-side-min).
   - Main column stays fixed at --pd-main-width as long as sidebars are visible.
   - If container becomes too narrow (computed in JS), .post-detail--single hides sidebars.
*/

/* Desktop enhancement: keep only the middle column scrollable for 2/3-column layouts.
   This avoids changing overall app scroll position when browsing toc/related.
*/
@media (min-width: 769px) {
  /* keep main fixed when sidebars exist */
  .post-detail--has-left:not(.post-detail--single) .post-detail__main,
  .post-detail--has-right:not(.post-detail--single) .post-detail__main {
    width: var(--pd-main-width);
    max-width: var(--pd-main-width);
    margin: 0;
  }

  /* single column: let it shrink with viewport */
  .post-detail--single .post-detail__main,
  .post-detail--no-sides .post-detail__main {
    width: 100%;
    max-width: var(--pd-main-width);
    margin: 0 auto;
  }

  /* Two sidebars */
  .post-detail--has-left.post-detail--has-right:not(.post-detail--single) .post-detail__shell {
    grid-template-columns:
      clamp(var(--pd-side-min), 18vw, 260px)
      var(--pd-main-width)
      clamp(var(--pd-side-min), 18vw, 260px);
  }

  /* Only left sidebar */
  .post-detail--has-left:not(.post-detail--has-right):not(.post-detail--single) .post-detail__shell {
    grid-template-columns:
      clamp(var(--pd-side-min), 18vw, 260px)
      var(--pd-main-width);
  }

  /* Only right sidebar */
  .post-detail--has-right:not(.post-detail--has-left):not(.post-detail--single) .post-detail__shell {
    grid-template-columns:
      var(--pd-main-width)
      clamp(var(--pd-side-min), 18vw, 260px);
  }
}

/* Mobile: keep the page fully fluid */
@media (max-width: 768px) {
  .post-detail__container {
    max-width: 100%;
    width: 100%;
    padding-left: 12px;
    padding-right: 12px;
  }

  .post-detail__content {
    width: 100%;
  }

  /* make long words/links/code not blow up width */
  .post-detail :deep(.md),
  .post-detail :deep(.el-card) {
    overflow-wrap: anywhere;
    word-break: break-word;
  }

  .post-detail :deep(pre),
  .post-detail :deep(code) {
    white-space: pre-wrap;
  }
}

.post-detail__container {
  max-width: 1500px;
  margin: 0 auto;
  padding: 16px var(--pd-container-pad);
}

/* Base shell grid (must exist regardless of sidebars). */
.post-detail__shell {
  display: grid;
  align-items: start;
  gap: var(--pd-gap);

  /* Default: single column; modifiers will override */
  grid-template-columns: 1fr;
  justify-content: center;

  /* Default: natural page flow */
  height: auto;
  overflow: visible;
}

/* Extra safety: prevent any layout from causing horizontal overflow.
   This is the root cause of the 'left thin strip + huge blank area' when the viewport is narrow.
*/
.post-detail {
  overflow-x: hidden;
}

.post-detail__shell,
.post-detail__main {
  min-width: 0;
}

.post-detail__content {
  min-width: 0;
}

.post-hero {
  border-radius: var(--app-radius);
  border: 1px solid var(--el-border-color);
  background: rgba(255, 255, 255, 0.60);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  padding: 14px;
}

html.dark .post-hero {
  background: rgba(18, 20, 24, 0.55);
}

.post-hero__top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.post-hero__cover {
  margin-top: 12px;
  border-radius: 12px;
  overflow: hidden;
  background: var(--el-fill-color-light);
}

.post-hero__cover img {
  width: 100%;
  max-height: 360px;
  object-fit: cover;
  display: block;
}

.post-detail__title {
  margin: 6px 0 4px;
  font-size: 28px;
  font-weight: 900;
  letter-spacing: -0.02em;
  line-height: 1.2;
}

.post-detail__meta {
  color: var(--el-text-color-secondary);
  margin-bottom: 8px;
  font-size: 13px;
}

.post-detail__sectionTitle {
  margin: 0 0 10px;
  font-weight: 900;
  letter-spacing: -0.01em;
}

.post-detail__alert {
  margin-bottom: 12px;
}

.comment {
  padding: 10px 0;
  border-bottom: 1px dashed var(--el-border-color);
}

.comment:last-child {
  border-bottom: none;
}

.comment__top {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  gap: 12px;
}

.comment__name {
  font-weight: 800;
}

.comment__time {
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.comment__content {
  margin-top: 6px;
  white-space: pre-wrap;
  line-height: 1.7;
}

.post-detail__cardHeader {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.post-detail__actions {
  display: flex;
  justify-content: flex-end;
}

.post-detail__hint {
  margin-top: 8px;
  color: var(--el-text-color-secondary);
}

/* Make back button more visible */
.post-detail__back {
  border-width: 2px;
  font-weight: 700;
}

.post-detail__back:hover {
  filter: brightness(0.98);
}

.post-detail__back:active {
  transform: translateY(1px);
}

/* keep existing toc/related/md styles below */

.post-detail__side {
  height: 100%;
}

.post-detail__sideInner {
  height: 100%;
  overflow: hidden; /* left/right should not scroll */
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
  padding: 4px 8px;
  border-radius: 10px;
  transition: background 140ms ease, color 140ms ease;
}

.toc__item:hover {
  color: var(--el-color-primary);
  background: rgba(64, 158, 255, 0.10);
}

.toc__item--active {
  background: var(--el-fill-color-light);
  color: var(--el-color-primary);
  font-weight: 800;
}

.toc__item--l2 {
  padding-left: 8px;
}

.toc__item--l3 {
  padding-left: 18px;
}

.toc__item--l4 {
  padding-left: 30px;
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
  padding: 4px 8px;
  border-radius: 10px;
  transition: background 140ms ease, color 140ms ease;
}

.related__item:hover {
  color: var(--el-color-primary);
  background: rgba(64, 158, 255, 0.10);
}

.related__item--active {
  color: var(--el-color-primary);
  font-weight: 800;
  background: var(--el-fill-color-light);
}

/* Markdown polish */
.md :deep(p) {
  margin: 0 0 12px;
}

.md :deep(h2) {
  margin: 18px 0 10px;
  padding-top: 6px;
  font-size: 20px;
  font-weight: 900;
  letter-spacing: -0.01em;
}

.md :deep(h3) {
  margin: 16px 0 8px;
  font-size: 17px;
  font-weight: 900;
}

.md :deep(blockquote) {
  margin: 12px 0;
  padding: 10px 12px;
  border-left: 4px solid var(--el-color-primary);
  background: rgba(64, 158, 255, 0.08);
  border-radius: 10px;
}

html.dark .md :deep(blockquote) {
  background: rgba(64, 158, 255, 0.12);
}

.md :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: 12px 0;
  overflow: hidden;
  border-radius: 10px;
}

.md :deep(th),
.md :deep(td) {
  border: 1px solid var(--el-border-color);
  padding: 10px;
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
  border-radius: 10px;
}

.md :deep(pre) {
  background: var(--el-fill-color-light);
  padding: 12px;
  border-radius: 10px;
  overflow: auto;
  border: 1px solid var(--el-border-color);
}

.md :deep(code) {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New', monospace;
}

.md :deep(p > code) {
  background: var(--el-fill-color-light);
  border: 1px solid var(--el-border-color);
  padding: 2px 6px;
  border-radius: 8px;
}
</style>
