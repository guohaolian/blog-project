# 前端开发文档（`blog-web` / `blog-admin-web`）

- 技术栈：Vue 3 + Vite + Element Plus + Pinia + Axios
- 包管理：npm（每个项目独立 `package-lock.json`）
- 目标：让你在开始写页面前，先把**工程结构、请求封装、状态管理、路由与权限、模块划分、联调方式**统一起来。

关联文档：
- PRD：`docs/prd.md`
- 接口规范：`docs/api-conventions.md`
- 命名与目录：`docs/naming-and-structure.md`
- 验收：`docs/acceptance-checklist.md`

---

## 1. 必须先定的 5 条前端约定

1) 所有 API 请求路径统一写成相对路径：`/api/...`（不要写死域名）
2) 开发环境通过 Vite 代理 `/api` 到后端；生产通过 Nginx 反代 `/api`
3) 后台管理端必须有路由守卫（未登录不允许进管理路由）
4) axios 统一封装：统一处理 `{code,message,data}` 与 401
5) 页面与接口按“业务域”拆模块：posts/categories/tags/comments/site/upload/resources/auth

---

## 2. 目录结构（两端尽量一致）

建议结构（两端共享类似形态）：

- `src/api/`
  - `request.ts`（axios 实例、拦截器）
  - `modules/`（按业务域拆）
- `src/stores/`
  - `auth.ts`（后台必需）
  - `site.ts`
- `src/router/`
  - `index.ts`
  - `guards.ts`（后台必需）
- `src/views/`（页面）
- `src/components/`（可复用组件）
- `src/utils/`（token、format 等）

---

## 3. 联调方式

### 3.1 开发环境（推荐）
- 后端：`http://localhost:8080`
- 前台：`http://localhost:5173`
- 后台：`http://localhost:5174`

前端请求写 `/api/...`，由 Vite 代理到后端。

### 3.2 生产环境
- 前台与后台分别是两个域名的静态站点
- `/api` 由 Nginx 反代到后端（前端无需 CORS）

---

## 4. axios 封装规范（request.ts）

### 4.1 响应体处理
后端统一响应结构见 `docs/api-conventions.md`。

前端处理建议：
- `code === 0`：返回 `data`
- 非 0：弹出 `message` 并 `throw`

### 4.2 401/403 处理
- 401：清 token → 跳转登录页（后台）
- 403：提示无权限（不要清 token）

### 4.3 Token 注入
后台管理端所有请求（除 login）都自动注入：
- `Authorization: Bearer <token>`

---

## 5. Pinia（状态管理）

### 5.1 后台 auth store（必做）
- state：`token`、`me`、`loading`
- actions：`login`、`logout`、`fetchMe`

### 5.2 site store（前台/后台都可用）
- state：`siteSetting`
- actions：`fetchSite`（调用 `/api/site` 或 admin 的 `/api/admin/site`）

---

## 6. 路由与权限（后台重点）

### 6.1 后台路由
- `/login`：登录页
- `/dashboard`：仪表盘
- `/posts`：文章列表
- `/posts/new`：新建
- `/posts/:id/edit`：编辑
- `/categories`：分类
- `/tags`：标签
- `/comments`：评论审核
- `/resources`：资源管理
- `/settings`：站点设置
- `/admins`：管理员管理（若启用）

### 6.2 守卫策略
- 未登录：除 `/login` 外全部跳转 `/login`
- 已登录访问 `/login`：跳转 `/dashboard`
- 刷新后：如 token 存在但 me 为空 → 调用 `/api/admin/auth/me`

---

## 7. 页面与接口模块映射（按 PRD）

### 7.1 posts
- 后台：列表（分页/筛选/搜索）、编辑（草稿/发布/撤回/删除）
- 前台：列表（分页）、详情（Markdown 渲染）

### 7.2 categories / tags
- 后台 CRUD
- 前台列表与筛选

### 7.3 comments
- 前台：发表评论（默认待审核）+ 展示已审核评论
- 后台：审核通过/拒绝/删除

### 7.4 site
- 前台：站点名/公告/页脚/关于/友链展示
- 后台：表单配置站点设置

### 7.5 upload/resources
- upload：图片上传，返回 url
- resources：资源列表与删除（若后端落库）

---

## 8. Element Plus 交互约定

- 列表统一 `el-table` + `el-pagination`
- 表单统一 `el-form` + rules
- 删除/危险操作统一二次确认（MessageBox）
- 全局提示：成功/失败统一 Message

---

## 9. 前端自测重点

对照 `docs/acceptance-checklist.md`：
- 后台登录→刷新不掉线→401 自动回登录
- 发布文章后前台可见
- 评论提交后提示“待审核”，审核通过后可见
- 站点设置修改后前台展示更新
