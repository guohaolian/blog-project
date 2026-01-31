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
          <div style="display: flex; justify-content: flex-end; margin-bottom: 8px">
            <el-upload
              :show-file-list="false"
              accept="image/*"
              :before-upload="onBodyBeforeUpload"
            >
              <el-button size="small" :loading="bodyUploading">Upload image (markdown)</el-button>
            </el-upload>
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

const route = useRoute()
const router = useRouter()

const id = computed(() => Number(route.params.id))
const isNew = computed(() => route.path.endsWith('/new'))

const formRef = ref<FormInstance>()
const loading = ref(false)

const coverUploading = ref(false)
const bodyUploading = ref(false)

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

async function loadMeta() {
  const [cs, ts] = await Promise.all([adminCategories(), adminTags()])
  categories.value = cs
  tags.value = ts
}

async function loadDetail() {
  if (isNew.value) return
  const vo = await adminPostGet(id.value)
  form.title = vo.title
  form.summary = vo.summary || ''
  form.content = vo.content
  form.coverUrl = vo.coverUrl || ''
  form.categoryId = (vo.categoryId as any) || undefined
  form.tagIds = vo.tagIds || []
  form.status = vo.status
}

async function save() {
  const ok = await formRef.value?.validate().catch(() => false)
  if (!ok) return

  loading.value = true
  try {
    if (isNew.value) {
      const newId = await adminPostCreate({
        title: form.title,
        summary: form.summary,
        content: form.content,
        coverUrl: form.coverUrl || undefined,
        categoryId: form.categoryId || null,
        tagIds: form.tagIds,
      })
      ElMessage.success('Saved')
      router.replace(`/admin/posts/${newId}/edit`)
    } else {
      await adminPostUpdate(id.value, {
        title: form.title,
        summary: form.summary,
        content: form.content,
        coverUrl: form.coverUrl || undefined,
        categoryId: form.categoryId || null,
        tagIds: form.tagIds,
      })
      ElMessage.success('Saved')
    }
  } finally {
    loading.value = false
  }
}

async function publish() {
  await adminPostPublish(id.value)
  form.status = 'PUBLISHED'
  ElMessage.success('Published')
}

async function unpublish() {
  await adminPostUnpublish(id.value)
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

onMounted(async () => {
  await loadMeta()
  await loadDetail()
})
</script>
