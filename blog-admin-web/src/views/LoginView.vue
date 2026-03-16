<template>
  <div class="login-page">
    <div class="login-bg" aria-hidden="true" />
    <div class="login-bg-grid" aria-hidden="true" />
    <div class="login-bg-noise" aria-hidden="true" />

    <div class="login-left" aria-hidden="true">
      <div class="login-left-inner">
        <div class="login-left-brand">
          <div class="login-left-title">Blog Admin</div>
          <div class="login-left-subtitle">Sign in to continue</div>
        </div>

        <AnimatedCharacters :isTyping="isTyping" :showPassword="showPassword" :passwordLength="form.password.length"
          :loginFailed="loginFailed" :loginSuccess="loginSuccess" />
      </div>
    </div>

    <div class="login-right">

      <div class="login-card" role="main">
        <div class="login-theme">
          <el-tooltip :content="theme.isDark ? 'Switch to light' : 'Switch to dark'" placement="bottom">
            <el-button circle text class="login-theme-btn" @click="theme.toggle()">
              <el-icon size="18">
                <component :is="theme.isDark ? Sunny : Moon" />
              </el-icon>
            </el-button>
          </el-tooltip>
        </div>

        <div class="login-brand">
          <div class="login-title">Blog Admin</div>
          <div class="login-subtitle">Sign in to continue</div>
        </div>

        <el-form ref="formRef" class="login-form" :model="form" :rules="rules" label-position="top" @submit.prevent>
          <el-form-item label="Username" prop="username">
            <el-input ref="usernameInputRef" v-model="form.username" size="large" autocomplete="username"
              @focus="isTyping = true" @blur="isTyping = false" @keyup.enter="handleLogin" />
          </el-form-item>

          <el-form-item label="Password" prop="password">
            <el-input v-model="form.password" size="large" :type="showPassword ? 'text' : 'password'"
              autocomplete="current-password" @focus="isTyping = true" @blur="isTyping = false"
              @keyup.enter="handleLogin">
              <template #suffix>
                <el-icon class="login-password-toggle" @mousedown.prevent @click="showPassword = !showPassword">
                  <component :is="showPassword ? View : Hide" />
                </el-icon>
              </template>
            </el-input>
          </el-form-item>

          <div class="login-messages">
            <el-alert v-if="errorMsg" type="error" :title="errorMsg" show-icon :closable="false" />

            <el-alert v-if="showSeedHint" class="login-hint" type="info" title="Default seed account: admin / admin123"
              show-icon :closable="false" />
          </div>

          <el-button class="login-submit" type="primary" size="large" :loading="loading" @click="handleLogin">
            Login
          </el-button>
        </el-form>

        <div class="login-footer">
          <span>© {{ new Date().getFullYear() }} Blog</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, nextTick, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { useAuthStore } from '../stores/auth'
import { useThemeStore } from '../stores/theme'
import { Moon, Sunny, View, Hide } from '@element-plus/icons-vue'
import AnimatedCharacters from '../components/animated-login/AnimatedCharacters.vue'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()
const theme = useThemeStore()

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

const isTyping = ref(false)
const showPassword = ref(false)
const loginFailed = ref(false)
const loginSuccess = ref(false)

let failedTimer: number | undefined
let successTimer: number | undefined

const usernameInputRef = ref<{ focus?: () => void }>()

onMounted(async () => {
  await nextTick()
  usernameInputRef.value?.focus?.()
})

async function handleLogin() {
  errorMsg.value = ''
  loginFailed.value = false
  loginSuccess.value = false

  if (failedTimer) window.clearTimeout(failedTimer)
  if (successTimer) window.clearTimeout(successTimer)

  const ok = await formRef.value?.validate().catch(() => false)
  if (!ok) return

  loading.value = true
  try {
    await auth.login(form.username, form.password)

    loginSuccess.value = true
    successTimer = window.setTimeout(() => {
      loginSuccess.value = false
    }, 6000)

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

    loginFailed.value = true
    failedTimer = window.setTimeout(() => {
      loginFailed.value = false
    }, 3000)
  } finally {
    loading.value = false
  }
}

const showSeedHint = import.meta.env.DEV
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 1fr 1fr;
  align-items: stretch;
  padding: 0;
  position: relative;
  overflow: hidden;
}

.login-left {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: stretch;
  justify-content: flex-end;
  padding: 42px 100px 42px 24px;
}

.login-left-inner {
  width: min(720px, 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.login-left-brand {
  position: absolute;
  top: 24px;
  left: 0;
  right: 0;
  display: grid;
  gap: 6px;
  color: var(--admin-text);
  text-align: center;
}

.login-left-title {
  font-size: 20px;
  font-weight: 800;
  letter-spacing: 0.2px;
}

.login-left-subtitle {
  font-size: 13px;
  color: var(--admin-muted);
}

.login-right {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding: 24px 24px 24px 100px;
}

.login-bg {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(1100px 680px at 18% 18%, rgba(79, 70, 229, 0.26), transparent 58%),
    radial-gradient(900px 560px at 86% 30%, rgba(99, 102, 241, 0.18), transparent 62%),
    radial-gradient(900px 700px at 55% 115%, rgba(14, 165, 233, 0.10), transparent 55%),
    linear-gradient(180deg, var(--admin-bg-0), var(--admin-bg-1));
}

/* subtle grid to add depth (very light) */
.login-bg-grid {
  position: absolute;
  inset: 0;
  opacity: 0.45;
  background-image:
    linear-gradient(to right, rgba(15, 23, 42, 0.05) 1px, transparent 1px),
    linear-gradient(to bottom, rgba(15, 23, 42, 0.05) 1px, transparent 1px);
  background-size: 48px 48px;
  mask-image: radial-gradient(900px 520px at 50% 35%, #000 60%, transparent 100%);
  pointer-events: none;
}

/* tiny noise overlay to avoid banding and make it more premium */
.login-bg-noise {
  position: absolute;
  inset: 0;
  opacity: 0.06;
  background-image: url('data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 width=%22120%22 height=%22120%22 viewBox=%220 0 120 120%22%3E%3Cfilter id=%22n%22%3E%3CfeTurbulence type=%22fractalNoise%22 baseFrequency=%220.9%22 numOctaves=%222%22 stitchTiles=%22stitch%22/%3E%3C/filter%3E%3Crect width=%22120%25%22 height=%22120%25%22 filter=%22url(%23n)%22 opacity=%221%22/%3E%3C/svg%3E');
  pointer-events: none;
}

.login-card {
  position: relative;
  width: 100%;
  max-width: 420px;
  background: var(--admin-surface);
  border: 1px solid var(--admin-border);
  border-radius: var(--admin-radius);
  box-shadow: var(--admin-shadow);
  backdrop-filter: blur(10px);
  padding: 30px;
}

.login-theme {
  position: absolute;
  top: 12px;
  right: 12px;
}

.login-theme-btn {
  color: var(--admin-muted);
}

.login-theme-btn:hover {
  color: var(--admin-text);
  background: rgba(127, 127, 127, 0.08);
}

/* soft glow behind the card */
.login-card::before {
  content: '';
  position: absolute;
  inset: -22px;
  border-radius: calc(var(--admin-radius) + 18px);
  background: radial-gradient(420px 260px at 30% 20%, rgba(79, 70, 229, 0.22), transparent 60%);
  filter: blur(10px);
  opacity: 0.9;
  z-index: -1;
}

.login-brand {
  text-align: left;
  margin-bottom: 18px;
}

.login-title {
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 0.2px;
  color: var(--admin-text);
}

.login-subtitle {
  margin-top: 6px;
  font-size: 13px;
  color: var(--admin-muted);
}

.login-form {
  margin-top: 12px;
}

.login-password-toggle {
  cursor: pointer;
  color: var(--admin-muted);
}

.login-password-toggle:hover {
  color: var(--admin-text);
}

.login-messages {
  display: grid;
  gap: 10px;
  margin: 6px 0 14px;
}

.login-hint {
  opacity: 0.92;
}

.login-hint :deep(.el-alert__description) {
  color: var(--admin-muted);
}

.login-submit {
  width: 100%;
}

.login-footer {
  margin-top: 16px;
  font-size: 12px;
  color: var(--admin-muted);
  text-align: center;
}

@media (max-height: 780px) {
  .login-left-inner {
    padding-top: 8px;
    padding-bottom: 8px;
  }

  .login-left :deep(.animated-characters-container) {
    transform: scale(0.9);
    transform-origin: center bottom;
  }
}

@media (max-width: 1024px) {
  .login-page {
    grid-template-columns: 1fr;
  }

  .login-left {
    display: none;
  }
}
</style>
