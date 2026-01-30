# 前端开发文档（`blog-web` / `blog-admin-web`）

- 技术栈：Vue 3 + Vite + Element Plus + Pinia + Axios
- 包管理：npm（每个项目独立 `package-lock.json`）
- 目标：让你在开始写页面前，先把**工程结构、请求封装、状态管理、路由与权限、模块划分、联调方式、部署对接**统一起来。

关联文档：
- PRD：`docs/prd.md`
- 接口规范：`docs/api-conventions.md`
- 命名与目录：`docs/naming-and-structure.md`
- 验收：`docs/acceptance-checklist.md`
- 本地环境：`docs/dev-setup.md`

---

## 1. 必须先定的前端硬约束（Do/Don't）

1) 所有 API 请求路径统一写相对路径：`/api/...`（不要写死域名）
2) 开发环境通过 Vite 代理 `/api` 到后端；生产通过 Nginx 反代 `/api`
3) **用户前台（`blog-web`）不需要登录**：
   - 不做登录页、不做 token store、不做路由守卫强制跳转
   - axios 不注入 Authorization
   - 401 只做提示/兜底处理，不跳转登录
4) 后台管理端（`blog-admin-web`）必须有登录 + 路由守卫（未登录不允许访问管理路由）
5) axios 统一封装：统一处理 `{code,message,data}` 与 401/403
6) 页面与接口按“业务域”拆模块：posts/categories/tags/comments/site/upload/resources/auth

---

## 2. 目录结构（两端尽量一致）

建议结构（两端共享类似形态）：

- `src/api/`
  - `request.ts`（axios 实例、拦截器）
  - `modules/`（按业务域拆）
- `src/stores/`
  - `auth.ts`（仅后台需要）
  - `site.ts`
- `src/router/`
  - `index.ts`
  - `guards.ts`（仅后台需要）
- `src/views/`（页面）
- `src/components/`（可复用组件）
- `src/utils/`（token、format 等）

---

## 3. 用户端不登录：确认与回滚清单（`blog-web`）

> 目的：防止把后台的登录体系“误拷贝”到用户端，导致需求偏离。

### 3.1 代码检查点（必须满足）

- 路由：`blog-web/src/router/index.ts`
  - 不存在 `/login`
  - 不存在 `beforeEach` 强制跳登录/鉴权逻辑
- 状态：`blog-web/src/stores/`
  - 不存在 `auth` store（token/me/logout 等）
- 请求：`blog-web/src/api/request.ts`
  - 不注入 `Authorization`
  - 不做 “401 => router.push('/login')”

### 3.2 回滚建议（如果之前误加了登录）

优先顺序：
1) 删除/回退 `blog-web` 的登录页面与相关路由
2) 删除 `auth store` 与 token 工具
3) 还原 `request.ts`：移除 token 注入与 401 跳转
4) 删除 UI 上所有“登录/退出/个人中心”等入口

---

## 4. 联调方式（开发/生产）

### 4.1 开发环境（推荐）
- 后端：`http://localhost:8080`
- 前台：`http://localhost:5173`
- 后台：`http://localhost:5174`

前端请求写 `/api/...`，由 Vite 代理到后端。

> 注意：如果你让后端临时跑在 `18080`，需要同步修改两个前端的 `vite.config.ts` 代理端口；建议开发期尽量统一用 `8080`。

### 4.2 生产环境
- 前台与后台分别是两个静态站点
- `/api` 由 Nginx 反代到后端（前端无须 CORS）

---

## 5. axios 封装规范（`request.ts`）

### 5.1 响应体处理
后端统一响应结构见 `docs/api-conventions.md`。

前端处理建议：
- `code === 0`：返回 `data`
- 非 0：弹出 `message` 并 `throw`

### 5.2 401/403 处理（区分前台/后台）

- `blog-admin-web`：
  - 401：清 token → 跳转 `/login`
  - 403：提示无权限（不清 token）
- `blog-web`：
  - 401：提示/兜底（不跳转登录，不存 token）

### 5.3 Token 注入（仅后台）
后台管理端所有请求（除 login）自动注入：
- `Authorization: Bearer <token>`

---

## 6. Pinia（状态管理）

### 6.1 后台 auth store（必须）
- state：`token`、`me`、`loading`
- actions：`login`、`logout`、`fetchMe`

### 6.2 site store（前台/后台都可用）
- state：`siteSetting`
- actions：`fetchSite`（调用 `/api/site` 或 admin 的 `/api/admin/site`）

---

## 7. 路由与权限（后台重点）

### 7.1 后台路由
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

### 7.2 守卫策略
- 未登录：除 `/login` 外全部跳转 `/login`
- 已登录访问 `/login`：跳转 `/dashboard`
- 刷新后：如 token 存在但 me 为空 → 调用 `/api/admin/auth/me`

---

## 8. MVP 里程碑（前端拆分建议）

> 这部分是为了让你“先跑通闭环，再做增强”。每个里程碑都应该能联调并自测。

- M1（骨架联调）：
  - admin：登录页 + 路由守卫 + request 拦截器 + 基础布局
  - web：首页列表/详情骨架 + request 封装（无 token）
- M2（文章闭环）：
  - admin：文章列表 + 编辑/发布/撤回/删除
  - web：已发布文章列表 + 详情展示（Markdown 渲染）
- M3（分类/标签/搜索/归档）：
  - web：分类/标签列表、筛选列表、搜索页、归档页
  - admin：分类/标签 CRUD
- M4（评论闭环）：
  - web：评论展示（仅 APPROVED）+ 提交（默认 PENDING）
  - admin：评论审核通过/拒绝/删除
- M5（站点设置）：
  - web：关于/友链/SEO（title/description）
  - admin：settings 表单
- M6（上传/资源）：
  - admin：封面/正文图片上传
  - （可选）资源列表与删除

---

## 9. 本地开发（Windows + npm + IDEA/WebStorm）

### 9.1 npm 安装依赖
在每个前端项目分别执行：
- `npm install`

> 建议：不要混用 pnpm/yarn，保持 lockfile 稳定。

### 9.2 启动
- 前台：在 `blog-web` 目录 `npm run dev`
- 后台：在 `blog-admin-web` 目录 `npm run dev`

如果你更喜欢“一键启动”，可以使用仓库的 PowerShell 脚本（见 `scripts/`）。

---

## 10. 生产部署对接（Nginx + 静态站）

1) `npm run build` 生成产物（Vite 默认在 `dist/`）
2) Nginx 配置：
   - SPA 刷新：`try_files $uri $uri/ /index.html;`
   - `/api/` 反代到后端
   - `/uploads/` 静态映射（用于图片访问）

参考：`deploy/nginx/blog.conf.example`

---

## 11. 前端自测重点

对照 `docs/acceptance-checklist.md`：
- 后台登录 → 刷新不掉线 → 401 自动回登录
- 后台发布文章后前台可见
- 评论提交提示“待审核”，审核通过后前台可见
- 站点设置修改后前台展示更新
