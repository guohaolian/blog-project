# 本地 MySQL 数据库初始化与联调（blog-api）

目标：让 `blog-api` 在本地连接 MySQL（5.5 兼容）后可以正常启动，并且后续能直接开始写业务代码（MyBatis-Plus CRUD、登录、文章等）。

> 适用范围：Windows + PowerShell + MySQL 5.5/5.7/8.0（脚本按 5.5 语法写，8.0 也可执行）。

> 更完整的「从 0 初始化并验证」路线请看：`docs/db-init-and-verify.md`

---

## 1. 前置条件

- 已安装并启动 MySQL 服务
- 已知 root 密码，或准备创建专用账号

可选：安装一个客户端
- DataGrip / Navicat / MySQL Workbench
- 或者使用 MySQL 自带 `mysql` 命令行

---

## 2. 建库建表

仓库已提供初始化脚本：

- `deploy/sql/init.sql`

### 重要说明（避免踩坑）

- `init.sql` **会执行**：
  - `CREATE DATABASE IF NOT EXISTS blog_db ...`
  - `USE blog_db`
  - `DROP TABLE IF EXISTS ...`（重复执行会清空数据）
- 脚本包含 seed 数据：默认管理员 `admin/admin123`

### 2.1 方式 A：用客户端执行（推荐）

1) 连接本地 MySQL（host=localhost, port=3306）
2) 直接执行 `deploy/sql/init.sql` 的全部内容

### 2.2 方式 B：命令行执行

PowerShell 对原生程序的输入重定向 `<` 支持不一致，推荐在 PowerShell 里通过 `cmd /c` 执行：

```powershell
cmd /c "cd /d E:\blog-project && mysql -uroot -p<你的root密码> < deploy\sql\init.sql"
```

例如（仅示例）：

```powershell
cmd /c "cd /d E:\blog-project && mysql -uroot -pghl200224 < deploy\sql\init.sql"
```

---

## 3. 配置后端连接

后端默认启用 `dev` profile（见 `blog-api/src/main/resources/application.yml`）：

- `spring.profiles.active: dev`

在 `application-dev.yml` 中配置：

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`

当前默认值：

- DB: `blog_db`
- user: `root`
- password: `root`

如果你的密码不是 `root`，请修改 `application-dev.yml`。

---

## 4. 启动验证

### 4.1 IDEA 启动

推荐按 `docs/idea-run-config.md` 配置 Run Configuration。

### 4.2 验证健康接口

启动后访问：

- `http://localhost:8080/api/health`

期望返回：

```json
{"status":"ok"}
```

---

## 5. 常见问题

### Q1：启动报 `Access denied for user 'root'@'localhost'`

- 账号/密码不对
- 或 root 不允许本机 host 登录

解决：
- 修改 `application-dev.yml` 的用户名/密码
- 或新建专用账号（例子）：

```sql
CREATE USER 'blog'@'localhost' IDENTIFIED BY 'blog123';
GRANT ALL PRIVILEGES ON blog_db.* TO 'blog'@'localhost';
FLUSH PRIVILEGES;
```

也可以直接使用模板：`deploy/sql/create-user.local.sql.example`

### Q2：启动报 `Communications link failure`

- MySQL 没启动
- 端口不是 3306
- 防火墙拦截

检查：
- 确认服务启动
- 确认端口
- 修改 `spring.datasource.url`

### Q3：MySQL 8.0 报时区/SSL 警告

可以在 JDBC URL 追加参数（可选）：

- `serverTimezone=Asia/Shanghai`
- `useSSL=false`

例如：

```text
jdbc:mysql://localhost:3306/blog_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
```

---

## 6. 下一步建议（准备写业务）

- 增加最小的数据库连通性检查（一个 `SELECT 1` / 简单 Mapper）
- （可选）用 Flyway 管理后续表结构演进
