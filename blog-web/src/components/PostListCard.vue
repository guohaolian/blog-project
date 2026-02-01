<template>
  <el-card class="post-card" :class="{ 'post-card--with-cover': !!post.coverUrl }">
    <div class="post-card__row">
      <router-link v-if="post.coverUrl" :to="to" class="post-card__cover">
        <img :src="post.coverUrl" alt="cover" loading="lazy" />
      </router-link>

      <div class="post-card__main">
        <div class="post-card__titleRow">
          <router-link :to="to" class="post-card__title">
            {{ post.title }}
          </router-link>

          <div v-if="showViews" class="post-card__badge">
            Views: {{ post.viewCount ?? 0 }}
          </div>
        </div>

        <div class="post-card__meta">
          <span v-if="post.publishedAt">{{ post.publishedAt }}</span>
          <span v-if="post.category"> · {{ post.category.name }}</span>
          <span v-if="showTags && (post.tags?.length || 0) > 0">
            · Tags: {{ (post.tags || []).map(t => t.name).join(', ') }}
          </span>
        </div>

        <div v-if="post.summary" class="post-card__summary">{{ post.summary }}</div>

        <slot />
      </div>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import type { PostListItemVO } from '../api/posts'

type Props = {
  post: PostListItemVO
  showViews?: boolean
  showTags?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  showViews: true,
  showTags: false,
})

const to = `/post/${props.post.id}`
</script>

<style scoped>
.post-card {
  border-radius: var(--app-radius);
  transition: transform 160ms ease, box-shadow 160ms ease;
  margin-bottom: 14px;
}

.post-card:hover {
  transform: translateY(-1px);
}

.post-card__row {
  display: flex;
  gap: 14px;
  align-items: stretch;
  min-height: 200px;
}

.post-card__cover {
  flex: 0 0 240px;
  width: 240px;
  border-radius: 12px;
  overflow: hidden;
  background: var(--el-fill-color-light);
}

.post-card__cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 220ms ease;
}

.post-card:hover .post-card__cover img {
  transform: scale(1.03);
}

.post-card__main {
  min-width: 0;
  flex: 1 1 auto;
  padding-top: 2px;
}

.post-card__titleRow {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.post-card__title {
  display: inline-block;
  font-size: 20px;
  font-weight: 800;
  color: var(--el-text-color-primary);
  letter-spacing: -0.01em;
  line-height: 1.25;
}

.post-card__title:hover {
  text-decoration: none;
}

.post-card__badge {
  flex: 0 0 auto;
  font-size: 12px;
  color: var(--el-text-color-secondary);
  white-space: nowrap;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(0, 0, 0, 0.04);
}

html.dark .post-card__badge {
  background: rgba(255, 255, 255, 0.08);
}

.post-card__meta {
  color: var(--el-text-color-secondary);
  margin: 8px 0 10px;
  font-size: 13px;
}

.post-card__summary {
  color: var(--el-text-color-regular);
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.7;
}

@media (max-width: 680px) {
  .post-card__row {
    flex-direction: column;
    min-height: 0;
  }
  .post-card__cover {
    width: 100%;
    flex-basis: auto;
    height: 180px;
  }
  .post-card__titleRow {
    flex-direction: column;
  }
  .post-card__badge {
    align-self: flex-start;
  }
}
</style>
