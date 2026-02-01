# scripts 说明

这些脚本用于**框架阶段**的一键启动/停止/自检（不涉及业务功能）。

## 1) 启动后端（local profile，不依赖数据库）

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

## 2) 启动前端

```powershell
.\scripts\start-frontend.ps1 -App web
.\scripts\start-frontend.ps1 -App admin
```

## 3) 一键启动/停止三端

```powershell
.\scripts\start-all.ps1 -ApiPort 18080
```

```powershell
.\scripts\stop-all.ps1 -ApiPort 18080
```

> 说明：
> - `start-all.ps1` 会在独立 PowerShell 窗口中分别启动两个前端 dev server。
> - `start-all.ps1` 会在 `scripts/.pids.json` 里记录启动的 PID。
> - `stop-all.ps1` 会优先读取 `scripts/.pids.json` 并按 PID 精确停止；若文件不存在才回退到按端口停止。

## 4) 导出 API 接口文档（OpenAPI / Swagger）

前置：确保你已经构建过后端可执行 jar（用于脚本临时启动导出）。

```powershell
cd .\blog-api
mvn -DskipTests package
```

### 4.1 导出 OpenAPI JSON/YAML（必选）

```powershell
.\scripts\export-openapi.ps1
```

输出目录：`docs/generated/`
- `openapi.json`
- `openapi.yaml`（如果 /v3/api-docs.yaml 可用）
- `openapi.web.json` / `openapi.admin.json`（如果 springdoc 分组导出可用）

> 说明：
> - 如果你的 `8080` 端口被占用，脚本会自动找一个空闲端口临时启动后端再导出。
> - 导出完成会自动关闭临时启动的后端进程。

### 4.2 生成 HTML（可选，更好看，适合发给别人看）

```powershell
.\scripts\export-openapi-html.ps1
```

输出：`docs/generated/`
- `api-docs.html`（全量文档）
- `api-docs.web.html`（前台 web 分组文档，若 openapi.web.json 存在）
- `api-docs.admin.html`（后台 admin 分组文档，若 openapi.admin.json 存在）

### 4.3 生成 Markdown（可选，更容易做 git diff）

```powershell
.\scripts\export-openapi-md.ps1
```

输出：`docs/generated/api-docs.md`

## 5) 常见问题

### PowerShell 的 curl 问题
PowerShell 里 `curl` 是 `Invoke-WebRequest` 的别名。建议使用：

```powershell
cmd /c "curl -s -i http://127.0.0.1:18080/api/health"
```

### 端口被占用
如果提示端口占用，可以用：

```powershell
.\scripts\stop-port.ps1 -Port 8080
.\scripts\stop-port.ps1 -Port 5173
.\scripts\stop-port.ps1 -Port 5174
```
