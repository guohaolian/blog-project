# IntelliJ IDEA 运行配置（blog-api）

目标：让你在 Windows + IntelliJ IDEA 下 **不依赖脚本**，也能稳定跑起后端（local/dev）、并能快速切换端口和 profile。

> 约定：后端项目目录为 `blog-api`，主类为 `com.example.blog.BlogApiApplication`。

---

## 1. 前置条件

- JDK：11
- Maven：3.8+（IDEA 自带 Maven 也可以）
- 已完成数据库初始化（如要跑 `dev`）：见 `docs/db-local-setup.md`

---

## 2. 打开项目

建议直接打开仓库根目录 `E:\blog-project`。

如果你只打开 `blog-api` 也可以，但后续脚本/文档路径会不一致。

---

## 3. 配置 Project SDK / Maven

1) `File -> Project Structure -> Project`
- Project SDK：选择 **JDK 11**

2) `Settings -> Build, Execution, Deployment -> Build Tools -> Maven`
- Maven home path：保持默认或选择你本机 Maven
- User settings file：默认即可

---

## 4. 运行方式 A：Spring Boot（推荐）

### 4.1 local profile（不依赖 MySQL，先验证框架）

1) 打开 `blog-api/src/main/java/com/example/blog/BlogApiApplication.java`
2) 右键 `Run 'BlogApiApplication'`，会自动生成一个运行配置
3) 打开 `Run/Debug Configurations`，找到该配置并修改：

- **Active profiles**：`local`
- **Environment variables（可选）**：无
- **Program arguments（可选）**：
  - `--server.port=18080`

启动后验证：
- `GET http://127.0.0.1:18080/api/health` 应返回 `{"status":"ok"}`

> 注意：PowerShell 的 `curl` 是别名，建议用：
> - `cmd /c "curl -s -i http://127.0.0.1:18080/api/health"`

### 4.2 dev profile（连接 MySQL，开始业务开发）

在上面配置基础上，把：
- Active profiles：改为 `dev`
- Program arguments：可以不写（默认端口 8080），或写 `--server.port=8080`

数据库连接参数来源：
- 默认：`blog-api/src/main/resources/application-dev.yml`
- 如果你本机 MySQL 用户密码不同：就修改该文件，或在 Run Configuration 里用环境变量覆盖（更推荐生产/团队协作）。

---

## 5. 运行方式 B：Maven（可选）

在 IDEA 右侧 Maven 面板找到 `blog-api -> Lifecycle -> spring-boot:run`。

你可以在运行参数里加：
- `-Dspring-boot.run.profiles=dev`
- `-Dspring-boot.run.arguments=--server.port=8080`

---

## 6. Debug（断点调试）

把 Run 改为 Debug 即可。

建议你在以下位置打断点：
- Controller：入参、返回
- Security：鉴权过滤器（后续加 JWT 时）
- Service：业务主逻辑

---

## 7. 常见问题

### Q1：端口被占用

方案：改端口。
- IDEA Program arguments：`--server.port=18080`
- 或者使用脚本：`scripts/start-backend-local.ps1 -Port 18080`

### Q2：启动报数据库连接错误

说明你当前跑的是 `dev` profile，但 MySQL 未启动/账号密码不对/库表未初始化。

解决顺序：
1. 先用 `local` profile 确认框架启动没问题
2. 再按 `docs/db-local-setup.md` 初始化数据库
3. 最后切回 `dev`
