<template>
  <div class="page">
    <div class="container">
      <div class="page__header">
        <h2 class="page__title">Category Posts</h2>
        <el-button class="page__action" text @click="$router.push('/categories')">All Categories</el-button>
      </div>

      <PostListCard
        v-for="p in list"
        :key="p.id"
        :post="p"
        :show-views="true"
        class="page__card"
        v-loading="loading"
      />

      <el-empty v-if="!loading && list.length === 0" description="No posts" />

      <div class="page__pager">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next"
          :current-page="pageNum"
          :page-size="pageSize"
          :page-sizes="[5, 10, 20, 50]"
          :total="total"
          @current-change="onPageChange"
          @size-change="onPageSizeChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getPosts, type PostListItemVO } from '../api/posts'
import PostListCard from '../components/PostListCard.vue'

const route = useRoute()

const loading = ref(false)
const list = ref<PostListItemVO[]>([])
const total = ref(0)

const pageNum = ref(1)
const pageSize = ref(10)

function onPageChange(p: number) {
  pageNum.value = p
  fetchList()
}

function onPageSizeChange(ps: number) {
  pageSize.value = ps
  pageNum.value = 1
  fetchList()
}

async function fetchList() {
  loading.value = true
  try {
    const categoryId = Number(route.params.id)
    const res = await getPosts({ pageNum: pageNum.value, pageSize: pageSize.value, categoryId })
    list.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

onMounted(fetchList)

watch(
  () => route.params.id,
  () => {
    pageNum.value = 1
    fetchList()
  },
)
</script>

<style scoped>
.page {
  padding: 18px 10px 32px;
}

.page__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;
}

.page__title {
  margin: 0;
  font-size: 22px;
  font-weight: 800;
  letter-spacing: -0.01em;
}

.page__pager {
  margin-top: 14px;
  display: flex;
  justify-content: center;
}
</style>
