# 数据库初始化与验证（从 0 到 blog-api 连通）

目标：把 MySQL 准备好、执行初始化脚本、验证库表与 seed 数据，然后用 `dev` profile 启动 `blog-api`。

适用：Windows + PowerShell + MySQL 5.5/5.7/8.0（脚本按 5.5 兼容写法）。

---

## 0. 关键事实（避免踩坑）

- 初始化脚本：`deploy/sql/init.sql`
- 该脚本 **会 DROP TABLE IF EXISTS**（重复执行会清空数据）。
- 默认数据库名：`blog_db`
- 默认管理员：
  - username：`admin`
  - password：`admin123`

---

## 1. 确认 MySQL 已启动

在 PowerShell 执行：

```powershell
netstat -ano | findstr :3306
```

若无输出：说明 MySQL 可能未启动，或端口不是 3306。

---

## 2. 执行 init.sql

### 2.1 方式 A：命令行导入（推荐最快）

要求：`mysql` 客户端已加入 PATH。

```powershell
mysql -uroot -p < "E:\blog-project\deploy\sql\init.sql"
```

> 这个脚本自己会 `CREATE DATABASE` + `USE blog_db`。

### 2.2 方式 B：图形化工具导入

用 DataGrip/DBeaver/Navicat 连接 MySQL 后，直接打开并运行 `deploy/sql/init.sql`。

---

## 3. 验证表结构与 seed 数据

```powershell
mysql -uroot -p -e "USE blog_db; SHOW TABLES;"
mysql -uroot -p -e "USE blog_db; SELECT id,username,status FROM admin_user;"
mysql -uroot -p -e "USE blog_db; SELECT id,site_name FROM site_setting;"
```

期望结果：
- `SHOW TABLES` 至少包含：`admin_user, post, category, tag, comment, site_setting, file_resource` 等
- `admin_user` 至少有 `admin` 一条

---

## 4. 配置后端连接数据库（dev profile）

默认位置：`blog-api/src/main/resources/application-dev.yml`

默认值：
- url：`jdbc:mysql://localhost:3306/blog_db?useUnicode=true&characterEncoding=utf8&useSSL=false`
- username：`root`
- password：`root`

如果你本机 root 密码不是 `root`：
- 修改 `application-dev.yml` 中的 username/password

> 可选：MySQL 8.0 可能需要在 url 加 `serverTimezone=Asia/Shanghai`。

---

## 5. 启动后端并验证

先用 local profile 验证框架（不连 DB）：

```powershell
cd E:\blog-project\blog-api
mvn -q -DskipTests package
java -jar .\target\blog-api-0.0.1-SNAPSHOT.jar --spring.profiles.active=local --server.port=18080
```

再切回 dev profile：

```powershell
java -jar .\target\blog-api-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

验证 health：

```powershell
cmd /c "curl -s -i http://127.0.0.1:8080/api/health"
```

---

## 6. 常见问题

### 6.1 `Access denied for user ...`

- 账号密码不对：检查 `application-dev.yml`
- root 不允许本地登录：建议创建专用账号（见 `docs/db-local-setup.md` 的示例 SQL）

### 6.2 `Communications link failure`

- MySQL 未启动
- 端口不是 3306
- 防火墙/安全软件阻断

### 6.3 重复执行 init.sql 后数据没了

正常现象：脚本里有 `DROP TABLE IF EXISTS`。

开发阶段建议：
- 你要保留数据时，不要重复跑 init；
- 或者未来引入 Flyway 做增量迁移（可选）。
