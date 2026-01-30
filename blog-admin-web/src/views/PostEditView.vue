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
        <el-input v-model="form.coverUrl" placeholder="/uploads/... or https://..." />
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
        <el-input v-model="form.content" type="textarea" :rows="16" placeholder="# Markdown..." />
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
import {
  adminCategories,
  adminPostCreate,
  adminPostGet,
  adminPostPublish,
  adminPostUnpublish,
  adminPostUpdate,
  adminTags,
} from '../api/posts'
import type { CategoryVO, TagVO } from '../api/posts'

const route = useRoute()
const router = useRouter()

const id = computed(() => Number(route.params.id))
const isNew = computed(() => route.path.endsWith('/new'))

const formRef = ref<FormInstance>()
const loading = ref(false)

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

onMounted(async () => {
  await loadMeta()
  await loadDetail()
})
</script>
