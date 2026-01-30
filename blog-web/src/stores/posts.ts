import { defineStore } from 'pinia'
import { getPosts, type PageResult, type PostListItemVO } from '../api/posts'

export const usePostsStore = defineStore('posts', {
  state: () => ({
    loading: false,
    list: [] as PostListItemVO[],
    total: 0,
    pageNum: 1,
    pageSize: 10,
    keyword: '' as string,
    lastLoadedAt: 0,
  }),
  actions: {
    async fetch() {
      this.loading = true
      try {
        const res: PageResult<PostListItemVO> = await getPosts({
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          keyword: this.keyword || undefined,
        })
        this.list = res.list
        this.total = res.total
        this.lastLoadedAt = Date.now()
      } finally {
        this.loading = false
      }
    },
    async refresh() {
      // keep current page/keyword, just re-fetch
      await this.fetch()
    },
  },
})
