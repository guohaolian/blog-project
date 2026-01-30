<template>
  <div style="padding: 16px">
    <div style="display: flex; align-items: center; justify-content: space-between; gap: 12px; margin-bottom: 12px">
      <h2 style="margin: 0">Settings</h2>
      <div style="display: flex; gap: 8px">
        <el-button size="small" :loading="loading" @click="load">Reload</el-button>
        <el-button size="small" type="primary" :loading="saving" @click="save">Save</el-button>
      </div>
    </div>

    <el-form :model="form" label-width="120px" v-loading="loading">
      <el-form-item label="Site name">
        <el-input v-model="form.siteName" maxlength="100" />
      </el-form-item>

      <el-form-item label="Site notice">
        <el-input v-model="form.siteNotice" maxlength="255" />
      </el-form-item>

      <el-form-item label="About content">
        <el-input v-model="form.aboutContent" type="textarea" :rows="6" />
      </el-form-item>

      <el-form-item label="Links (JSON)">
        <el-input v-model="form.linksJson" type="textarea" :rows="4" placeholder='[{"name":"GitHub","url":"https://github.com"}]' />
        <div style="margin-top: 6px; color: var(--el-text-color-secondary); font-size: 12px">
          Tip: keep it as JSON array string for simplicity.
        </div>
      </el-form-item>

      <el-divider />

      <el-form-item label="SEO title">
        <el-input v-model="form.seoTitle" maxlength="255" />
      </el-form-item>

      <el-form-item label="SEO keywords">
        <el-input v-model="form.seoKeywords" maxlength="255" />
      </el-form-item>

      <el-form-item label="SEO description">
        <el-input v-model="form.seoDescription" maxlength="255" />
      </el-form-item>

      <el-form-item label="Footer text">
        <el-input v-model="form.footerText" maxlength="255" />
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { adminGetSite, adminUpdateSite } from '../api/site'

const loading = ref(false)
const saving = ref(false)

const form = reactive({
  siteName: '',
  siteNotice: '',
  aboutContent: '',
  linksJson: '[]',
  seoTitle: '',
  seoKeywords: '',
  seoDescription: '',
  footerText: '',
})

async function load() {
  loading.value = true
  try {
    const s = await adminGetSite()
    form.siteName = s.siteName || ''
    form.siteNotice = s.siteNotice || ''
    form.aboutContent = s.aboutContent || ''
    form.linksJson = s.linksJson || '[]'
    form.seoTitle = s.seoTitle || ''
    form.seoKeywords = s.seoKeywords || ''
    form.seoDescription = s.seoDescription || ''
    form.footerText = s.footerText || ''
  } finally {
    loading.value = false
  }
}

async function save() {
  if (!form.siteName.trim()) {
    ElMessage.warning('Site name is required')
    return
  }
  // basic JSON validation for links
  if (form.linksJson && form.linksJson.trim()) {
    try {
      const v = JSON.parse(form.linksJson)
      if (!Array.isArray(v)) {
        ElMessage.error('Invalid linksJson: must be a JSON array')
        return
      }
    } catch (e: any) {
      ElMessage.error(`Invalid linksJson: ${e?.message || e}`)
      return
    }
  }

  saving.value = true
  try {
    await adminUpdateSite({ ...form })
    ElMessage.success('Saved')
    await load()
  } finally {
    saving.value = false
  }
}

onMounted(load)
</script>
