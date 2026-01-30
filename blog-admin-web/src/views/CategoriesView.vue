<template>
  <div style="padding: 16px">
    <div style="display: flex; align-items: center; justify-content: space-between; gap: 12px; margin-bottom: 12px">
      <h2 style="margin: 0">Categories</h2>
      <el-button type="primary" @click="openCreate">New</el-button>
    </div>

    <el-table :data="list" v-loading="loading" style="width: 100%" size="small">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="Name" />
      <el-table-column label="Actions" width="220">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">Edit</el-button>
          <el-popconfirm title="Delete this category?" @confirm="remove(row.id)">
            <template #reference>
              <el-button size="small" type="danger">Delete</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editing ? 'Edit Category' : 'New Category'" width="420px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="Name">
          <el-input v-model="form.name" maxlength="50" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" :loading="saving" @click="save">Save</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  adminCategoryCreate,
  adminCategoryDelete,
  adminCategoryList,
  adminCategoryUpdate,
  type CategoryVO,
} from '../api/categories'

const loading = ref(false)
const saving = ref(false)
const list = ref<CategoryVO[]>([])

const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const editing = ref(false)

const form = reactive({
  name: '',
})

async function fetchList() {
  loading.value = true
  try {
    list.value = await adminCategoryList()
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editing.value = false
  editingId.value = null
  form.name = ''
  dialogVisible.value = true
}

function openEdit(row: CategoryVO) {
  editing.value = true
  editingId.value = row.id
  form.name = row.name
  dialogVisible.value = true
}

async function save() {
  if (!form.name.trim()) {
    ElMessage.warning('Name is required')
    return
  }

  saving.value = true
  try {
    if (editing.value && editingId.value != null) {
      await adminCategoryUpdate(editingId.value, { name: form.name })
      ElMessage.success('Updated')
    } else {
      await adminCategoryCreate({ name: form.name })
      ElMessage.success('Created')
    }
    dialogVisible.value = false
    await fetchList()
  } finally {
    saving.value = false
  }
}

async function remove(id: number) {
  await adminCategoryDelete(id)
  ElMessage.success('Deleted')
  await fetchList()
}

onMounted(fetchList)
</script>
