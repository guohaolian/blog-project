<template>
  <div style="padding: 16px">
    <div style="display:flex;align-items:center;justify-content:space-between;gap:12px;margin-bottom:12px">
      <h2 style="margin:0">Admins</h2>
      <div style="display:flex;gap:8px">
        <el-button size="small" :loading="loading" @click="fetchList">Refresh</el-button>
        <el-button size="small" type="primary" @click="openCreate">New Admin</el-button>
      </div>
    </div>

    <el-table :data="list" v-loading="loading" style="width:100%" size="small">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="Username" width="160" />
      <el-table-column prop="displayName" label="Display Name" min-width="160" />
      <el-table-column prop="status" label="Status" width="110">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? 'ENABLED' : 'DISABLED' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="Created" width="170" />
      <el-table-column prop="updatedAt" label="Updated" width="170" />
      <el-table-column label="Actions" width="260">
        <template #default="{ row }">
          <el-button size="small" @click="openReset(row)">Reset Password</el-button>
          <el-button
            size="small"
            :type="row.status === 1 ? 'warning' : 'success'"
            :disabled="saving || (auth.user?.id === row.id && row.status === 1)"
            @click="toggle(row)"
          >
            {{ row.status === 1 ? 'Disable' : 'Enable' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Create dialog -->
    <el-dialog v-model="createVisible" title="New Admin" width="480px">
      <el-form :model="createForm" label-width="120px">
        <el-form-item label="Username">
          <el-input v-model="createForm.username" maxlength="50" />
        </el-form-item>
        <el-form-item label="Display name">
          <el-input v-model="createForm.displayName" maxlength="50" />
        </el-form-item>
        <el-form-item label="Password">
          <el-input v-model="createForm.password" type="password" show-password maxlength="100" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible=false">Cancel</el-button>
        <el-button type="primary" :loading="saving" @click="create">Create</el-button>
      </template>
    </el-dialog>

    <!-- Reset dialog -->
    <el-dialog v-model="resetVisible" title="Reset Password" width="480px">
      <el-form :model="resetForm" label-width="120px">
        <el-form-item label="User">
          <div style="font-weight:600">#{{ resetForm.id }} {{ resetForm.username }}</div>
        </el-form-item>
        <el-form-item label="New password">
          <el-input v-model="resetForm.newPassword" type="password" show-password maxlength="100" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resetVisible=false">Cancel</el-button>
        <el-button type="primary" :loading="saving" @click="reset">Reset</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as AdminApi from '../api/admins'
import { useAuthStore } from '../stores/auth'
import { useRouter } from 'vue-router'

const loading = ref(false)
const saving = ref(false)
const list = ref<AdminApi.AdminUserVO[]>([])

const auth = useAuthStore()
const router = useRouter()

const createVisible = ref(false)
const resetVisible = ref(false)

const createForm = reactive({
  username: '',
  displayName: '',
  password: '',
})

const resetForm = reactive({
  id: 0,
  username: '',
  newPassword: '',
})

async function fetchList() {
  loading.value = true
  try {
    list.value = await AdminApi.adminUserList()
  } finally {
    loading.value = false
  }
}

function openCreate() {
  createForm.username = ''
  createForm.displayName = ''
  createForm.password = ''
  createVisible.value = true
}

async function create() {
  if (!createForm.username.trim() || !createForm.password.trim()) {
    ElMessage.warning('Username and password are required')
    return
  }
  if (createForm.username.trim().length < 3) {
    ElMessage.warning('Username must be at least 3 chars')
    return
  }
  if (createForm.password.trim().length < 6) {
    ElMessage.warning('Password must be at least 6 chars')
    return
  }
  saving.value = true
  try {
    await AdminApi.adminUserCreate({
      username: createForm.username,
      password: createForm.password,
      displayName: createForm.displayName || undefined,
    })
    ElMessage.success('Created')
    createVisible.value = false
    fetchList()
  } finally {
    saving.value = false
  }
}

function openReset(row: AdminApi.AdminUserVO) {
  resetForm.id = row.id
  resetForm.username = row.username
  resetForm.newPassword = ''
  resetVisible.value = true
}

async function reset() {
  if (!resetForm.newPassword.trim()) {
    ElMessage.warning('New password is required')
    return
  }
  if (resetForm.newPassword.trim().length < 6) {
    ElMessage.warning('Password must be at least 6 chars')
    return
  }
  saving.value = true
  try {
    await AdminApi.adminUserResetPassword(resetForm.id, { newPassword: resetForm.newPassword })
    ElMessage.success('Reset')
    resetVisible.value = false

    // If current user resets its own password, the current token should be considered invalid.
    if (auth.user?.id && auth.user.id === resetForm.id) {
      ElMessage.warning('Password changed. Please login again.')
      auth.logout()
      router.replace('/login')
      return
    }

    fetchList()
  } finally {
    saving.value = false
  }
}

async function toggle(row: AdminApi.AdminUserVO) {
  if (auth.user?.id && row.id === auth.user.id && row.status === 1) {
    ElMessage.warning("You can't disable yourself")
    return
  }
  const next = row.status === 1 ? 0 : 1
  await ElMessageBox.confirm(
    `${next === 0 ? 'Disable' : 'Enable'} admin '${row.username}'?`,
    'Confirm',
    { type: 'warning' },
  )
  saving.value = true
  try {
    await AdminApi.adminUserUpdateStatus(row.id, { status: next })
    ElMessage.success('Updated')
    fetchList()
  } finally {
    saving.value = false
  }
}

onMounted(fetchList)
</script>
