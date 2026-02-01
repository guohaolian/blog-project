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

      <el-form-item label="Home banner">
        <div style="display:flex; flex-direction:column; gap:10px; width: 100%">
          <div style="display:flex; align-items:center; gap:10px; flex-wrap: wrap">
            <el-upload
              :show-file-list="false"
              :auto-upload="false"
              accept="image/*"
              :on-change="onBannerFileChange"
            >
              <el-button type="primary">Upload banner</el-button>
            </el-upload>

            <el-button v-if="form.bannerUrl" @click="onClearBanner">Clear</el-button>
            <el-input v-model="form.bannerUrl" placeholder="Or paste /uploads/... URL" />
          </div>

          <div v-if="form.bannerUrl" style="border: 1px solid var(--el-border-color); border-radius: 10px; overflow: hidden; max-width: 720px">
            <img :src="form.bannerUrl" alt="banner" style="display:block; width: 100%; height: 220px; object-fit: cover" />
          </div>
          <div style="color: var(--el-text-color-secondary); font-size: 12px">
            Tip: Banner will show as full-screen hero on Home page. Uploaded image will also appear in Resources.
          </div>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminGetSite, adminUpdateSite } from '../api/site'
import { adminUploadImage } from '../api/upload'
import { adminResourceDelete, adminResourcePage } from '../api/resources'

const loading = ref(false)
const saving = ref(false)

const form = reactive({
  siteName: '',
  siteNotice: '',
  aboutContent: '',
  linksJson: '[]',
  bannerUrl: '',
  seoTitle: '',
  seoKeywords: '',
  seoDescription: '',
  footerText: '',
})

const loadedBannerUrl = ref('')
const lastUploadedBannerUrl = ref('')

async function onBannerFileChange(uploadFile: any) {
  const f: File | undefined = uploadFile?.raw
  if (!f) return
  saving.value = true
  try {
    const r = await adminUploadImage(f)
    form.bannerUrl = r.url
    lastUploadedBannerUrl.value = r.url
    ElMessage.success('Banner uploaded')
  } finally {
    saving.value = false
  }
}

async function deleteResourceByUrl(url: string) {
  // find by keyword(url) then delete first exact match
  const page = await adminResourcePage({ pageNum: 1, pageSize: 10, keyword: url, contentTypePrefix: 'image/' })
  const hit = (page.list || []).find((x) => x.url === url)
  if (!hit) return false
  await adminResourceDelete(hit.id)
  return true
}

async function onClearBanner() {
  const current = form.bannerUrl
  form.bannerUrl = ''

  // Only offer deletion when:
  // - this banner was uploaded in this session
  // - and it wasn't already saved as the site's banner when we loaded
  const isNewUpload = !!current && current === lastUploadedBannerUrl.value
  const wasSaved = !!current && current === loadedBannerUrl.value
  if (!isNewUpload || wasSaved) return

  try {
    await ElMessageBox.confirm(
      'Do you also want to delete this uploaded image from Resources? This will remove the file_resource record and try to delete the file from disk.',
      'Delete resource?',
      { type: 'warning', confirmButtonText: 'Delete', cancelButtonText: 'Keep' },
    )
    const ok = await deleteResourceByUrl(current)
    if (ok) ElMessage.success('Resource deleted')
    else ElMessage.info('Resource not found (maybe already deleted)')
  } catch {
    // user cancelled
  }
}

async function load() {
  loading.value = true
  try {
    const s = await adminGetSite()
    form.siteName = s.siteName || ''
    form.siteNotice = s.siteNotice || ''
    form.aboutContent = s.aboutContent || ''
    form.linksJson = s.linksJson || '[]'
    form.bannerUrl = s.bannerUrl || ''
    loadedBannerUrl.value = form.bannerUrl
    lastUploadedBannerUrl.value = ''
    form.seoTitle = s.seoTitle || ''
    form.seoKeywords = s.seoKeywords || ''
    form.seoDescription = s.seoDescription || ''
    form.footerText = s.footerText || ''
  } catch (e: any) {
    ElMessage.error(`Failed to load settings: ${e?.message || e}`)
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
