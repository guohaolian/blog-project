# Blog Project（前后端分离博客系统）

一套**简单、可部署**的前后端分离博客系统仓库，包含三个独立项目：

- `blog-web`：用户前台网页端（Vue3）
- `blog-admin-web`：后台管理系统网页端（Vue3）
- `blog-api`：Java 后端服务（Spring Boot）

> 约定：前端包管理统一使用 **npm**（不使用 pnpm）。

---

## 0. 环境依赖（先装这些）

> 你可以用 Windows / macOS / Linux 开发；本仓库内置的脚本主要针对 **Windows PowerShell**。

### 必需

- **JDK 11**（建议 11.0.x），并确保 `java -version` 可用
- **Maven 3.6+**（建议 3.8+），并确保 `mvn -v` 可用
- **Node.js 18 LTS**（或 16+），并确保 `node -v` / `npm -v` 可用

### 可选（强烈建议）

- **MySQL 5.5+**（本地开发建议 5.7/8.0 也可用；连接参数按 yml 配置即可）
- **IDEA**（后端开发） / **VS Code 或 WebStorm**（前端开发）

---

## 1. 你能做什么

- 运行前台站点浏览文章（首页/分类/标签/归档/详情等）
- 运行后台管理系统（登录/文章/分类/标签/评论/资源/站点设置等）
- 运行 Java 后端 API（JWT 鉴权、MyBatis-Plus、文件上传 /uploads 等）
- 一键导出 OpenAPI 接口文档（JSON / YAML / HTML / Markdown）

---

## 2. 目录结构

```
blog-project/
  blog-web/         # 用户前台（Vue3 + Vite）
  blog-admin-web/   # 管理端（Vue3 + Vite）
  blog-api/         # 后端（Spring Boot）
  docs/             # 项目文档
  scripts/          # 本地开发脚本（PowerShell）
```

---

## 3. 默认端口与地址

> 下面是“默认约定”。如果端口冲突，可以在启动参数里改（例如 `--server.port=18080`）。

| 模块 | 默认端口 | 默认地址 |
|---|---:|---|
| blog-api（后端） | 8080 | http://127.0.0.1:8080 |
| blog-api（本地无 DB：local profile 示例） | 18080 | http://127.0.0.1:18080 |
| blog-web（前台） | 5173 | http://localhost:5173 |
| blog-admin-web（后台） | 5174 | http://localhost:5174 |
| 后端健康检查 | - | `GET /api/health` |
| OpenAPI JSON | - | `/v3/api-docs`（默认） |
| Swagger UI（开发环境通常开启） | - | `/swagger-ui.html` |

---

## 4. 文档入口

- 文档索引：`docs/index.md`
- PRD（业务需求）：`docs/prd.md`
- 验收清单：`docs/acceptance-checklist.md`
- 前端开发文档：`docs/frontend-dev-guide.md`
- 后端开发文档：`docs/backend-dev-guide.md`
- 脚本说明：`scripts/README.md`

---

## 5. 快速开始（推荐：使用 scripts）

> 所有命令默认在项目根目录执行（`E:\blog-project`）。

### 5.1 一键启动（后端使用MySQL + 前台 + 管理端）

适合：你刚拉代码，想先确认“前后端都能跑起来”（**不依赖 MySQL**）。

```powershell
.\scripts\start-all.ps1 -ApiPort 8080
```

停止：

```powershell
.\scripts\stop-all.ps1 -ApiPort 8080
```

后端探活：

```powershell
.\scripts\healthcheck.ps1 -ApiPort 8080
```

### 5.2 启动后端（连接 MySQL：dev/prod profile）

适合：你要联调完整业务。

```powershell
.\scripts\start-backend-mysql.ps1 -Profile dev -Port 8080
```

如果你不想改 yml，也可以临时覆盖数据库连接（可选）：

```powershell
.\scripts\start-backend-mysql.ps1 -Profile dev -Port 8080 `
  -DbHost 127.0.0.1 -DbPort 3306 -DbName blog_db `
  -DbUser root -DbPassword 你的密码
```

停止：

```powershell
.\scripts\stop-port.ps1 -Port 8080
```

---

## 6. 手动启动（不使用脚本）

### 6.1 后端（blog-api）

#### A) local profile（不连数据库）

```powershell
cd .\blog-api
mvn -q -DskipTests package
java -jar .\target\blog-api-0.0.1-SNAPSHOT-exec.jar --spring.profiles.active=local --server.port=18080
```

探活：

```powershell
cmd /c "curl -s -i http://127.0.0.1:18080/api/health"
```

#### B) dev profile（连接 MySQL）

先确保 `blog-api/src/main/resources/application-dev.yml` 里的数据库账号密码可用，然后启动：

```powershell
cd .\blog-api
mvn -q -DskipTests package
java -jar .\target\blog-api-0.0.1-SNAPSHOT-exec.jar --spring.profiles.active=dev --server.port=8080
```

### 6.2 前端（blog-web / blog-admin-web）

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

---

## 7. 导出接口文档（OpenAPI）

导出 JSON/YAML：

```powershell
.\scripts\export-openapi.ps1
```

生成更好看的 HTML：

```powershell
.\scripts\export-openapi-html.ps1
```

生成 Markdown（便于 diff）：

```powershell
.\scripts\export-openapi-md.ps1
```

输出目录：`docs/generated/`

---

## 8. 部署说明

- 阿里云 ECS + Nginx + systemd（简单部署）：`docs/deploy-ecs-nginx.md`

> 提醒：前端路由使用 History 模式时，需要 Nginx 做 fallback 到 `index.html`。
> 本项目的 Nginx 示例配置已在部署文档中说明。

---

## 9. 常见问题

### 9.1 PowerShell 的 curl

PowerShell 里 `curl` 是 `Invoke-WebRequest` 的别名。建议在 PowerShell 中用：

```powershell
cmd /c "curl -s -i http://127.0.0.1:8080/api/health"
```

### 9.2 为什么 target 下有两个 jar（普通 jar + -exec.jar）

这是因为 `blog-api/pom.xml` 的 `spring-boot-maven-plugin` 配置了 `classifier=exec`。

- `blog-api-0.0.1-SNAPSHOT-exec.jar`：Spring Boot **repackage 后的可执行 jar**（推荐用它 `java -jar` 启动）
- `blog-api-0.0.1-SNAPSHOT.jar`：原始 jar（更偏“库产物/非 boot 运行包”的用途）

> 简单理解：**跑服务用 `-exec.jar`**。

---

## 10. 下一步建议

- 先跑 `start-all.ps1` 确认三端正常
- 再按 `docs/dev-setup.md` 配好 MySQL 并用 `start-backend-mysql.ps1` 联调
- 用 `scripts/export-openapi-*.ps1` 导出接口文档，方便对照前端对接
