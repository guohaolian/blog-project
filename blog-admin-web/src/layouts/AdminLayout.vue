<template>
  <el-container style="min-height: 100vh">
    <el-aside width="200px" style="border-right: 1px solid var(--el-border-color)">
      <div style="padding: 12px 16px; font-weight: 600">Blog Admin</div>
      <el-menu :default-active="activePath" router>
        <el-menu-item index="/admin">Home</el-menu-item>
        <el-menu-item index="/admin/posts">Posts</el-menu-item>
        <el-menu-item index="/admin/posts/new">New Post</el-menu-item>
        <el-menu-item index="/admin/categories">Categories</el-menu-item>
        <el-menu-item index="/admin/tags">Tags</el-menu-item>
        <el-menu-item index="/admin/comments">Comments</el-menu-item>
        <el-menu-item index="/admin/resources">Resources</el-menu-item>
        <el-menu-item index="/admin/settings">Settings</el-menu-item>
        <el-menu-item index="/admin/admins">Admins</el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header style="display: flex; align-items: center; justify-content: flex-end; gap: 12px">
        <span v-if="auth.user" style="color: var(--el-text-color-regular)">
          {{ auth.user.displayName || auth.user.username }}
        </span>
        <el-button size="small" type="danger" @click="logout">Logout</el-button>
      </el-header>

      <el-main style="padding: 0">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const activePath = computed(() => route.path)

function logout() {
  auth.logout()
  router.replace('/login')
}
</script>
