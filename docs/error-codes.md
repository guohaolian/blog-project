# Error Codes（统一错误码表）

> 约定：接口统一返回 `ApiResponse`，其中：
> - `code=0` 表示成功
> - `code!=0` 表示失败，错误信息在 `message`
>
> 注意：HTTP status 仍会按语义返回（400/401/403/404/500），但前端/调用方以 `code` 作为业务判断主依据。

## 通用错误码

| code | name | default message | HTTP status | 说明 |
|---:|---|---|---:|---|
| 0 | OK | ok | 200 | 成功 |
| 40001 | BAD_REQUEST | bad request | 400 | 参数错误/校验失败/请求体解析失败 |
| 40100 | UNAUTHORIZED | unauthorized | 401 | 未登录/Token 无效/过期 |
| 40300 | FORBIDDEN | forbidden | 403 | 无权限 |
| 40400 | NOT_FOUND | not found | 404 | 资源不存在 |
| 50000 | SYSTEM_ERROR | system error | 500 | 未捕获异常/系统错误 |

## 管理端 / 账号

| code | name | default message | HTTP status | 说明 |
|---:|---|---|---:|---|
| 41001 | ADMIN_USERNAME_OR_PASSWORD_INVALID | username or password invalid | 400 | 管理员用户名或密码错误 |
| 41002 | ADMIN_USER_DISABLED | admin user disabled | 403 | 管理员被禁用 |

## 文章

| code | name | default message | HTTP status | 说明 |
|---:|---|---|---:|---|
| 42001 | POST_NOT_FOUND | post not found | 404 | 文章不存在 |
| 42002 | POST_NOT_PUBLISHED | post not published | 400 | 文章未发布不可访问 |

## 评论

| code | name | default message | HTTP status | 说明 |
|---:|---|---|---:|---|
| 43001 | COMMENT_NOT_FOUND | comment not found | 404 | 评论不存在 |
| 43002 | COMMENT_STATUS_INVALID | comment status invalid | 400 | 评论状态不合法 |

## 扩展方式

新增错误码时：
1. 在 `blog-api/src/main/java/com/example/blog/common/ErrorCode.java` 增加枚举项
2. 在本文件补充一行表格
3. 如需映射到不同 HTTP status，可在 `GlobalExceptionHandler` 中调整 `BizException` 的 http status 映射规则
