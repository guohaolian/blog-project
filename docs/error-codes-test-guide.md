# 错误码复现测试手册（基于当前实现）

本文档用于**按错误码逐条复现/验证**：
- 后端返回的 `ApiResponse{ code, message, data, timestamp }`
- HTTP status 是否符合映射规则
- 前端（管理端/用户端）是否能弹出语义化提示（message）

> 适用范围：
> - 后端：`blog-api`
> - 管理端：`blog-admin-web`
> - 用户端：`blog-web`
>
> 约定（与实现一致）：
> - `code===0`：前端返回 `data`
> - `code!==0`：前端 `Promise.reject(new Error(message))`
> - 管理端遇到 `40100`：会弹提示（登录过期/invalid token），然后清 token 并**立即跳转**到 `/login`

---

## 测试前准备

### 1) 启动服务（示例）
按你项目当前习惯启动（可用仓库 `scripts/` 下脚本）。确保：
- 后端 API 可访问（通常为 `http://localhost:8080`，由 nginx/vite 代理到 `/api`）
- 管理端：`http://localhost:5174`（以你的 Vite 端口为准）
- 用户端：`http://localhost:5173`（以你的 Vite 端口为准）

### 2) 浏览器 DevTools 设置
- Network 勾选 **Preserve log**（跳转后也能看到 401/404 等请求）
- Console 打开，便于看到页面 catch 的错误

### 3) 复现时的“验收点”
每条错误都建议至少确认这三项：
1. HTTP status 符合预期
2. response body 的 `code/message` 符合预期
3. 前端是否提示了 `message`（管理端通常为 ElementPlus `ElMessage`，用户端看页面实现）

---

## 错误码：0 OK

**目的**：验证成功响应。

**复现方式**：随便调用一个成功接口。
- 管理端：登录成功后进入“文章列表/分类/标签”等页面（任意成功请求）
- 用户端：打开首页/文章列表

**期望**：
- HTTP 200
- body：`code=0`，`data!=null`
- 前端无错误弹窗

---

## 错误码：40001 BAD_REQUEST（HTTP 400）

> 常见来源：参数校验失败、请求体 JSON 解析失败。

### 复现方式 A（推荐，最容易）：请求体非法 JSON
1. 打开浏览器 DevTools → Console
2. 用 `fetch` 向任意 **POST JSON** 接口发送非法 JSON。

示例（登录接口）：
```js
fetch('/api/admin/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: '{"username":' // 故意截断
}).then(r => r.json()).then(console.log).catch(console.error)
```

**期望**：
- HTTP 400
- `code=40001`
- `message` 为后端解析错误信息（可能是技术信息）或 `bad request`

### 复现方式 B：缺少必填字段（触发表单校验）
对带 `@Valid` 的接口，传空字段/缺字段。比如登录把 username 传空：
```js
fetch('/api/admin/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ username: '', password: '' })
}).then(r => r.json()).then(console.log)
```

---

## 错误码：40100 UNAUTHORIZED（HTTP 401）

> 典型场景：未登录、token 失效、token 无效。

### 复现方式 A（管理端最常用）：手动把 token 弄坏
1. 先在管理端正常登录进入后台
2. 打开 Console，执行：
```js
localStorage.setItem('BLOG_ADMIN_TOKEN', 'bad-token')
```
3. 点击任意会发请求的页面（文章列表/设置页等）或直接刷新

**期望（管理端）**：
- Network 看到某个请求返回：HTTP 401，body `code=40100`，message 常见为 `invalid token` 或 `unauthorized`
- 页面弹出 warning（例如 `invalid token` 或 `登录已过期，请重新登录`）
- 随后自动跳转到 `/login`

### 复现方式 B：未登录直接访问受保护路由
1. 清空 token：
```js
localStorage.removeItem('BLOG_ADMIN_TOKEN')
```
2. 在地址栏输入：`/admin/posts`

**期望**：
- 直接跳转 `/login?redirect=...`
- 该方式一般不弹“token 失效”提示（因为根本没走请求返回 40100）

---

## 错误码：40300 FORBIDDEN（HTTP 403）

> 场景：已登录但无权限。

**说明**：是否能复现取决于系统是否存在“低权限账号/不同角色”。如果目前只有单一管理员角色，可能无法触发。

### 复现方式（需要权限差异账号）
1. 用权限较低账号登录
2. 访问需要更高权限的页面/接口（例如管理员管理、资源删除等）

**期望**：
- HTTP 403
- body：`code=40300`，message=`forbidden`
- 前端弹出 `forbidden`

---

## 错误码：40400 NOT_FOUND（HTTP 404）

> 场景：请求了一个**不存在的 API 路径**，并且后端把它包装成统一 `ApiResponse` 返回（`code=40400`）。
>
> 注意：
> - **业务域 Not Found**（例如文章/评论不存在）优先使用业务码：`42001/43001`，不要用 40400。
> - 有些框架/代理层对 404 可能直接返回 HTML 或 plain text（这时前端拿不到 `{code,message}`）。
>

### 复现方式 A（推荐）：直接请求一个后端一定不存在的路径

1. 打开任意前端页面（管理端或用户端）
2. DevTools → Console 执行：

```js
fetch('/api/__this_path_should_not_exist__', {
  method: 'GET',
}).then(async (r) => ({ status: r.status, body: await r.json() }))
  .then(console.log)
```

**期望**（当前实现，稳定）：
- `status: 404`
- `body.code === 40400`
- `body.message === "not found"`
- `body.data === null`

> 如果你仍然看到 Spring 默认的 `{error, path, status, ...}` JSON：请确认后端已重启并加载了最新配置（`spring.mvc.throw-exception-if-no-handler-found=true`）。

### 复现方式 B：对比验证（业务 Not Found）

- 文章不存在：访问 `42001 POST_NOT_FOUND`
- 评论不存在：触发 `43001 COMMENT_NOT_FOUND`

这些能稳定返回标准 `ApiResponse`，并且 HTTP 也是 404。

---

## 错误码：50000 SYSTEM_ERROR（HTTP 500）

> 场景：未捕获异常。

**说明**：不建议在生产环境刻意制造。测试环境可在已知会触发异常的接口上复现。

### 复现方式（需要你知道一个会报错的接口）
- 例如：传入会导致后端 NPE / 数据库异常的参数

**期望**：
- HTTP 500
- body：`code=50000`，message 为异常 message（可能是技术信息）
- 前端弹出该 message（用于开发排查）

---

## 错误码：41001 ADMIN_USERNAME_OR_PASSWORD_INVALID（HTTP 400）

### 复现方式：管理端登录输入错误密码
1. 打开管理端 `/login`
2. 输入存在的用户名 + 错误密码（或不存在用户名）

**期望**：
- HTTP 400
- body：`code=41001`，message=`username or password invalid`
- 管理端登录页提示该 message（取决于 LoginView 是否 catch 后显示）

---

## 错误码：41002 ADMIN_USER_DISABLED（HTTP 403）

### 复现方式：使用 status!=1 的管理员账号登录
前提：数据库里存在一个管理员用户，其 `status != 1`。

1. 用该账号在管理端登录

**期望**：
- HTTP 403
- body：`code=41002`，message=`admin user disabled`
- 前端弹出该 message

> 如果你没有禁用账号，可在数据库中把某个管理员的 status 改为 0 后测试。

---

## 错误码：42001 POST_NOT_FOUND（HTTP 404）

### 复现方式（管理端）：编辑不存在文章
1. 管理端登录后
2. 地址栏输入：
   - `http://localhost:5174/admin/posts/99999/edit`

**期望（管理端）**：
- 调用 `GET /api/admin/posts/99999` 返回 HTTP 404，`code=42001`（或 message 为 `post not found`）
- 页面弹出 `post not found`
- 随后自动跳转回文章列表 `/admin/posts`

---

## 错误码：42002 POST_NOT_PUBLISHED（HTTP 400）

> 场景：**前台**访问未发布文章。
>
> 关键点：用管理端接口通常不会触发，因为管理端允许查看草稿；要用用户端/公开接口访问。

### 复现步骤（推荐）
1. 管理端新建一篇文章，保存为草稿（不要 Publish）
2. 记录文章 id
3. 在用户端（blog-web）尝试访问该文章详情页（以你项目实际路由为准）：
   - 例如：`/posts/{id}` 或 `/post/{id}`
4. 或在用户端页面打开 DevTools，找到“文章详情接口”的真实请求路径，然后替换 id 为草稿文章 id。

**期望**：
- HTTP 400
- body：`code=42002`，message=`post not published`
- 用户端应展示该 message（具体 UI 取决于页面是否做了错误提示；axios 层会 reject `Error(message)`）

---

## 错误码：43001 COMMENT_NOT_FOUND（HTTP 404）

> 场景：对一个不存在的 comment id 做“审核/驳回”等操作。
>
> 后端实现位置：`AdminCommentController` -> `AdminCommentService.updateStatus()`。

### 复现方式（推荐：管理端审核不存在评论）

1. 先登录管理端（确保 localStorage 有 `BLOG_ADMIN_TOKEN`）
2. 打开浏览器 DevTools → Console
3. 执行下面任意一个（选一个即可）：

**Approve 不存在的评论 id**（例如 99999）：
```js
fetch('/api/admin/comments/99999/approve', {
  method: 'PUT',
  headers: {
    Authorization: 'Bearer ' + localStorage.getItem('BLOG_ADMIN_TOKEN'),
    'Content-Type': 'application/json',
  },
}).then(async (r) => ({ status: r.status, body: await r.json() }))
  .then(console.log)
```

**Reject 不存在的评论 id**：
```js
fetch('/api/admin/comments/99999/reject', {
  method: 'PUT',
  headers: {
    Authorization: 'Bearer ' + localStorage.getItem('BLOG_ADMIN_TOKEN'),
    'Content-Type': 'application/json',
  },
}).then(async (r) => ({ status: r.status, body: await r.json() }))
  .then(console.log)
```

**期望**：
- HTTP 404
- body：`code=43001`，message=`comment not found`
- 管理端前端如果用页面按钮触发，会弹出 `comment not found`

---

## 错误码：43002 COMMENT_STATUS_INVALID（HTTP 400）

> 说明（重要）：**当前后端实现里该错误码尚不可通过现有接口触发**。
>
> 原因：管理端评论相关接口只有：
> - `PUT /api/admin/comments/{id}/approve`（内部固定把 status 设置为 `APPROVED`）
> - `PUT /api/admin/comments/{id}/reject`（内部固定把 status 设置为 `REJECTED`）
>
> 也就是说前端/外部调用方**没有机会传入“非法 status”**，所以不会出现 43002。

### 如果你希望 43002 可测试/可用（建议做法）

新增一个通用接口（示例）：
- `PUT /api/admin/comments/{id}/status`
- body: `{ "status": "PENDING|APPROVED|REJECTED" }`

然后在 service 里校验 status：
- 合法：更新
- 非法：抛 `new BizException(ErrorCode.COMMENT_STATUS_INVALID)`

> 需要我直接把这个接口补上并同步更新前端/文档的话，告诉我要不要做。

---

## 网络错误 / 超时（无 code）

> 当前实现（两端前端）：
> - 当发生断网/CORS/服务不可达等“网络错误”时，会弹出 `ElMessage.error(...)`（做了 1.5s 节流，避免并发请求刷屏）
> - 当发生超时（10s）时，会弹出 `Request timeout`

### 网络错误复现
1. DevTools → Network → 勾选 Offline
2. 在管理端或用户端点击任意会请求的页面

**期望**：
- 立刻弹出错误提示（通常是 `Network Error` / 浏览器错误 message / `Network error`）

### 超时复现
1. DevTools → Network → Throttling 选择 Slow 3G（或更慢）
2. 触发一个相对耗时的请求（例如文章列表/资源列表）

**期望**：
- 若超过 10s：弹出 `Request timeout`

---

## 附录：如何快速定位“某个页面到底请求了哪个 API”

1. 打开 DevTools → Network
2. 过滤 `Fetch/XHR`
3. 点击你要测试的页面/按钮
4. 在 Network 条目里看 Request URL（把它用在本文的 fetch 示例里，就能精准复现相同错误）
