<template>
  <div style="padding: 16px; max-width: 900px; margin: 0 auto">
    <el-button text @click="$router.back()">Back</el-button>

    <h2 style="margin: 8px 0 4px">{{ post?.title }}</h2>
    <div style="color: #888; margin-bottom: 12px">
      <span v-if="post?.publishedAt">{{ post.publishedAt }}</span>
      <span v-if="post?.category"> · {{ post.category.name }}</span>
      <span v-if="(post?.tags?.length || 0) > 0">
        · Tags: {{ post?.tags?.map(t => t.name).join(', ') }}
      </span>
    </div>

    <el-card v-if="post">
      <!-- M1: keep it simple; later we can add markdown rendering -->
      <pre style="white-space: pre-wrap; margin: 0">{{ post.content }}</pre>
    </el-card>

    <el-empty v-else description="Loading..." />
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { getPostDetail, type PostDetailVO } from '../api/posts'

const route = useRoute()
const post = ref<PostDetailVO | null>(null)

onMounted(async () => {
  const id = Number(route.params.id)
  post.value = await getPostDetail(id)
})
</script>
