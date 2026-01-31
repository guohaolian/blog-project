# OpenAPI（以当前代码实现为准）

> 版本：v1（implemented）
>
> 本文档**以当前仓库代码实现为准**，用于前后端联调与验收。
>
> - 接口统一约定见：`docs/api-conventions.md`
> - 项目：`blog-api` + `blog-web` + `blog-admin-web`

---

## 0. 通用约定

### 0.1 Base URL

- Web（前台公开接口）：`/api`
- Admin（后台管理接口）：`/api/admin`

### 0.2 响应包装

后端统一返回：`ApiResponse<T>`

```json
{
  "code": 0,
  "message": "ok",
  "data": {},
  "timestamp": 1700000000000
}
```

前端（`blog-web` / `blog-admin-web`）axios 拦截器会：
- `code === 0`：直接返回 `data`
- `code !== 0`：`Promise.reject(Error(message))`

### 0.3 错误码（与代码一致）

见 `blog-api/src/main/java/com/example/blog/common/ErrorCodes.java`

- `0`：OK
- `40001`：BAD_REQUEST
- `40100`：UNAUTHORIZED
- `40300`：FORBIDDEN
- `40400`：NOT_FOUND
- `50000`：SYSTEM_ERROR

> 说明：本项目对 `BizException` 会映射为合适的 HTTP 状态码（400/401/403/404），同时响应体仍返回上述 `code`。

### 0.4 分页模型

`PageResult<T>`：

```json
{
  "list": [],
  "total": 0,
  "pageNum": 1,
  "pageSize": 10
}
```

---

## 1. Web（前台）接口

### 1.1 Health

#### GET `/api/health`

- 用途：健康检查
- Auth：不需要

**Response.data**

```json
"ok"
```

---

### 1.2 Site

#### GET `/api/site`

- 用途：获取站点设置（前台展示/SEO/公告/关于/友链）
- Auth：不需要

**Response.data：SiteSettingVO（实现版）**

```json
{
  "siteName": "My Blog",
  "siteNotice": "...",
  "aboutContent": "# About...",
  "linksJson": "[{\"name\":\"GitHub\",\"url\":\"https://github.com/...\"}]",
  "seoTitle": "My Blog",
  "seoKeywords": "blog,java,vue",
  "seoDescription": "...",
  "footerText": "Copyright..."
}
```

> 说明：友链使用 `linksJson` 字符串（JSON 数组）存储，前台用 `parseLinksJson()` 解析。

---

### 1.3 Categories / Tags

#### GET `/api/categories`

- 用途：分类列表
- Auth：不需要

**Response.data：CategoryVO[]（实现版）**

```json
[{ "id": 1, "name": "Java" }]
```

#### GET `/api/tags`

- 用途：标签列表
- Auth：不需要

**Response.data：TagVO[]（实现版）**

```json
[{ "id": 1, "name": "Spring" }]
```

---

### 1.4 Posts

#### GET `/api/posts`

Query：
- `pageNum` number（>=1）
- `pageSize` number（1~100）
- `keyword` string（可选）
- `categoryId` number（可选）
- `tagId` number（可选）

**Response.data：PageResult<PostListItemVO>**

```json
{
  "list": [
    {
      "id": 1,
      "title": "...",
      "summary": "...",
      "coverUrl": "/uploads/202601/uuid.jpg",
      "category": { "id": 1, "name": "Java" },
      "tags": [{ "id": 1, "name": "Spring" }],
      "publishedAt": "2026-01-30 12:00:00",
      "viewCount": 0
    }
  ],
  "total": 1,
  "pageNum": 1,
  "pageSize": 10
}
```

#### GET `/api/posts/{id}`

- 用途：文章详情（注意：该接口会 **viewCount +1**）
- Auth：不需要

**Response.data：PostDetailVO（实现版）**

```json
{
  "id": 1,
  "title": "...",
  "summary": "...",
  "content": "# markdown...",
  "coverUrl": "/uploads/202601/uuid.jpg",
  "category": { "id": 1, "name": "Java" },
  "tags": [{ "id": 1, "name": "Spring" }],
  "publishedAt": "2026-01-30 12:00:00",
  "viewCount": 12
}
```

#### GET `/api/posts/hot`

Query：
- `limit` number（可选，默认 10，最大 50）

**Response.data：HotPostVO[]**

```json
[{ "id": 1, "title": "...", "viewCount": 12, "publishedAt": "2026-01-30 12:00:00" }]
```

#### GET `/api/posts/archives`

- 用途：归档（按年月聚合）

**Response.data：ArchiveMonthGroupVO[]**

```json
[
  {
    "month": "2026-01",
    "count": 2,
    "posts": [
      { "id": 1, "title": "...", "publishedAt": "2026-01-30 12:00:00" }
    ]
  }
]
```

---

### 1.5 Comments（前台）

#### GET `/api/posts/{id}/comments`

- 用途：获取文章评论（仅已审核 APPROVED）

**Response.data：CommentVO[]**

```json
[{ "id": 1, "nickname": "Tom", "content": "Nice post", "createdAt": "2026-01-30 12:00:00" }]
```

#### POST `/api/posts/{id}/comments`

- 用途：创建评论（匿名允许），默认进入待审核

**Request**

```json
{ "nickname": "Tom", "email": "tom@example.com", "content": "Nice post" }
```

**Response.data：number（commentId）**

```json
1
```

---

## 2. Static（Uploads）

### GET `/uploads/**`

- 用途：访问上传的静态资源
- Auth：不需要

> 说明：后端将 `app.upload.dir` 映射为 `/uploads/**`。
>
> 为了让“删除资源后快速生效”，当前实现对 `/uploads/**` **禁用强缓存**（cachePeriod=0）。

---

## 3. Admin（后台）接口

> 说明：除登录接口外，Admin 接口都需要 Header：`Authorization: Bearer <token>`

### 3.1 Auth

#### POST `/api/admin/auth/login`

**Request**

```json
{ "username": "admin", "password": "******" }
```

**Response.data：AdminLoginResponse（实现版）**

```json
{ "token": "<jwt>" }
```

#### GET `/api/admin/auth/me`

**Response.data：AdminMeResponse（实现版）**

```json
{ "id": 1, "username": "admin", "displayName": "Administrator" }
```

---

### 3.2 Dashboard

#### GET `/api/admin/dashboard/stats`

**Response.data：AdminDashboardStatsVO**

```json
{
  "draftCount": 0,
  "publishedCount": 0,
  "categoriesCount": 0,
  "tagsCount": 0,
  "commentsPendingCount": 0,
  "totalViews": 0
}
```

---

### 3.3 Posts（Admin）

#### GET `/api/admin/posts`

Query：
- `pageNum` number
- `pageSize` number
- `status` string（可选：DRAFT/PUBLISHED）
- `keyword` string（可选）

**Response.data：PageResult<AdminPostListItemVO>**

#### POST `/api/admin/posts`

**Request**

```json
{ "title": "...", "summary": "...", "content": "#...", "coverUrl": "/uploads/...", "categoryId": 1, "tagIds": [1,2] }
```

**Response.data：number（newPostId）**

#### GET `/api/admin/posts/{id}`

**Response.data：AdminPostEditVO**

#### PUT `/api/admin/posts/{id}`

**Response.data：null**

#### DELETE `/api/admin/posts/{id}`

**Response.data：null**

#### PUT `/api/admin/posts/{id}/publish`

**Response.data：null**

#### PUT `/api/admin/posts/{id}/unpublish`

**Response.data：null**

#### GET `/api/admin/posts/meta/categories`

**Response.data：CategoryVO[]**（`{id,name}`）

#### GET `/api/admin/posts/meta/tags`

**Response.data：TagVO[]**（`{id,name}`）

---

### 3.4 Categories（Admin）

#### GET `/api/admin/categories`

**Response.data：CategoryVO[]**（`{id,name}`）

#### POST `/api/admin/categories`

Request：`{ "name": "Java" }`

**Response.data：number（newId）**

#### PUT `/api/admin/categories/{id}`

**Response.data：null**

#### DELETE `/api/admin/categories/{id}`

**Response.data：null**

---

### 3.5 Tags（Admin）

#### GET `/api/admin/tags`

**Response.data：TagVO[]**（`{id,name}`）

#### POST `/api/admin/tags`

Request：`{ "name": "Spring" }`

**Response.data：number（newId）**

#### PUT `/api/admin/tags/{id}`

**Response.data：null**

#### DELETE `/api/admin/tags/{id}`

**Response.data：null**

---

### 3.6 Comments（Admin）

#### GET `/api/admin/comments`

Query：
- `pageNum` number
- `pageSize` number
- `status` string（可选：PENDING/APPROVED/REJECTED）
- `postId` number（可选）

**Response.data：PageResult<AdminCommentVO>**

#### PUT `/api/admin/comments/{id}/approve`

**Response.data：null**

#### PUT `/api/admin/comments/{id}/reject`

**Response.data：null**

#### DELETE `/api/admin/comments/{id}`

**Response.data：null**

---

### 3.7 Upload

#### POST `/api/admin/upload/image`

- Content-Type：`multipart/form-data`
- Form field：`file`

**Response.data：UploadResultVO**

```json
{
  "url": "/uploads/202601/uuid.jpg",
  "originalName": "a.jpg",
  "size": 12345,
  "contentType": "image/jpeg"
}
```

限制（与当前代码一致）：
- 尺寸：<= 10MB
- content-type 允许：`image/jpeg` / `image/jpg` / `image/png` / `image/gif` / `image/webp`
- content-type 不在白名单时：若文件扩展名为 `jpg/png/gif/webp` 也允许兜底

---

### 3.8 Resources

#### GET `/api/admin/resources`

Query：
- `pageNum` number
- `pageSize` number
- `keyword` string（可选）
- `contentTypePrefix` string（可选，例如 `image/`）

**Response.data：PageResult<FileResourceVO>**

#### DELETE `/api/admin/resources/{id}`

**Response.data**

```json
{ "deleted": true }
```

> 说明：删除资源只会删除资源记录和服务器文件，不会自动修改文章内容中的引用。

---

### 3.9 Admins（管理员管理）

#### GET `/api/admin/admins`

**Response.data：AdminUserVO[]**

#### POST `/api/admin/admins`

**Request**

```json
{ "username": "admin2", "password": "******", "displayName": "Admin 2" }
```

**Response.data**

```json
{ "id": 2 }
```

#### PUT `/api/admin/admins/{id}/reset-password`

Request：`{ "newPassword": "******" }`

**Response.data**

```json
{ "reset": true }
```

#### PUT `/api/admin/admins/{id}/status`

Request：`{ "status": 0 }`

**Response.data**

```json
{ "updated": true }
```

---

## 4. 前端路由与接口映射（实现提示）

- `blog-web`
  - 首页：调用 `GET /api/posts`
  - 文章详情：调用 `GET /api/posts/{id}`（会 viewCount+1）
  - 归档：调用 `GET /api/posts/archives`
  - 分类/标签：调用 `GET /api/categories` / `GET /api/tags`
  - 评论：调用 `GET/POST /api/posts/{id}/comments`
  - 站点设置：调用 `GET /api/site`（并通过 `site.startAutoRefresh()` 默认 15 秒自动刷新）

- `blog-admin-web`
  - 登录：`POST /api/admin/auth/login`
  - 仪表盘：`GET /api/admin/dashboard/stats`
  - 文章：`/api/admin/posts*`
  - 分类/标签：`/api/admin/categories` / `/api/admin/tags`
  - 评论：`/api/admin/comments*`
  - 上传：`POST /api/admin/upload/image`
  - 资源：`/api/admin/resources*`
  - 管理员：`/api/admin/admins*`

---

## 5. 变更记录

- 2026-01-31：从 `docs/openapi-draft.md` 分叉，生成 implemented 版本，以当前代码为准。
