# PRD：前后端分离博客系统（包含可选增强需求）

- 版本：v1.0
- 日期：2026-01-30
- 项目：
  - `blog-web`（用户前台）
  - `blog-admin-web`（后台管理）
  - `blog-api`（后端 API）

> 本 PRD 用于冻结业务范围与验收口径。工程约定与接口通用规范见：
> - `docs/api-conventions.md`
> - `docs/naming-and-structure.md`
> - `docs/stack-and-versions.md`

---

## 1. 背景与目标

### 1.1 背景
从零开发一套完整的前后端分离博客系统，部署到阿里云服务器，整体以“简单、可维护、可上线”为主。

### 1.2 目标
- 管理员：在后台完成登录、写作、发布、分类/标签管理、评论审核、上传资源、站点设置。
- 访客：在前台浏览文章、分类/标签筛选、搜索、归档、热门文章、发表评论（审核后展示）。
- 后端：提供统一规范的 REST API（JWT 鉴权 + 业务接口 + 统一响应体/错误处理）。

### 1.3 非目标（不强制）
- 复杂的多角色权限体系（第一阶段只做 ADMIN/匿名即可）
- 高并发分布式/消息队列/全文检索（ES）
- 第三方登录/支付

---

## 2. 角色与权限模型

### 2.1 角色
- **VISITOR（匿名访客）**：可访问前台公开内容；可发表评论。
- **ADMIN（管理员）**：登录后台后拥有全部管理权限。

### 2.2 权限边界
- `/api/admin/**`：必须 ADMIN
- 前台公开读接口：允许匿名
- 评论创建：允许匿名；评论审核/删除：必须 ADMIN

---

## 3. 功能范围（包含可选增强）

> 说明：你已确认“包含可选需求”，因此本 PRD 将 P0 + P1 一并纳入 v1.0 交付范围；P2 仍作为可延后。

### 3.1 前台（`blog-web`）

#### v1.0（必须交付）
1) 首页文章列表（分页、摘要、封面、时间）
2) 文章详情（Markdown 渲染、封面、分类/标签；上一篇/下一篇可选）
3) 分类列表 + 分类筛选列表（分页）
4) 标签列表 + 标签筛选列表（分页）
5) 搜索（关键字；优先标题，摘要可选）
6) 归档页（按年月聚合）
7) 热门文章（按阅读量排序，简单实现）
8) 关于页 / 友链页（来自站点设置或静态兜底）
9) 评论：展示（仅已审核）+ 发表评论（默认待审核）
10) 基础 SEO：页面 title/description 基础设置

#### v1.x（可延后）
- 主题切换/暗色模式
- 图片懒加载、代码高亮优化
- RSS

### 3.2 后台（`blog-admin-web`）

#### v1.0（必须交付）
1) 管理员登录/退出
2) 仪表盘（极简统计）：文章数、分类数、标签数、评论待审核数
3) 文章管理：列表（分页/筛选/搜索）、新建/编辑、发布/撤回、删除（建议逻辑删除）
4) 分类管理：CRUD
5) 标签管理：CRUD
6) 评论管理：列表、审核通过/拒绝、删除
7) 上传：封面上传、正文图片上传
8) 资源管理：已上传文件列表、删除（推荐落库；若不落库可降级）
9) 站点设置：站点名、公告、关于内容、SEO、页脚信息
10) 管理员管理（简化）：新增管理员/重置密码（若只做一个管理员可降级）

### 3.3 后端（`blog-api`）

#### v1.0（必须交付）
1) 认证鉴权：Spring Security + JWT（ADMIN 登录）
2) 文章域：后台 CRUD/发布/撤回；前台已发布列表/详情/筛选/搜索/归档
3) 分类/标签域：后台 CRUD；前台查询
4) 评论域：匿名创建；后台审核/删除；前台仅展示已审核
5) 上传域：图片上传，本地磁盘存储 + URL 返回
6) 站点设置域：前台读取、后台更新
7) 阅读量：访问详情累加（简单版）
8) 统一规范：统一响应体、错误码、分页、参数校验、全局异常

---

## 4. 页面清单（信息架构）

### 4.1 前台页面（`blog-web`）
- `/` 首页文章列表
- `/post/:id` 文章详情
- `/categories` 分类列表
- `/category/:id` 分类文章列表
- `/tags` 标签列表
- `/tag/:id` 标签文章列表
- `/search` 搜索结果页（支持 query 参数）
- `/archives` 归档
- `/about` 关于
- `/links` 友链
- `/*` 404

### 4.2 后台页面（`blog-admin-web`）
- `/login` 登录
- `/dashboard` 仪表盘
- `/posts` 文章列表
- `/posts/new` 新建文章
- `/posts/:id/edit` 编辑文章
- `/categories` 分类管理
- `/tags` 标签管理
- `/comments` 评论管理（审核）
- `/resources` 资源管理
- `/settings` 站点设置
- `/admins` 管理员管理（如启用）

---

## 5. API 范围（资源级接口清单）

> 响应体、分页、鉴权约定参见 `docs/api-conventions.md`。

### 5.1 认证（Admin）
- `POST /api/admin/auth/login` 登录
- `POST /api/admin/auth/logout` 退出（可选）
- `GET  /api/admin/auth/me` 当前管理员信息

### 5.2 文章（Admin）
- `GET  /api/admin/posts` 分页列表（支持 status/keyword/categoryId/tagId）
- `POST /api/admin/posts` 新建
- `GET  /api/admin/posts/{id}` 详情
- `PUT  /api/admin/posts/{id}` 更新
- `DELETE /api/admin/posts/{id}` 删除
- `PUT  /api/admin/posts/{id}/publish` 发布
- `PUT  /api/admin/posts/{id}/unpublish` 撤回

### 5.3 分类/标签（Admin）
- `GET/POST/PUT/DELETE /api/admin/categories...`
- `GET/POST/PUT/DELETE /api/admin/tags...`

### 5.4 上传/资源（Admin）
- `POST /api/admin/upload/image` 上传图片（返回 url）
- `GET  /api/admin/resources` 资源分页列表（如落库）
- `DELETE /api/admin/resources/{id}` 删除资源（如落库）

### 5.5 评论（Admin）
- `GET  /api/admin/comments` 评论管理列表（含待审核）
- `PUT  /api/admin/comments/{id}/approve` 审核通过
- `PUT  /api/admin/comments/{id}/reject` 审核拒绝
- `DELETE /api/admin/comments/{id}` 删除

### 5.6 站点设置（Admin）
- `GET  /api/admin/site` 获取站点设置
- `PUT  /api/admin/site` 更新站点设置

### 5.7 前台公开接口（Web）
- `GET /api/posts` 已发布文章分页（支持 categoryId/tagId/keyword）
- `GET /api/posts/{id}` 已发布文章详情（含阅读量）
- `GET /api/categories` 分类列表
- `GET /api/tags` 标签列表
- `GET /api/archives` 归档
- `GET /api/site` 站点设置
- `GET /api/posts/{id}/comments` 评论列表（仅已审核）
- `POST /api/posts/{id}/comments` 新增评论（匿名）

---

## 6. 数据模型范围（建议）

- `admin_user`
- `post`
- `category`
- `tag`
- `post_tag`
- `comment`
- `site_setting`
- `file_resource`（支持资源管理；如不落库可降级）

---

## 7. 验收标准（DoD）

建议直接以 `docs/acceptance-checklist.md` 为准；v1.0 至少满足：
- 后台登录、权限拦截（401/403）
- 发文发布闭环（后台发布→前台可见）
- 分类/标签/搜索/归档可用
- 评论闭环（提交→待审核→审核后展示）
- 站点设置修改后前台可展示
- 上传图片可访问并在前台展示

---

## 8. 风险与约束

- MySQL 5.5 较老：字符集、索引长度、驱动兼容要保守。
- 评论开启后存在 spam 风险：默认全审核；后续可加验证码/频率限制。
- 本地磁盘存储简化部署，但要有备份与目录权限管理。
