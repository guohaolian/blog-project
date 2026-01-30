<template>
  <div style="padding: 16px">
    <h2>Admin Home</h2>

    <p v-if="auth.user">Welcome, {{ auth.user.displayName || auth.user.username }}.</p>
    <p v-else>You're logged in.</p>

    <el-button type="danger" @click="logout">Logout</el-button>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()

function logout() {
  auth.logout()
  router.replace('/login')
}
</script>
