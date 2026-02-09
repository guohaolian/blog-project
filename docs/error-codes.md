# Error Codes（统一错误码表）

> 约定：接口统一返回 `ApiResponse`（JSON）。
>
> **统一返回体（后端返回）**
>
> - 成功：HTTP 2xx
>   ```json
>   { "code": 0, "message": "ok", "data": "<T>", "timestamp": 1700000000000 }
>   ```
> - 失败：HTTP 4xx/5xx（按语义映射）
>   ```json
>   { "code": 40001, "message": "bad request", "data": null, "timestamp": 1700000000000 }
>   ```
>
> **前端接收（axios 封装）**
>
> - `code === 0`：返回 `body.data`（业务方拿到的就是 data）
> - `code !== 0`：统一 `Promise.reject(new Error(message))`
>   - 管理端（`blog-admin-web`）额外处理：当 `code === 40100` 时会先提示（如 `登录已过期，请重新登录`），再清理 token（`auth.clearToken()`），用于“未登录/Token 失效”场景
> - HTTP 非 2xx（例如 400/401/403/404/500）：依然会尝试从 `err.response.data` 解析 `{code,message}`，确保提示语义化（不再只显示 `Request failed with status code xxx`）
> - 网络错误/超时：
>   - 超时：`Request timeout`
>   - 其它网络错误：`Network error` 或 axios 的 `err.message`

## HTTP status 映射规则（实现口径）

后端对 `BizException` 的 HTTP status 映射由 `GlobalExceptionHandler.mapBizStatus()` 决定。

当前规则（已按业务语义补齐）：

- `40100 (UNAUTHORIZED)` -> HTTP 401
- `40300 (FORBIDDEN)` / `41002 (ADMIN_USER_DISABLED)` -> HTTP 403
- `40400 (NOT_FOUND)` / `42001 (POST_NOT_FOUND)` / `43001 (COMMENT_NOT_FOUND)` -> HTTP 404
- 其它业务错误码（包括 `40001` 及其它 4xxxx 业务码） -> HTTP 400

未捕获异常 -> HTTP 500（`50000 SYSTEM_ERROR`）。

> 备注：security 层（如未登录、无权限、token 无效）同样返回 `ApiResponse`，并带 401/403 HTTP status。
> - token 无效：`40100` + message `invalid token`（由 `JwtAuthFilter` 返回）

## 错误码总表（以 `ErrorCode.java` 为准）

> 表格说明：
> - 「后端返回体」固定是 `ApiResponse{code,message,data:null,timestamp}`，这里主要写 message 的典型语义
> - 「前端提示」表示最终业务方能拿到/展示的错误信息（即 `Error(message)` 的 message）

| code | name | default message（后端） | HTTP status（后端） | 后端返回体示例（简写） | 前端提示（admin/web） |
|---:|---|---|---:|---|---|
| 0 | OK | ok | 200 | `{code:0,data:<T>}` | 返回 `data`（不报错） |
| 40001 | BAD_REQUEST | bad request | 400 | `{code:40001,message:"bad request"}` | 报错 `Error("bad request" 或具体校验信息)` |
| 40100 | UNAUTHORIZED | unauthorized | 401 | `{code:40100,message:"unauthorized"}` / `invalid token` | 报错 message；**admin** 会清 token |
| 40300 | FORBIDDEN | forbidden | 403 | `{code:40300,message:"forbidden"}` | 报错 `forbidden` |
| 40400 | NOT_FOUND | not found | 404 | `{code:40400,message:"not found"}` | 报错 `not found` |
| 50000 | SYSTEM_ERROR | system error | 500 | `{code:50000,message:"system error"}`（实际可能为异常 message） | 报错后端 message（用于开发排查） |
| 41001 | ADMIN_USERNAME_OR_PASSWORD_INVALID | username or password invalid | 400 | `{code:41001,message:"username or password invalid"}` | 报错 `username or password invalid` |
| 41002 | ADMIN_USER_DISABLED | admin user disabled | 403 | `{code:41002,message:"admin user disabled"}` | 报错 `admin user disabled` |
| 42001 | POST_NOT_FOUND | post not found | 404 | `{code:42001,message:"post not found"}` | 报错 `post not found` |
| 42002 | POST_NOT_PUBLISHED | post not published | 400 | `{code:42002,message:"post not published"}` | 报错 `post not published` |
| 43001 | COMMENT_NOT_FOUND | comment not found | 404 | `{code:43001,message:"comment not found"}` | 报错 `comment not found` |
| 43002 | COMMENT_STATUS_INVALID | comment status invalid | 400 | `{code:43002,message:"comment status invalid"}` | 报错 `comment status invalid` |

## 管理端登录错误（当前实现）

- 用户名/密码错误 -> `41001 ADMIN_USERNAME_OR_PASSWORD_INVALID`
  - HTTP 400
  - body.message: `username or password invalid`
- 用户被禁用/状态不可用 -> `41002 ADMIN_USER_DISABLED`
  - HTTP 403
  - body.message: `admin user disabled`

前端最终都会显示后端的 `message`。

## 兼容层说明：ErrorCodes.java

后端仍保留 `ErrorCodes.java`（标记为 deprecated）作为 **历史代码兼容层**。

- 新代码推荐使用：`ErrorCode`（枚举）
- 旧代码可能仍在用：`ErrorCodes.BAD_REQUEST/UNAUTHORIZED/...`（int 常量）

只要项目中还有对 `ErrorCodes` 的引用，就不能删除该文件；如需清理，可全局替换为 `ErrorCode.xxx.getCode()` 或直接抛 `new BizException(ErrorCode.xxx)`。

## 扩展方式

新增错误码时：
1. 在 `blog-api/src/main/java/com/example/blog/common/ErrorCode.java` 增加枚举项
2. 在本文件表格中追加一行，并补充“HTTP status（后端）/前端提示”的语义
3. 如需映射到不同 HTTP status，可在 `GlobalExceptionHandler.mapBizStatus()` 中调整规则
