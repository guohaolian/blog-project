# OpenAPI / 接口明细（字段级草案，可直接联调）

> 本文档是 `docs/prd.md`（接口清单）的“字段级落地版”。
> 通用响应体/分页/鉴权规则见：`docs/api-conventions.md`。

---

## 0. 通用约定

### 0.1 URL 前缀
- 全部接口统一以 `/api` 开头
- 管理端以 `/api/admin` 开头

### 0.2 认证
- Header：`Authorization: Bearer <token>`
- 管理端除登录接口外都需要 token

### 0.3 字段命名
- JSON 字段统一使用 **camelCase**（例如 `coverUrl`、`publishedAt`）
- 后端数据库字段可用下划线，VO/DTO 转换时对齐 camelCase

### 0.4 时间格式
- 建议后端对外统一返回字符串：`yyyy-MM-dd HH:mm:ss`
-（或返回 epoch 毫秒时间戳，二选一；本草案使用字符串）

### 0.5 通用响应包装
所有响应都被 `ApiResponse` 包装：

```json
{
  "code": 0,
  "message": "ok",
  "data": {},
  "timestamp": 1700000000000
}
```

本文档只写 `data` 的结构。

### 0.6 通用分页模型

**PageResult<T>**
```json
{
  "list": [],
  "total": 0,
  "pageNum": 1,
  "pageSize": 10
}
```

---

## 1. 通用模型（VO）

### 1.1 CategoryVO
```json
{
  "id": 1,
  "name": "Java",
  "postCount": 12,
  "createdAt": "2026-01-30 12:00:00"
}
```

### 1.2 TagVO
```json
{
  "id": 1,
  "name": "Spring",
  "postCount": 12,
  "createdAt": "2026-01-30 12:00:00"
}
```

### 1.3 PostListItemVO（前台列表项）
```json
{
  "id": 1,
  "title": "...",
  "summary": "...",
  "coverUrl": "https://.../uploads/202601/...jpg",
  "category": {"id": 1, "name": "Java"},
  "tags": [{"id": 1, "name": "Spring"}],
  "publishedAt": "2026-01-30 12:00:00",
  "viewCount": 0
}
```

### 1.4 PostDetailVO（前台详情）
```json
{
  "id": 1,
  "title": "...",
  "summary": "...",
  "content": "# markdown...",
  "coverUrl": "https://...",
  "category": {"id": 1, "name": "Java"},
  "tags": [{"id": 1, "name": "Spring"}],
  "publishedAt": "2026-01-30 12:00:00",
  "viewCount": 12,
  "prev": {"id": 10, "title": "prev title"},
  "next": {"id": 12, "title": "next title"}
}
```

> prev/next 可选：没有则为 null。

### 1.5 AdminPostListItemVO（后台列表项）
```json
{
  "id": 1,
  "title": "...",
  "status": "DRAFT",
  "category": {"id": 1, "name": "Java"},
  "publishedAt": null,
  "updatedAt": "2026-01-30 12:00:00"
}
```

### 1.6 AdminPostEditVO（后台编辑回填）
```json
{
  "id": 1,
  "title": "...",
  "summary": "...",
  "content": "# markdown...",
  "coverUrl": "https://...",
  "categoryId": 1,
  "tagIds": [1, 2],
  "status": "DRAFT"
}
```

### 1.7 CommentVO（前台展示）
```json
{
  "id": 1,
  "nickname": "Tom",
  "content": "Nice post",
  "createdAt": "2026-01-30 12:00:00"
}
```

### 1.8 AdminCommentVO（后台管理）
```json
{
  "id": 1,
  "postId": 1,
  "postTitle": "...",
  "nickname": "Tom",
  "email": "tom@example.com",
  "content": "...",
  "status": "PENDING",
  "createdAt": "2026-01-30 12:00:00"
}
```

### 1.9 SiteSettingVO
```json
{
  "siteName": "My Blog",
  "siteNotice": "公告...",
  "aboutContent": "# About...",
  "links": [
    {"name": "GitHub", "url": "https://github.com/..."}
  ],
  "seoTitle": "My Blog",
  "seoKeywords": "blog,java,vue",
  "seoDescription": "...",
  "footerText": "Copyright..."
}
```

> `links` 如果你不想做复杂，可先存在 JSON 字符串字段里。

### 1.10 UploadResultVO
```json
{
  "url": "/uploads/202601/uuid.jpg",
  "originalName": "a.jpg",
  "size": 12345,
  "contentType": "image/jpeg"
}
```

### 1.11 FileResourceVO
```json
{
  "id": 1,
  "url": "/uploads/202601/uuid.jpg",
  "originalName": "a.jpg",
  "size": 12345,
  "contentType": "image/jpeg",
  "createdAt": "2026-01-30 12:00:00"
}
```

---

## 2. 认证（Admin）

### 2.1 POST /api/admin/auth/login

**Request**
```json
{
  "username": "admin",
  "password": "******"
}
```

**Response.data**
```json
{
  "token": "<jwt>",
  "expiresAt": 1700000000000
}
```

可能错误：
- 40001 参数错误
- 40100 用户名或密码错误

### 2.2 GET /api/admin/auth/me

**Response.data**
```json
{
  "id": 1,
  "username": "admin",
  "displayName": "Administrator",
  "roles": ["ADMIN"]
}
```

可能错误：
- 40100 未登录/token 失效

---

## 3. 文章（Web）

### 3.1 GET /api/posts
Query：
- `pageNum` (number, required, >=1)
- `pageSize` (number, required, 1~100)
- `keyword` (string, optional)
- `categoryId` (number, optional)
- `tagId` (number, optional)

**Response.data**：PageResult<PostListItemVO>

### 3.2 GET /api/posts/{id}
Path：`id`（number）

**Response.data**：PostDetailVO

可能错误：
- 40400 文章不存在或未发布

### 3.3 GET /api/archives

**Response.data**
```json
[
  {
    "month": "2026-01",
    "count": 3,
    "posts": [
      {"id": 1, "title": "...", "publishedAt": "2026-01-30 12:00:00"}
    ]
  }
]
```

### 3.4 GET /api/posts/hot
Query：
- `limit` (number, optional, default 10, max 50)

**Response.data**
```json
[
  {"id": 1, "title": "...", "viewCount": 100, "publishedAt": "2026-01-30 12:00:00"}
]
```

> 该接口不在 PRD 清单里，但前台有“热门文章”需求。若你不想加新接口，也可以用 `/api/posts?sort=viewCount`（需要额外设计）。

---

## 4. 分类 / 标签（Web）

### 4.1 GET /api/categories
**Response.data**
```json
[
  {"id": 1, "name": "Java", "postCount": 12, "createdAt": "2026-01-30 12:00:00"}
]
```

### 4.2 GET /api/tags
**Response.data**
```json
[
  {"id": 1, "name": "Spring", "postCount": 12, "createdAt": "2026-01-30 12:00:00"}
]
```

---

## 5. 评论（Web）

### 5.1 GET /api/posts/{id}/comments
Path：`id`（postId）

**Response.data**
```json
[
  {"id": 1, "nickname": "Tom", "content": "Nice", "createdAt": "2026-01-30 12:00:00"}
]
```

### 5.2 POST /api/posts/{id}/comments
Path：`id`（postId）

**Request**
```json
{
  "nickname": "Tom",
  "email": "tom@example.com",
  "content": "Nice post"
}
```

规则：
- nickname 必填（1~30）
- email 可选（若填需合法）
- content 必填（1~500）

**Response.data**
```json
{
  "commentId": 1,
  "status": "PENDING"
}
```

可能错误：
- 40400 文章不存在或未发布

---

## 6. 站点设置（Web）

### 6.1 GET /api/site
**Response.data**：SiteSettingVO

---

## 7. 文章（Admin）

### 7.1 GET /api/admin/posts
Query：
- `pageNum`、`pageSize`
- `status` (DRAFT/PUBLISHED)
- `keyword`
- `categoryId`
- `tagId`

**Response.data**：PageResult<AdminPostListItemVO>

### 7.2 POST /api/admin/posts
**Request**
```json
{
  "title": "...",
  "summary": "...",
  "content": "# markdown...",
  "coverUrl": "/uploads/...jpg",
  "categoryId": 1,
  "tagIds": [1, 2],
  "status": "DRAFT"
}
```

规则建议：
- title 必填（1~120）
- summary 可选（0~300）
- content 必填
- categoryId 可选（允许无分类）或必填（二选一，建议必填）

**Response.data**
```json
{
  "id": 1
}
```

### 7.3 GET /api/admin/posts/{id}
**Response.data**：AdminPostEditVO

### 7.4 PUT /api/admin/posts/{id}
Request 同 POST

**Response.data**
```json
{
  "updated": true
}
```

### 7.5 DELETE /api/admin/posts/{id}
**Response.data**
```json
{
  "deleted": true
}
```

### 7.6 PUT /api/admin/posts/{id}/publish
**Response.data**
```json
{
  "published": true,
  "publishedAt": "2026-01-30 12:00:00"
}
```

### 7.7 PUT /api/admin/posts/{id}/unpublish
**Response.data**
```json
{
  "unpublished": true
}
```

---

## 8. 分类 / 标签（Admin）

### 8.1 Categories
- `GET /api/admin/categories`
- `POST /api/admin/categories`
- `PUT /api/admin/categories/{id}`
- `DELETE /api/admin/categories/{id}`

**CategoryCreate/UpdateRequest**
```json
{
  "name": "Java"
}
```

**List Response.data**
```json
[
  {"id": 1, "name": "Java"}
]
```

### 8.2 Tags
- `GET /api/admin/tags`
- `POST /api/admin/tags`
- `PUT /api/admin/tags/{id}`
- `DELETE /api/admin/tags/{id}`

**TagCreate/UpdateRequest**
```json
{
  "name": "Spring"
}
```

---

## 9. 评论（Admin）

### 9.1 GET /api/admin/comments
Query：
- `pageNum`、`pageSize`
- `status` (PENDING/APPROVED/REJECTED)
- `postId` (optional)

**Response.data**：PageResult<AdminCommentVO>

### 9.2 PUT /api/admin/comments/{id}/approve
**Response.data**
```json
{"approved": true}
```

### 9.3 PUT /api/admin/comments/{id}/reject
**Response.data**
```json
{"rejected": true}
```

### 9.4 DELETE /api/admin/comments/{id}
**Response.data**
```json
{"deleted": true}
```

---

## 10. 上传与资源（Admin）

### 10.1 POST /api/admin/upload/image
Content-Type：`multipart/form-data`

FormData：
- `file`：图片文件

**Response.data**：UploadResultVO

可能错误：
- 40001 文件为空/格式不支持/超出大小

### 10.2 GET /api/admin/resources
Query：`pageNum`、`pageSize`

**Response.data**：PageResult<FileResourceVO>

### 10.3 DELETE /api/admin/resources/{id}
**Response.data**
```json
{"deleted": true}
```

---

## 11. 站点设置（Admin）

### 11.1 GET /api/admin/site
**Response.data**：SiteSettingVO

### 11.2 PUT /api/admin/site
**Request**：SiteSettingVO（同结构）

**Response.data**
```json
{"updated": true}
```
