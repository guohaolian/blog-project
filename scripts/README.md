# scripts 说明

这些脚本用于**本地开发 / 联调 / 导出接口文档**。

> 约定：从项目根目录 `E:\blog-project` 执行脚本，例如：`./scripts/start-all.ps1`

---

## 1) 启动后端（不依赖数据库：local profile）

适合：只想快速验证后端能跑起来、不连 MySQL。

```powershell
.\scripts\start-backend-local.ps1 -Port 18080
```

停止：

```powershell
.\scripts\stop-port.ps1 -Port 18080
```

健康检查：

```powershell
.\scripts\healthcheck.ps1 -ApiPort 18080
```

---

## 2) 启动后端（连接 MySQL：dev/prod profile）

适合：需要完整业务联调（MySQL + MyBatis-Plus）。

### 2.1 使用 yml 中已有的数据库配置（最常用）

> 说明：
> - `--spring.profiles.active=dev` 会读取 `blog-api/src/main/resources/application-dev.yml`
> - `--spring.profiles.active=prod` 会读取 `.../application-prod.yml`

```powershell
.\scripts\start-backend-mysql.ps1 -Profile dev -Port 8080
```

### 2.2 临时覆盖数据库连接（不改 yml，可选）

当你要切换到另一台 MySQL（例如服务器）时很方便。

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

## 3) 启动前端（Vite dev server）

```powershell
.\scripts\start-frontend.ps1 -App web
.\scripts\start-frontend.ps1 -App admin
```

> 说明：该脚本会执行 `npm install`（如已安装会很快），然后 `npm run dev`。

---

## 4) 一键启动/停止三端

```powershell
.\scripts\start-all.ps1 -ApiPort 18080
```

```powershell
.\scripts\stop-all.ps1 -ApiPort 18080
```

> 说明：
> - `start-all.ps1` 会启动后端（local profile）+ 两个前端 dev server（会开两个独立 PowerShell 窗口）。
> - `start-all.ps1` 会在 `scripts/.pids.json` 里记录 PID。
> - `stop-all.ps1` 优先按 `scripts/.pids.json` 精确停止；文件不存在才回退成按端口停止。

---

## 5) 导出 API 接口文档（OpenAPI / Swagger）

输出目录：`docs/generated/`

前置：确保你能启动后端，或已构建出可执行 jar（脚本也会尝试临时起一个服务）。

### 5.1 导出 OpenAPI JSON/YAML（必选）

```powershell
.\scripts\export-openapi.ps1
```

输出：
- `openapi.json`（必有）
- `openapi.yaml`（若 `/v3/api-docs.yaml` 可访问则生成）
- `openapi.web.json` / `openapi.admin.json`（若 springdoc 分组可用则生成）

> 说明：你当前 `blog-api/src/main/resources/application.yml` 已配置 springdoc 分组：
> - `web`：匹配 `/api/**` 且排除 `/api/admin/**`
> - `admin`：匹配 `/api/admin/**`

### 5.2 生成 HTML（可选，更好看）

```powershell
.\scripts\export-openapi-html.ps1
```

默认会优先生成分组版：
- `api-docs.web.html`
- `api-docs.admin.html`

若找不到分组 spec，则会回退生成：
- `api-docs.html`

### 5.3 生成 Markdown（可选，适合 git diff）

```powershell
.\scripts\export-openapi-md.ps1
```

输出：`docs/generated/api-docs.md`

---

## 6) 常见问题

### 6.1 PowerShell 里的 curl

PowerShell 里 `curl` 是 `Invoke-WebRequest` 的别名。为了避免参数行为差异，`healthcheck.ps1` 用的是：

```powershell
cmd /c "curl -s -i http://127.0.0.1:8080/api/health"
```

### 6.2 端口被占用

```powershell
.\scripts\stop-port.ps1 -Port 8080
.\scripts\stop-port.ps1 -Port 5173
.\scripts\stop-port.ps1 -Port 5174
```
