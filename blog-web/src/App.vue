<template>
  <div>
    <div style="border-bottom: 1px solid var(--el-border-color); padding: 10px 16px">
      <div style="max-width: 900px; margin: 0 auto; display: flex; align-items: center; justify-content: space-between; gap: 12px">
        <div style="font-weight: 700">My Blog</div>
        <div style="display: flex; gap: 8px">
          <el-button text @click="$router.push('/')">Home</el-button>
          <el-button text @click="$router.push('/categories')">Categories</el-button>
          <el-button text @click="$router.push('/tags')">Tags</el-button>
        </div>
      </div>
    </div>

    <router-view />
  </div>
</template>
