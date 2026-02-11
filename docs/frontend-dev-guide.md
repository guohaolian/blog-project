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
  - `theme.ts`（暗黑模式，仅 UI 状态，但两端均可复用模式）
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

## 4. 暗黑模式（后台 `blog-admin-web`，含登录页）

> 目标：后台所有页面（包括 `/login`）支持 Light/Dark/Auto 切换，并与 Element Plus 的暗黑变量联动。

### 4.1 实现要点（Element Plus）

- Element Plus 的暗黑模式是通过 **给 `<html>` 加 `dark` class** 来启用的。
- 入口需要引入 Element Plus 暗黑变量文件：
  - `blog-admin-web/src/main.ts`：
    - `import 'element-plus/theme-chalk/dark/css-vars.css'`

### 4.2 Theme Store（Pinia）

位置：`blog-admin-web/src/stores/theme.ts`

约定：
- `mode: 'light' | 'dark' | 'auto'`
- `resolvedMode`：当 `mode==='auto'` 时，根据时间窗口决定最终模式
- 本地存储：
  - `localStorage['blog-admin-web:theme']`
  - `localStorage['blog-admin-web:theme:auto-window']`
- 关键动作：
  - `applyToDom()`：
    - `document.documentElement.classList.toggle('dark', resolvedMode === 'dark')`
    - `document.documentElement.style.colorScheme = resolvedMode`
  - `init()`：应用启动时尽早调用，保证首屏（含登录页）不闪烁

### 4.3 初始化时机（避免闪屏）

位置：`blog-admin-web/src/main.ts`
- 在 `app.mount('#app')` 之前执行：
  - `useThemeStore(pinia).init()`

原因：
- 如果在页面渲染后再切换 `<html class="dark">`，会产生一瞬间的“亮→暗”闪烁。

### 4.4 UI 切换入口（后台 Layout）

位置：`blog-admin-web/src/layouts/AdminLayout.vue`
- 顶部 header 使用 `el-segmented` 绑定 `theme.mode`，并在 `@change` 时调用 `theme.setMode(...)`
- 提供 3 个选项：Light / Auto / Dark

> 提示：登录页不经过 `AdminLayout`，但因 theme 已在 `main.ts` 初始化，登录页同样会跟随当前主题。

### 4.5 样式约定（登录页/自定义背景）

位置：`blog-admin-web/src/style.css`
- `html.dark { ... }`：
  - 除了 Element Plus 的暗黑变量生效外，本项目还在此定义后台专用的 CSS 变量（例如背景/文字/边框/阴影等）。
- `body { background: var(--admin-bg-0); color: var(--admin-text); }`

页面开发建议：
- 优先使用 Element Plus 语义变量（如 `--el-bg-color`、`--el-text-color-regular`、`--el-border-color`）。
- 需要自定义时，使用后台自有变量（`--admin-*`）并在 `html.dark` 覆盖。
- 不要在组件里硬编码“黑色/白色”，避免主题切换后不一致。

### 4.6 自测清单（暗黑模式）

- 刷新页面：不会出现明显“先亮后暗/先暗后亮”的闪烁。
- 登录页：跟随当前主题（切到 Dark 后刷新 `/login` 仍保持 Dark）。
- Auto 模式：在默认窗口 `19:00~07:00` 内外切换时能自动更新。
- 切换主题后：本地存储生效（关闭浏览器再打开仍保持）。

---

## 5. 联调方式（开发/生产）

### 5.1 开发环境（推荐）
- 后端：`http://localhost:8080`
- 前台：`http://localhost:5173`
- 后台：`http://localhost:5174`

前端请求写 `/api/...`，由 Vite 代理到后端。

> 注意：如果你让后端临时跑在 `18080`，需要同步修改两个前端的 `vite.config.ts` 代理端口；建议开发期尽量统一用 `8080`。

### 5.2 生产环境
- 前台与后台分别是两个静态站点
- `/api` 由 Nginx 反代到后端（前端无须 CORS）

---

## 6. axios 封装规范（`request.ts`）

### 6.1 响应体处理
后端统一响应结构见 `docs/api-conventions.md`。

前端处理建议：
- `code === 0`：返回 `data`
- 非 0：弹出 `message` 并 `throw`

### 6.2 401/403 处理（区分前台/后台）

- `blog-admin-web`：
  - 401：清 token → 跳转 `/login`
  - 403：提示无权限（不清 token）
- `blog-web`：
  - 401：提示/兜底（不跳转登录，不存 token）

### 6.3 Token 注入（仅后台）
后台管理端所有请求（除 login）自动注入：
- `Authorization: Bearer <token>`

---

## 7. Pinia（状态管理）

### 7.1 后台 auth store（必须）
- state：`token`、`me`、`loading`
- actions：`login`、`logout`、`fetchMe`

### 7.2 site store（前台/后台都可用）
- state：`siteSetting`
- actions：`fetchSite`（调用 `/api/site` 或 admin 的 `/api/admin/site`）

---

## 7. 路由与权限（后台重点）

### 7.1 后台路由
- `/login`：登录页
- `/admin`：仪表盘
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
- 已登录访问 `/login`：跳转 `/admin`
- 刷新后：如 token 存在但 me 为空 → 调用 `/api/admin/auth/me`

### 7.3 Dashboard（Admin）图表（ECharts）

> 位置：`blog-admin-web/src/views/AdminHomeView.vue`
>
> 目的：把 dashboard 的关键指标用图表可视化，并支持“点击图表跳转到对应管理页面”。

#### 7.3.1 图表库与通用组件

- 依赖：`blog-admin-web/package.json` 已包含 `echarts`
- 通用组件：`blog-admin-web/src/components/charts/ECharts.vue`
  - Props：
    - `option: EChartsOption`：ECharts 配置
    - `height?: string`：容器高度（默认 `280px`）
    - `loading?: boolean`：loading 状态（会 show/hideLoading）
    - `onClick?: (params) => void`：点击事件回调（用于页面跳转）
  - 特性：
    - 自动 init / dispose
    - `ResizeObserver` + window resize 自适应
    - option 深度 watch，调用 `setOption(..., { notMerge: true })`

> 约定：尽量复用该组件，不要在每个页面重复写 init/dispose/resize 逻辑。

#### 7.3.2 当前 Dashboard 图表（基于 stats 接口）

数据来源：`GET /api/admin/dashboard/stats`（前端封装：`blog-admin-web/src/api/dashboard.ts`）

当前图表：
1) Posts status（环形饼图）
   - 维度：`draft` vs `published`
   - 中心显示：总数（`draft + published`）
2) Content structure（横向条形图）
   - 维度：`categories` vs `tags`
3) KPI overview（柱状图）
   - 维度：`total` / `totalViews` / `commentsPending`
   - 说明：不同量纲，仅作快照展示

#### 7.3.3 图表点击跳转（交互约定）

在 `AdminHomeView.vue` 中通过 `:on-click="..."` 绑定处理函数，实现“点击跳转到对应页面”：

- Posts status（饼图）
  - Draft → `/admin/posts?status=DRAFT`
  - Published → `/admin/posts?status=PUBLISHED`
- Content structure（条形图）
  - Categories → `/admin/categories`
  - Tags → `/admin/tags`
- KPI overview（柱状图）
  - Posts → `/admin/posts`
  - Pending comments → `/admin/comments?status=PENDING`
  - Views：当前无独立页面，暂兜底跳转到 `/admin/posts`

> 约定：跳转尽量复用已有的跳转函数（例如 `goPosts()` / `goComments()`），避免散落硬编码。

#### 7.3.4 Dashboard 布局对齐（样式约定）

为了让同一行卡片视觉对齐，`AdminHomeView.vue` 使用：
- `el-row align="stretch"`
- 让 `el-col` 作为 flex 容器，内部 `el-card` `width: 100%` 以拉伸对齐

> 约定：优先用局部 `scoped` 样式，不要影响全局 Element Plus 样式。

#### 7.3.5 后续扩展：趋势类图表

现有 `/dashboard/stats` 仅提供汇总数。要做折线/面积等趋势图，建议新增接口：
- `GET /api/admin/dashboard/trends?days=7|30`
- 返回时间序列（例如按天统计发布数/浏览量等）

前端建议：新增 `src/api/dashboard.ts` 方法 + 在 Dashboard 增加折线/面积图卡片。

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

## 部署注意：富文本图片 / Banner 预览（/uploads/**）

上传接口返回的图片 URL 为相对路径，例如：`/uploads/202602/xxx.jpg`。

生产环境推荐由 Nginx 直接暴露静态目录（而不是走后端转发），并确保：

- Nginx 配置包含：
  - `location ^~ /uploads/ { alias /opt/blog/uploads/; try_files $uri =404; }`
  - `client_max_body_size 20m;`（否则上传可能 413）
- 浏览器能直接访问：`http://<公网IP>/uploads/...`

如果出现“上传成功但预览 404”，优先检查：
1) Nginx 是否真的加载了该配置（`nginx -T`）
2) `/opt/blog/uploads` 下是否存在对应文件

---

## 11. 前端自测重点

对照 `docs/acceptance-checklist.md`：
- 后台登录 → 刷新不掉线 → 401 自动回登录
- 后台发布文章后前台可见
- 评论提交提示“待审核”，审核通过后前台可见
- 站点设置修改后前台展示更新

---

## 12. 首页全屏 Banner（Home Hero）

> 目标：进入首页首先展示全屏 Banner（可配置），提示用户向下滚动进入文章列表。

### 12.1 数据来源

- 字段：`SiteSettingVO.bannerUrl`
- 获取：前台 `GET /api/site`
- 管理端配置：后台 `Settings` 页面上传图片（调用 `POST /api/admin/upload/image`）后将返回的 `/uploads/**` URL 保存到 `bannerUrl`

> 说明：上传接口会同步写入 `file_resource`，所以 Banner 图会在资源管理中可见。

### 12.2 展示逻辑（blog-web）

- 入口：`blog-web/src/views/HomeView.vue`
- 满足以下条件才显示 Banner：`siteStore.bannerUrl` 非空
- 交互：
  - 首屏底部有弹跳“向下”按钮，点击会 smooth scroll 到文章列表
  - 处于页面顶部时，首次向下滚轮也会滚到列表区域（更像 Landing Page 的体验）
- 与导航栏配合：
  - Banner 会延伸到 sticky topbar 下方
  - topbar/notice 的半透明背景会“透出” Banner（避免出现纯色顶栏）

### 12.3 避免颜色断层（叠层注意）

Banner 的背景图与遮罩采用 `::before / ::after` 两层实现：
- `::before` 承载背景图
- `::after` 承载**统一强度**的暗化遮罩
这样可避免 topbar/notice 半透明背景和 Banner 自身渐变遮罩叠加后产生“横向色带/断层”。

---

## 13. 管理端 Settings：Banner 清空与资源删除策略

- 清空（Clear）= 站点不再使用该图（只清空 `bannerUrl`）
- 资源删除（删除 `file_resource` + 尝试删除磁盘文件）需要显式操作

当前实现（安全策略）：
- 如果 Banner 是**本次 Settings 页面刚上传**且尚未保存为站点 Banner，点击 Clear 会弹窗询问是否同时删除该资源。
- 对于已经保存过的 Banner，Clear 默认不提供自动删除（避免误删文章引用的图片）。

---

## 8. 文章编辑：导入 Markdown 文件 + 自动上传图片并替换链接（Admin）

适用端：`blog-admin-web`

入口页面：
- 新建文章：`/admin/posts/new`
- 编辑文章：`/admin/posts/:id/edit`

对应实现文件（用于排查/二次开发）：
- 页面：`blog-admin-web/src/views/PostEditView.vue`
- 工具函数：`blog-admin-web/src/utils/markdownImport.ts`

### 8.1 你能得到什么

- 选择一个 `.md/.markdown` 文件后，**自动把文件内容填充到文章正文**。
- 你可以再选择一个“图片文件夹”，系统会：
  1) 扫描正文里的本地图片引用
  2) 自动逐个上传图片（复用管理端图片上传接口）
  3) 把正文里原图片路径替换成上传后的可访问 URL（通常是 `/uploads/YYYYMM/...`）

### 8.2 使用流程（推荐顺序）

1) 在文章编辑页点击 **Import Markdown**，选择你的 `.md` 文件
2) 内容会先填充到正文框
3) 按提示点击 **Pick image folder**，选择包含图片的文件夹（可选目录或其父目录）
4) 等待上传完成，页面会显示上传统计（成功/失败数量），并自动把正文里的图片链接替换掉

> 提示：页面会尽量自动打开文件夹选择器，但有些浏览器会拦截“紧跟文件选择后的二次弹窗”。
> 如果没自动弹出，请手动点一次 **Pick image folder**。

### 8.3 为什么必须“再选一次图片文件夹”（不是 bug）

浏览器出于安全限制：
- 选择 `.md` 文件 **不等于** 允许网页读取你磁盘上同目录/子目录的图片文件；
- 网页只能访问你在文件选择器里**显式授权**选中的文件。因此需要你再选择一次图片所在的文件夹。

### 8.4 支持/建议的图片写法（markdown 内）

工具会忽略明显的远程 URL（例如 `http://`、`https://`、`data:`），只处理“看起来是本地文件路径”的引用。

常见可处理的形式：
- Markdown 图片：`![](images/a.png)`、`![](./images/a.png)`
- HTML 图片：`<img src="images/a.png" />`
- Windows 绝对路径（兼容性支持）：`![](C:\Users\you\Desktop\x\images\a.png)`
  - 注意：浏览器沙箱无法直接访问该绝对路径，工具会按规则提取末尾片段（优先 `images/` 或 `assets/`）来匹配你选中的文件夹内容。

为了最高成功率，建议：
- md 内使用相对路径，并把图片放在 `images/` 或 `assets/` 目录下。

### 8.5 替换规则（你应该预期的行为）

- 上传成功后，会把正文中对应图片的 `src`/`(...)` 里的原路径，替换为上传接口返回的 URL。
- 同一张图片被多次引用时，会去重上传（按规范化后的路径去重），但会在正文中替换所有匹配到的引用。

### 8.6 失败与取消

- 如果选择文件夹后提示 “No files selected …”，通常是浏览器弹出了确认框，你点了取消或被浏览器拦截；重新点 **Pick image folder** 再选一次即可。
- 如果 md 引用了某张图，但你选的文件夹里找不到对应文件：
  - 会提示找不到文件，并在控制台打印失败项。
- 点击 **Cancel** 会中断本次批量上传（已替换的内容会保留当前状态）。

### 8.7 自测点（建议至少测一遍）

- [ ] Import Markdown 后正文内容立刻填充
- [ ] 选择图片文件夹后：资源列表能看到新增资源；文章内容中的图片链接被替换为 `/uploads/**`（或完整 URL）
- [ ] 远程图片链接不被替换
- [ ] md 引用不存在图片时有明确提示，且不会导致页面卡死
- [ ] Cancel 能中断上传
