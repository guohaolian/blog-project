# API 接口文档（自动生成方案）

本项目后端为 Spring Boot（`blog-api`）。推荐用 **OpenAPI/Swagger 自动生成**接口文档，前端/测试同学可以直接用 Swagger UI 调试。

本文档目标：
- **更精确**：接口列表永远以代码为准（自动生成）
- **更好看**：提供 Swagger UI 在线调试 + Redoc 单页 HTML 文档

## 1. 启用 Swagger UI（OpenAPI）

> 注意：**生产环境（prod）默认关闭 Swagger/OpenAPI**。
>
> - dev：用于开发联调，默认开启
> - prod：用于部署上线，默认关闭（避免公网暴露）
>
> 需要在生产临时开启时，请参考下文「1.2 生产环境临时开启」。

后端已接入 `springdoc-openapi`（见 `blog-api/pom.xml`），在 dev 环境启动后可访问：

- Swagger UI：`http://localhost:8080/swagger-ui.html`
- OpenAPI JSON：`http://localhost:8080/v3/api-docs`
- OpenAPI YAML：`http://localhost:8080/v3/api-docs.yaml`

### 1.1 分组（Web / Admin）

Swagger UI 左上角可以切换分组：
- `web`：前台公开接口（`/api/**`，排除 `/api/admin/**`）
- `admin`：管理端接口（`/api/admin/**`）

对应的分组 OpenAPI 地址（如果 springdoc 版本支持 group 导出）：
- `http://localhost:8080/v3/api-docs/web`
- `http://localhost:8080/v3/api-docs/admin`

### 1.2 生产环境临时开启（prod）

生产 profile 下默认配置：
- `springdoc.api-docs.enabled=false`
- `springdoc.swagger-ui.enabled=false`

如果你确实需要在生产临时打开（强烈建议配合 Nginx Basic Auth / IP 白名单）：

**方式 A：修改服务器上的外置配置文件**

例如 `/opt/blog/blog-api/application-prod.yml`：

```yaml
springdoc:
  api-docs:
    enabled: true
  swagger-ui:
    enabled: true
```

重启服务生效。

**方式 B：启动参数覆盖**

```bash
java -jar app.jar --spring.profiles.active=prod \
  --springdoc.api-docs.enabled=true \
  --springdoc.swagger-ui.enabled=true
```

## 2. 鉴权（JWT Bearer）在文档里的用法

管理端接口需要在请求头中携带：

- `Authorization: Bearer <token>`

在 Swagger UI 中：点击右上角 **Authorize**，填入你的 token。

> 说明：当前实现里，文档会展示 Bearer JWT 方案；具体哪些接口需要 token 仍以 Spring Security 配置为准。

## 3. 一键导出接口文档（推荐做法）

仓库已提供导出脚本（Windows PowerShell）：

- 导出 OpenAPI JSON/YAML：`scripts/export-openapi.ps1`
- 生成更好看的 HTML（Redoc）：`scripts/export-openapi-html.ps1`

### 3.1 导出 OpenAPI JSON/YAML

运行后会生成（或覆盖）文件：
- `docs/generated/openapi.json`
- `docs/generated/openapi.yaml`
- （可选）`docs/generated/openapi.web.json`
- （可选）`docs/generated/openapi.admin.json`

### 3.2 生成 HTML（更好看）

运行 HTML 生成脚本后会生成：
- `docs/generated/api-docs.html`

这个文件可以直接：
- 本地双击打开
- 或部署到服务器当静态文件对外提供

## 4. 怎么让文档更“精确”

自动生成的 OpenAPI 文档会根据代码推断字段结构，但要做到“字段必填/示例/备注”更精确，建议逐步补：

1) **参数校验注解（影响必填/范围）**
- `@NotNull` / `@NotBlank` / `@Size` / `@Min` / `@Max` ...

2) **OpenAPI 注解（影响展示文案与示例）**
- `@Schema(description = "...", example = "...")`
- `@Operation(summary = "...", description = "...")`
- `@Tag(name = "...", description = "...")`

> 建议顺序：先把“对外联调的核心 DTO”补齐（登录、发文、评论、上传、站点设置等）。

## 5. 与手写文档的关系

仓库里已有 `docs/openapi-implemented.md`（手写版，以当前代码实现为准），建议继续保留它用于：
- 业务解释
- 返回示例
- 注意事项（比如 viewCount +1 等）

自动生成的 OpenAPI 主要用于：
- 让接口列表永远跟代码一致
- 让联调可以直接在线调试
- 便于导入到 Apifox / Postman
