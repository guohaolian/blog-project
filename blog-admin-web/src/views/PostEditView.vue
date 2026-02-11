<template>
  <div style="padding: 16px; max-width: 900px">
    <h2 style="margin-bottom: 12px">{{ isNew ? 'New Post' : 'Edit Post' }}</h2>

    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="Title" prop="title">
        <el-input v-model="form.title" />
      </el-form-item>

      <el-form-item label="Summary" prop="summary">
        <el-input v-model="form.summary" type="textarea" :rows="2" />
      </el-form-item>

      <el-form-item label="Cover URL">
        <div style="display: flex; gap: 8px; width: 100%">
          <el-input v-model="form.coverUrl" placeholder="/uploads/... or https://..." />

          <el-upload
            :show-file-list="false"
            accept="image/*"
            :before-upload="onCoverBeforeUpload"
          >
            <el-button :loading="coverUploading">Upload</el-button>
          </el-upload>
        </div>
      </el-form-item>

      <el-form-item label="Category">
        <el-select v-model="form.categoryId" clearable style="width: 240px">
          <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </el-form-item>

      <el-form-item label="Tags">
        <el-select v-model="form.tagIds" multiple clearable style="width: 480px">
          <el-option v-for="t in tags" :key="t.id" :label="t.name" :value="t.id" />
        </el-select>
      </el-form-item>

      <el-form-item label="Content" prop="content">
        <div style="width: 100%">
          <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; gap: 8px">
            <div style="display: flex; gap: 8px; align-items: center; flex-wrap: wrap">
              <el-upload :show-file-list="false" accept="image/*" :before-upload="onBodyBeforeUpload">
                <el-button size="small" :loading="bodyUploading">Upload image (markdown)</el-button>
              </el-upload>

              <el-button size="small" :loading="mdImporting" @click="triggerMdPick">Import Markdown</el-button>
              <el-button size="small" :disabled="!mdImporting" @click="cancelMdImport">Cancel</el-button>

              <input
                ref="mdFileInputRef"
                type="file"
                accept=".md,.markdown,text/markdown"
                style="display: none"
                @change="onMdFilePicked"
              />

              <input
                ref="assetDirInputRef"
                type="file"
                style="display: none"
                webkitdirectory
                multiple
                @change="onAssetDirPicked"
              />

              <div style="font-size: 12px; color: #666">
                <div v-if="mdImportStats.total > 0">
                  Images: {{ mdImportStats.ok }}/{{ mdImportStats.total }} uploaded,
                  {{ mdImportStats.failed }} failed
                </div>
                <div v-else>Tip: pick a .md file, then pick its image folder for auto-upload.</div>
              </div>
            </div>

            <div style="display: flex; gap: 8px; align-items: center">
              <el-button size="small" :disabled="!lastImportedMd" @click="openAssetFolderPicker">Pick image folder</el-button>
            </div>
          </div>

          <el-input v-model="form.content" type="textarea" :rows="16" placeholder="# Markdown..." />
        </div>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" :loading="loading" @click="save">Save</el-button>
        <el-button @click="$router.push('/admin/posts')">Back</el-button>
        <el-button v-if="!isNew && form.status !== 'PUBLISHED'" type="success" @click="publish">Publish</el-button>
        <el-button v-if="!isNew && form.status === 'PUBLISHED'" type="warning" @click="unpublish">Unpublish</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import {
  adminCategories,
  adminPostCreate,
  adminPostGet,
  adminPostPublish,
  adminPostUnpublish,
  adminPostUpdate,
  adminTags,
} from '../api/posts'
import { adminUploadImage } from '../api/upload'
import type { CategoryVO, TagVO } from '../api/posts'
import { useAsyncTask, runWithErrorToast } from '../utils/requestHelpers'
import { importMarkdownWithUploads } from '../utils/markdownImport'

const route = useRoute()
const router = useRouter()

const id = computed(() => Number(route.params.id))
const isNew = computed(() => route.path.endsWith('/new'))

const formRef = ref<FormInstance>()
const loading = ref(false)

const coverUploading = ref(false)
const bodyUploading = ref(false)

// markdown importing state
const mdFileInputRef = ref<HTMLInputElement>()
const assetDirInputRef = ref<HTMLInputElement>()
const mdImporting = ref(false)
const mdAbortController = ref<AbortController | null>(null)
const lastImportedMd = ref<File | null>(null)
const lastPickedAssets = ref<FileList | null>(null)

const mdImportStats = reactive({
  total: 0,
  ok: 0,
  failed: 0,
})

const categories = ref<CategoryVO[]>([])
const tags = ref<TagVO[]>([])

const form = reactive({
  title: '',
  summary: '',
  content: '',
  coverUrl: '',
  categoryId: undefined as number | undefined,
  tagIds: [] as number[],
  status: 'DRAFT',
})

const rules: FormRules = {
  title: [{ required: true, message: 'Title is required', trigger: 'blur' }],
  content: [{ required: true, message: 'Content is required', trigger: 'blur' }],
}

const { run: loadMeta } = useAsyncTask(
  async () => {
    const [cs, ts] = await Promise.all([adminCategories(), adminTags()])
    categories.value = cs
    tags.value = ts
  },
  { defaultErrorMessage: 'Failed to load categories/tags' },
)

const { run: loadDetail } = useAsyncTask(
  async () => {
    if (isNew.value) return true
    if (!Number.isFinite(id.value) || id.value <= 0) {
      throw new Error('Invalid post id')
    }
    const vo = await adminPostGet(id.value)
    form.title = vo.title
    form.summary = vo.summary || ''
    form.content = vo.content
    form.coverUrl = vo.coverUrl || ''
    form.categoryId = (vo.categoryId as any) || undefined
    form.tagIds = vo.tagIds || []
    form.status = vo.status
    return true
  },
  { defaultErrorMessage: 'Failed to load post' },
)

async function save() {
  const okValid = await formRef.value?.validate().catch(() => false)
  if (!okValid) return

  loading.value = true
  try {
    if (isNew.value) {
      const newId = await runWithErrorToast(
        () =>
          adminPostCreate({
            title: form.title,
            summary: form.summary,
            content: form.content,
            coverUrl: form.coverUrl || undefined,
            categoryId: form.categoryId || null,
            tagIds: form.tagIds,
          }),
        { defaultErrorMessage: 'Failed to create post' },
      )
      if (!newId) return
      ElMessage.success('Saved')
      router.replace(`/admin/posts/${newId}/edit`)
    } else {
      const ok = await runWithErrorToast(
        () =>
          adminPostUpdate(id.value, {
            title: form.title,
            summary: form.summary,
            content: form.content,
            coverUrl: form.coverUrl || undefined,
            categoryId: form.categoryId || null,
            tagIds: form.tagIds,
          }),
        { defaultErrorMessage: 'Failed to update post' },
      )
      if (!ok) return
      ElMessage.success('Saved')
    }
  } finally {
    loading.value = false
  }
}

async function publish() {
  const ok = await runWithErrorToast(
    () => adminPostPublish(id.value),
    { defaultErrorMessage: 'Failed to publish' },
  )
  if (!ok) return
  form.status = 'PUBLISHED'
  ElMessage.success('Published')
}

async function unpublish() {
  const ok = await runWithErrorToast(
    () => adminPostUnpublish(id.value),
    { defaultErrorMessage: 'Failed to unpublish' },
  )
  if (!ok) return
  form.status = 'DRAFT'
  ElMessage.success('Unpublished')
}

async function uploadImage(file: File) {
  const res = await adminUploadImage(file)
  return res.url
}

function humanFileSize(bytes: number) {
  if (!Number.isFinite(bytes) || bytes <= 0) return '0B'
  if (bytes < 1024) return `${bytes}B`
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)}KB`
  return `${(bytes / 1024 / 1024).toFixed(2)}MB`
}

function extractErrMsg(e: unknown) {
  if (axios.isAxiosError(e)) {
    const data: any = e.response?.data
    if (data?.message) return String(data.message)
    if (e.response?.status) return `HTTP ${e.response.status}`
    return e.message || 'Upload failed'
  }
  return (e as any)?.message || String(e)
}

async function onCoverBeforeUpload(file: File) {
  coverUploading.value = true
  try {
    // precheck (avoid uploading huge files)
    if (file.size > 10 * 1024 * 1024) {
      ElMessage.error(`File too large: ${humanFileSize(file.size)} (max 10MB)`)
      return false
    }
    form.coverUrl = await uploadImage(file)
    ElMessage.success('Cover uploaded')
  } catch (e) {
    console.error('cover upload failed', e)
    ElMessage.error(extractErrMsg(e))
  } finally {
    coverUploading.value = false
  }
  return false
}

async function onBodyBeforeUpload(file: File) {
  bodyUploading.value = true
  try {
    if (file.size > 10 * 1024 * 1024) {
      ElMessage.error(`File too large: ${humanFileSize(file.size)} (max 10MB)`)
      return false
    }
    // keep it simple: append markdown image at end
    const md = `\n\n![](${await uploadImage(file)})\n`
    form.content = (form.content || '') + md
    ElMessage.success('Image uploaded')
  } catch (e) {
    console.error('body image upload failed', e)
    ElMessage.error(extractErrMsg(e))
  } finally {
    bodyUploading.value = false
  }
  return false
}

function triggerMdPick() {
  mdFileInputRef.value?.click()
}

function cancelMdImport() {
  mdAbortController.value?.abort()
}

async function onMdFilePicked(e: Event) {
  const input = e.target as HTMLInputElement
  const f = input.files?.[0]
  input.value = ''
  if (!f) return

  // Reset stats and remember markdown.
  lastImportedMd.value = f
  lastPickedAssets.value = null
  mdImportStats.total = 0
  mdImportStats.ok = 0
  mdImportStats.failed = 0

  // Read markdown text.
  const text = await f.text()

  // Fill content first.
  form.content = text

  // Then prompt user to pick the asset folder (browser security prevents auto-reading sibling files).
  ElMessage.success('Markdown imported. Now pick the image folder to auto-upload & replace.')
  // auto-open folder picker for convenience
  // NOTE: some browsers block programmatic folder picker right after a file pick.
  // If it doesn't open, user can click "Pick image folder" manually.
  try {
    assetDirInputRef.value?.click()
  } catch {
    // ignore
  }
}

async function onAssetDirPicked(e: Event) {
  try {
    const input = e.target as HTMLInputElement
    const files = input.files

    console.log('[md-import] onAssetDirPicked fired', { filesLen: files?.length })

    if (!files || files.length === 0) {
      ElMessage.warning('No files selected. If you saw a browser confirm dialog, please click “Upload/Allow”.')
      console.log('[md-import] no files selected (maybe canceled or blocked by browser)')
      // DO NOT clear input.value here; keep it so user can re-open without losing state.
      return
    }

    // copy out files before we clear input.value (some browsers invalidate FileList after clearing)
    const filesArr = Array.from(files)
    input.value = ''

    lastPickedAssets.value = files

    const imgCount = filesArr.filter((f) => f.type?.startsWith('image/')).length
    ElMessage.info(`Selected ${filesArr.length} file(s) from folder (${imgCount} image(s)).`)

    if (!lastImportedMd.value) {
      ElMessage.warning('Please import a Markdown file first.')
      return
    }

    mdAbortController.value?.abort()
    const ac = new AbortController()
    mdAbortController.value = ac

    mdImporting.value = true
    try {
      const before = form.content || ''
      const res = await importMarkdownWithUploads(before, {
        assets: filesArr,
        uploadImage,
        concurrency: 3,
        signal: ac.signal,
        onProgress: (p) => {
          mdImportStats.total = p.total
          mdImportStats.ok = p.ok
          mdImportStats.failed = p.failed
        },
      })

      console.log('[md-import] images', res.images)

      if (res.images.length === 0) {
        ElMessage.info('No local images found in markdown (or all images are already URLs).')
      }

      const okOnes = res.images.filter((x) => x.url)
      const failedOnes = res.images.filter((x) => !x.url)

      if (okOnes.length > 0) {
        console.log('[md-import] uploaded urls', okOnes.map((x) => ({ raw: x.raw, normalized: x.normalized, url: x.url })))
      }

      if (failedOnes.length > 0) {
        console.warn('[md-import] failed', failedOnes)
        const firstErr = failedOnes.find((x) => x.error)?.error
        if (firstErr) ElMessage.warning(firstErr)
      }

      form.content = res.markdown

      // sanity check: if uploaded but markdown unchanged, warn.
      if (okOnes.length > 0 && res.markdown === before) {
        ElMessage.warning('Images uploaded, but markdown was not updated. Please check image path format in markdown.')
      }

      if (failedOnes.length > 0) {
        ElMessage.warning(`Imported with ${failedOnes.length} image(s) failed. See console for details.`)
      } else if (res.images.length > 0) {
        ElMessage.success('Markdown images uploaded and replaced.')
      }
    } catch (err: any) {
      if (err?.name === 'AbortError') {
        ElMessage.info('Markdown import cancelled')
      } else {
        console.error('Markdown import failed', err)
        ElMessage.error(extractErrMsg(err))
      }
    } finally {
      mdImporting.value = false
    }
  } catch (err) {
    console.error('[md-import] onAssetDirPicked crashed', err)
    ;(window as any).alert?.(`onAssetDirPicked error: ${(err as any)?.message || err}`)
  }
}

// In template, use a small helper to open folder picker.
function openAssetFolderPicker() {
  assetDirInputRef.value?.click()
}

onMounted(async () => {
  await loadMeta()
  const ok = await loadDetail()
  if (!ok && !isNew.value) {
    // loadDetail already toasts; just exit to list.
    await router.replace('/admin/posts')
  }
})
</script>
