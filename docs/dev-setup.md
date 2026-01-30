# 本地开发环境搭建（Windows）

> 本文是“从 0 能跑起来”的最短路径。默认你使用 Windows + JetBrains 全家桶。

## 1) 必装软件清单

- Git（2.x）
- Node.js（建议 20 LTS）
- JDK 11（Temurin/Oracle 任意，团队统一）
- Maven（3.8+ / 3.9+）
- MySQL 5.5（按要求；本地如无法安装可先用 5.7 兼容开发，但上线前需回归验证）
- IDE：IntelliJ IDEA / WebStorm（或 IDEA + 前端插件）

推荐：
- DBeaver/Navicat
- Postman/Apifox

## 2) Node/npm 检查

打开 PowerShell：

```powershell
node -v
npm -v
```

建议：Node 20.x + npm 10.x。

## 3) Maven/JDK 检查

```powershell
java -version
mvn -v
```

确保 Java 为 11。

## 4) MySQL 初始化（建议约定）

- 创建数据库：`blog_db`
- 字符集：优先 `utf8`（MySQL 5.5 下更稳）
- 时区：建议统一为 `Asia/Shanghai`（或 UTC，但要全链路一致）

## 5) 本地运行约定（将来骨架生成后）

- 本地后端：默认 `http://localhost:8080`
- 前端开发服务器：
  - `blog-web`：`http://localhost:5173`
  - `blog-admin-web`：`http://localhost:5174`

开发期统一走 `/api` 代理：
- 前端请求：`/api/...`
- Vite 代理到后端：`http://localhost:8080`

## 6) 后端本地启动（重要）

### 6.1 local profile（不依赖数据库，用于“框架验证”）
当你只是想验证 Spring Boot 能启动、/api/health 可访问时，用 local profile（已排除 DataSource 自动配置）：

```powershell
cd .\blog-api
mvn -q -DskipTests package
java -jar .\target\blog-api-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

如果 8080 端口被占用，可以临时换端口：

```powershell
java -jar .\target\blog-api-0.0.1-SNAPSHOT.jar --spring.profiles.active=local --server.port=18080
```

健康检查（建议用 cmd 的 curl，避免 PowerShell 把 curl 映射成 Invoke-WebRequest）：

```powershell
cmd /c "curl -s -i http://127.0.0.1:18080/api/health"
```

### 6.2 dev profile（连接数据库，用于后续业务开发）

```powershell
java -jar .\target\blog-api-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

> 提示：MySQL 5.1 驱动使用 `com.mysql.jdbc.Driver`（已在 yml 中指定）。

## 7) Git 分支与提交（建议）

- `main`：稳定可发布
- `develop`：日常集成
- `feature/*`：功能开发

提交信息建议采用 Conventional Commits（可选）。
