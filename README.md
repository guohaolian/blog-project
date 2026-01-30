# Blog Project（前后端分离博客系统）

本仓库用于从零开发一套**简单可部署**的前后端分离博客系统，包含三个独立项目：

- `blog-web`：用户前台网页端（Vue3）
- `blog-admin-web`：后台管理系统网页端（Vue3）
- `blog-api`：Java 后端服务（Spring Boot）

> 约定：前端包管理统一使用 **npm**（不使用 pnpm）。

## 文档入口

- 文档索引：`docs/index.md`
- PRD（业务需求）：`docs/prd.md`
- 验收清单：`docs/acceptance-checklist.md`
- 前端开发文档：`docs/frontend-dev-guide.md`
- 后端开发文档：`docs/backend-dev-guide.md`
- 脚本说明：`scripts/README.md`

## 0) 代码框架本地启动（仅骨架验证）

### 后端（blog-api）

框架阶段推荐用 `local` profile 启动（不依赖 MySQL，可验证服务能跑起来）：

```powershell
cd .\blog-api
mvn -q -DskipTests package
java -jar .\target\blog-api-0.0.1-SNAPSHOT.jar --spring.profiles.active=local --server.port=18080
```

健康检查建议用 cmd 的 curl（PowerShell 里 curl 是 Invoke-WebRequest 的别名）：

```powershell
cmd /c "curl -s -i http://127.0.0.1:18080/api/health"
```

也可以用脚本：

```powershell
.\scripts\start-backend-local.ps1 -Port 18080
.\scripts\healthcheck.ps1 -ApiPort 18080
.\scripts\stop-port.ps1 -Port 18080
```

### 前端（blog-web / blog-admin-web）

```powershell
cd .\blog-web
npm install
npm run dev
```

```powershell
cd .\blog-admin-web
npm install
npm run dev
```

或用脚本：

```powershell
.\scripts\start-frontend.ps1 -App web
.\scripts\start-frontend.ps1 -App admin
```

一键启动/停止三端：

```powershell
.\scripts\start-all.ps1 -ApiPort 18080
.\scripts\stop-all.ps1 -ApiPort 18080
```

## 1. 技术栈（基线版本）
详见：`docs/stack-and-versions.md`

## 2. 目录结构与命名规范
详见：`docs/naming-and-structure.md`

## 3. 开发环境与本地启动
详见：`docs/dev-setup.md`

## 4. 接口规范（响应体/分页/鉴权）
详见：`docs/api-conventions.md`

## 5. 部署（阿里云 ECS 简单部署）
详见：`docs/deploy-ecs-nginx.md`

## 6. 推荐的开发顺序（里程碑）
详见：`docs/roadmap.md`
