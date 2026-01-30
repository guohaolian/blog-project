<template>
  <div style="max-width: 360px; margin: 80px auto; padding: 16px">
    <h2 style="margin-bottom: 16px">Admin Login</h2>

    <el-form ref="formRef" :model="form" :rules="rules" @submit.prevent>
      <el-form-item label="Username" prop="username">
        <el-input
          v-model="form.username"
          autocomplete="username"
          @keyup.enter="handleLogin"
        />
      </el-form-item>

      <el-form-item label="Password" prop="password">
        <el-input
          v-model="form.password"
          type="password"
          autocomplete="current-password"
          show-password
          @keyup.enter="handleLogin"
        />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" :loading="loading" @click="handleLogin">Login</el-button>
      </el-form-item>

      <el-alert
        v-if="errorMsg"
        type="error"
        :title="errorMsg"
        show-icon
        :closable="false"
      />

      <el-alert
        style="margin-top: 12px"
        type="info"
        title="Default seed account: admin / admin123"
        show-icon
        :closable="false"
      />
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

const formRef = ref<FormInstance>()

const form = reactive({
  username: 'admin',
  password: 'admin123',
})

const rules: FormRules = {
  username: [{ required: true, message: 'Please input username', trigger: 'blur' }],
  password: [{ required: true, message: 'Please input password', trigger: 'blur' }],
}

const loading = ref(false)
const errorMsg = ref('')

async function handleLogin() {
  errorMsg.value = ''

  const ok = await formRef.value?.validate().catch(() => false)
  if (!ok) return

  loading.value = true
  try {
    await auth.login(form.username, form.password)

    // best-effort preload; guard will also validate token
    try {
      await auth.fetchMe()
    } catch {
      // ignore
    }

    const redirect = (route.query.redirect as string) || '/admin'
    router.replace(redirect)
  } catch (e: any) {
    errorMsg.value = e?.message || 'Login failed'
  } finally {
    loading.value = false
  }
}
</script>
