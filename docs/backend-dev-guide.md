# 后端开发文档（`blog-api`）

- 技术栈：JDK 11 + Spring Boot 2.7.x + Spring Security + JWT + MyBatis-Plus + MySQL 5.5
- 目标：把 `blog-api` 按统一规范落地成可联调、可部署的 REST API。

关联文档：
- PRD：`docs/prd.md`
- 接口规范：`docs/api-conventions.md`
- 命名与目录：`docs/naming-and-structure.md`
- 验收：`docs/acceptance-checklist.md`
- 本地环境：`docs/dev-setup.md`
- 数据库初始化：`docs/db-local-setup.md`
- IDEA 运行配置：`docs/idea-run-config.md`

---

## 1. 最重要的业务边界：用户端不登录（后端视角）

PRD 已明确角色：VISITOR（匿名）+ ADMIN（后台）。v1.0 **不做“用户体系/用户登录”**。

### 1.1 允许匿名访问（Web 公共接口）

建议放在 `/api/**` 下，且在 Security 中显式放行：

- `GET /api/health`
- `GET /api/posts`
- `GET /api/posts/{id}`
- `GET /api/categories`
- `GET /api/tags`
- `GET /api/archives`
- `GET /api/site`
- `GET /api/posts/{id}/comments`
- `POST /api/posts/{id}/comments`（匿名提交，默认 PENDING）

### 1.2 必须鉴权（Admin 管理接口）

- `/api/admin/**`：全部需要 ADMIN

> 关键原则：不要给 `blog-web` 设计任何 token/会话能力；前台只做匿名读 + 匿名评论提交。

---

## 2. 项目分层与包结构（建议）

- `common/`：统一响应体、错误码、异常、分页对象
- `config/`：Security、MyBatis-Plus、（可选）CORS
- `security/`：JWT 工具、过滤器、UserDetailsService
- `controller/`：只做入参校验与返回
- `service/`：业务逻辑
- `mapper/`：MyBatis-Plus mapper
- `entity/`：数据库实体
- `dto/`：请求 DTO
- `vo/`：响应 VO

规则：
- 不直接返回 entity 给前端（用 VO）
- controller 不写业务逻辑

---

## 3. 统一响应体与错误处理

对齐 `docs/api-conventions.md`。

建议：
- `ApiResponse<T>`：{code,message,data,timestamp}
- `BizException`：业务异常
- `GlobalExceptionHandler`：统一把异常变成 ApiResponse

错误码最小集合：
- 0 成功
- 40001 参数错误
- 40100 未登录/token 失效
- 40300 无权限
- 40400 不存在
- 50000 系统错误

---

## 4. Spring Security + JWT（ADMIN 登录）

### 4.1 核心要求
- `/api/admin/**` 全部需要 ADMIN
- 前台读取接口允许匿名
- 评论创建允许匿名

### 4.2 接口
- `POST /api/admin/auth/login`
- `GET /api/admin/auth/me`

### 4.3 JWT 规范
- Header：`Authorization: Bearer <token>`
- token 载荷建议：sub（管理员标识）、roles、exp
- 密码：BCrypt

---

## 5. MVP 里程碑（后端交付顺序建议）

> 目标是“先交付可用闭环”，而不是一次性做全。

- M1（框架联调）：
  - `/api/health`
  - 统一响应体 + 全局异常
  - 分页对象/分页查询约定
- M2（Admin 鉴权闭环）：
  - login/me
  - JWT filter + 安全边界（放行公共接口，拦截 `/api/admin/**`）
  - BCrypt 校验
- M3（文章闭环）：
  - admin：posts CRUD + publish/unpublish
  - web：published list/detail + 阅读量累加
- M4（分类/标签 + 搜索/归档）：
  - categories/tags CRUD（admin）
  - web 查询与筛选（含 keyword/search、archives 聚合）
- M5（评论闭环）：
  - 匿名创建 comment（默认 PENDING）
  - admin：approve/reject/delete
  - web：仅展示 APPROVED
- M6（站点设置 + 上传/资源）：
  - site_setting：web GET；admin GET/PUT
  - upload：本地磁盘存储 + 返回 `/uploads/...`
  - （可选）file_resource 落库 + resources 列表删除

---

## 6. 业务域实现要点（按 PRD）

### 6.1 文章 post
- status：DRAFT/PUBLISHED
- 前台只查 PUBLISHED
- 后台可查全部并筛选
- 阅读量：访问详情累加（简单方案即可）

### 6.2 分类/标签
- category 1:N post
- tag N:N post（post_tag）

### 6.3 评论 comment
- status：PENDING/APPROVED/REJECTED
- 创建默认 PENDING
- 前台只展示 APPROVED

### 6.4 站点设置 site_setting
- 建议固定 id=1
- 前台 GET `/api/site`
- 后台 GET/PUT `/api/admin/site`

### 6.5 资源 file_resource（可选但 v1.0 推荐）
- 上传时落库（url、原始名、大小、类型、创建时间）
- 资源列表分页 + 删除

---

## 7. 上传（本地磁盘存储，简单优先）

### 7.1 目录与 URL
- 服务器磁盘目录（配置）：例如 `/opt/blog/uploads`
- 返回 URL：`/uploads/yyyyMM/<uuid>.<ext>`

### 7.2 安全限制
- 仅允许图片类型（白名单）
- 限制大小（例如 5MB）
- 文件名使用 uuid，避免路径穿越与重名

---

## 8. MySQL 5.5 注意事项

- 字符集优先 `utf8`
- 避免超长索引
- 时间字段建议 `DATETIME`

---

## 9. 配置分层（dev/prod）

- `application-dev.yml`：本地数据库、本地上传目录
- `application-prod.yml`：生产数据库、生产上传目录、JWT 密钥

关键配置建议：
- `jwt.secret`
- `jwt.expireSeconds`
- `upload.dir`

---

## 10. Windows + IDEA 运行方式（建议）

- IDEA 运行配置参考：`docs/idea-run-config.md`
- 数据库初始化参考：`docs/db-local-setup.md`

开发期建议统一后端端口为 `8080`（保持两个前端 Vite proxy 默认配置不需要改）。

---

## 11. 最小自测清单（后端）

对照 `docs/acceptance-checklist.md`，后端侧至少保证：
- 未登录访问 `/api/admin/**` 返回 401
- 登录后携带 token 可访问
- 草稿不出现在前台，发布后可查
- 评论创建默认待审核，审核通过后前台可见
- 上传返回的 url 可访问（配合 Nginx `/uploads` 映射）
