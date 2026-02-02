# 文档索引

- PRD（业务需求,以当前代码为准、状态表见 PRD 第9章）：`prd.md`
- 验收清单（QA Checklist 以当前代码为准、状态表见 PRD 第9章）：`acceptance-checklist.md`

- 前端开发文档：`frontend-dev-guide.md`
- 后端开发文档：`backend-dev-guide.md`

- 接口规范（响应体/分页/鉴权/CORS）：`api-conventions.md`
- API 接口文档（自动生成方案 / Swagger/OpenAPI 导出）：`api-generated.md`
- 错误码表（对外契约）：`error-codes.md`

- 接口明细（字段级，以当前实现为准）：`openapi-implemented.md`
- OpenAPI 草案（历史参考，不作为实现依据）：`openapi-draft.md`

- 数据库表结构草案（说明）：`db-schema-draft.md`

- 基线技术栈与版本：`stack-and-versions.md`
- 命名与目录结构：`naming-and-structure.md`
- npm 使用约定：`npm-guidelines.md`
- 本地开发环境搭建：`dev-setup.md`
- 开发路线图(以当前代码为准、状态表见 PRD 第9章)：`roadmap.md`

- 阿里云 ECS 部署（包含：生产环境默认关闭 Swagger，如临时开启、Nginx BasicAuth/IP 白名单）：`deploy-ecs-nginx.md`
  - 说明：前台/后台路由使用 **Vue Router history 模式** 时，部署到 Nginx 必须配置 **SPA fallback**（例如 `try_files ... /index.html;`，admin 需回退到 `/admin/index.html`），否则“刷新/直达子路由”会 404。对应可直接参考：`../deploy/nginx/blog.conf.example`

## docs/generated（接口文档生成产物）

> 由脚本自动生成，通常不手工编辑。

- OpenAPI：
  - `generated/openapi.json`
  - `generated/openapi.yaml`
  - `generated/openapi.web.json`（web 分组）
  - `generated/openapi.admin.json`（admin 分组）
- 可阅读文档：
  - `generated/api-docs.web.html`
  - `generated/api-docs.admin.html`
  - `generated/api-docs.md`

## deploy 目录（可直接复用）
- MySQL 初始化 SQL：`../deploy/sql/init.sql`
- Nginx 配置模板：`../deploy/nginx/blog.conf.example`
- systemd 服务模板：`../deploy/systemd/blog-api.service.example`

- OpenAPI (implemented, source of truth): `openapi-implemented.md`
