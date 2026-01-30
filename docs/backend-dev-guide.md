# 后端开发文档（`blog-api`）

- 技术栈：JDK 11 + Spring Boot 2.7.x + Spring Security + JWT + MyBatis-Plus + MySQL 5.5
- 目标：把 `blog-api` 按统一规范落地成可联调、可部署的 REST API。

关联文档：
- PRD：`docs/prd.md`
- 接口规范：`docs/api-conventions.md`
- 命名与目录：`docs/naming-and-structure.md`
- 验收：`docs/acceptance-checklist.md`

---

## 1. 项目分层与包结构（建议）

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

## 2. 统一响应体与错误处理

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

## 3. Spring Security + JWT（ADMIN 登录）

### 3.1 核心要求
- 管理端 `/api/admin/**` 全部需要 ADMIN
- 前台读接口允许匿名
- 评论创建允许匿名

### 3.2 接口
- `POST /api/admin/auth/login`
- `GET /api/admin/auth/me`

### 3.3 JWT 规范
- Header：`Authorization: Bearer <token>`
- token 载荷建议：sub(管理员标识)、roles、exp
- 密码：BCrypt

---

## 4. 业务域实现要点（按 PRD）

### 4.1 文章 post
- status：DRAFT/PUBLISHED
- 前台只查 PUBLISHED
- 后台可查全部并筛选
- 阅读量：访问详情累加（简单方案即可）

### 4.2 分类/标签
- category 1:N post
- tag N:N post（post_tag）

### 4.3 评论 comment
- status：PENDING/APPROVED/REJECTED
- 创建默认 PENDING
- 前台只展示 APPROVED

### 4.4 站点设置 site_setting
- 建议固定 id=1
- 前台 GET /api/site
- 后台 GET/PUT /api/admin/site

### 4.5 资源 file_resource（可选但 v1.0 推荐）
- 上传时落库（url、原始名、大小、类型、创建时间）
- 资源列表分页 + 删除

---

## 5. 上传（本地磁盘存储，简单优先）

### 5.1 目录与 URL
- 服务器磁盘目录（配置）：例如 `/opt/blog/uploads`
- 返回 URL：`/uploads/yyyyMM/<uuid>.<ext>`

### 5.2 安全限制
- 仅允许图片类型（白名单）
- 限制大小（例如 5MB）
- 文件名使用 uuid，避免路径穿越与重名

---

## 6. MySQL 5.5 注意事项

- 字符集优先 `utf8`
- 避免超长索引
- 时间字段建议 `DATETIME`

---

## 7. 配置分层（dev/prod）

- `application-dev.yml`：本地数据库、本地上传目录
- `application-prod.yml`：生产数据库、生产上传目录、JWT 密钥

关键配置建议：
- `jwt.secret`
- `jwt.expireSeconds`
- `upload.dir`

---

## 8. 最小自测清单（后端）

对照 `docs/acceptance-checklist.md`，后端侧至少保证：
- 未登录访问 `/api/admin/**` 返回 401
- 登录后携带 token 可访问
- 草稿不出现在前台，发布后可查
- 评论创建默认待审核，审核通过后前台可见
- 上传返回的 url 可访问（配合 Nginx `/uploads` 映射）
