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

## 4) 常见问题

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
