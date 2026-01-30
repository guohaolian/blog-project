<template>
  <div style="padding: 16px; max-width: 900px; margin: 0 auto">
    <el-button text @click="back">Back</el-button>

    <h2 style="margin: 8px 0 4px">{{ post?.title }}</h2>
    <div style="color: #888; margin-bottom: 12px">
      <span v-if="post?.publishedAt">{{ post.publishedAt }}</span>
      <span v-if="post?.category"> · {{ post.category.name }}</span>
      <span v-if="(post?.tags?.length || 0) > 0">
        · Tags: {{ post?.tags?.map(t => t.name).join(', ') }}
      </span>
      <span> · Views: {{ post?.viewCount ?? 0 }}</span>
    </div>

    <el-card v-if="post">
      <!-- M1: keep it simple; later we can add markdown rendering -->
      <pre style="white-space: pre-wrap; margin: 0">{{ post.content }}</pre>
    </el-card>

    <el-empty v-else description="Loading..." />

    <div style="margin-top: 16px">
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
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPostDetail, type PostDetailVO } from '../api/posts'
import { createPostComment, getPostComments, type CommentVO } from '../api/comments'
import { usePostsStore } from '../stores/posts'
import { useSiteStore } from '../stores/site'

const route = useRoute()
const router = useRouter()
const postsStore = usePostsStore()
const site = useSiteStore()

const post = ref<PostDetailVO | null>(null)

const commentsLoading = ref(false)
const comments = ref<CommentVO[]>([])

const commentSubmitting = ref(false)
const submitHint = ref('')
const commentForm = reactive({
  nickname: '',
  email: '',
  content: '',
})

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

onMounted(async () => {
  const id = Number(route.params.id)
  post.value = await getPostDetail(id)

  if (post.value?.title) {
    const siteName = site.siteName || 'My Blog'
    document.title = `${post.value.title} - ${siteName}`
  }

  await loadComments(id)
})
</script>
