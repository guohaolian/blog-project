# 命名规范与目录结构

## 1) 三个项目命名（固定）

- 用户前台 Web：`blog-web`
- 后台管理 Web：`blog-admin-web`
- Java 后端服务：`blog-api`

命名原则：全小写、短横线分隔、避免下划线/中文、可直接用于域名与系统服务名。

## 2) 推荐顶层目录结构（单仓三子项目）

```
blog-project/
  blog-web/
  blog-admin-web/
  blog-api/
  docs/
  deploy/
  scripts/
  README.md
```

说明：
- `docs/`：项目规范、接口约定、部署文档、排障手册
- `deploy/`：生产部署模板（nginx、systemd、SQL）
- `scripts/`：本地/CI 一键脚本（后续补齐）

## 3) 前端项目内部结构（建议）

`blog-web/` 与 `blog-admin-web/` 尽量保持一致：

```
src/
  api/            # axios 封装与接口模块
  assets/
  components/
  router/
  stores/         # pinia
  utils/          # token、format、request 等
  views/
  App.vue
  main.ts
```

## 4) 后端项目内部结构（建议）

```
src/main/java/.../
  common/         # 统一响应体、异常、枚举、常量
  config/         # Security、CORS、MyBatis-Plus 等配置
  controller/
  dto/
  entity/
  mapper/
  security/       # JWT 过滤器、认证/授权工具类
  service/
  vo/

src/main/resources/
  application.yml
  application-dev.yml
  application-prod.yml
  mapper/         # xml（如使用）
```

## 5) 域名与路径约定（部署时）

- 前台：`https://blog.example.com/`
- 后台：`https://admin.blog.example.com/`
- API：建议不单独暴露域名，走 Nginx 反代：
  - `https://blog.example.com/api/*`
  - `https://admin.blog.example.com/api/*`

好处：
- 前端生产环境无需处理跨域
- 安全组无需对外开放后端端口
