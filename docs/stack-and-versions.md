# 技术栈与版本（最终基线）

本文档用于**锁定项目基线版本**（2026-01），方便团队一致性与后续升级。

> 原则：优先选择成熟稳定、生态完善、可在阿里云简单部署的版本。

## 1) 前端（`blog-web` / `blog-admin-web` 统一）

- Node.js：**20 LTS**（推荐 `20.x`，团队统一一个小版本即可）
- npm：随 Node 自带（建议 `10.x`）
- Vue：**3.4.x**
- Vite：**5.4.x**
- Vue Router：**4.3.x**
- Pinia：**2.1.x**
- Element Plus：**2.7.x**
- Axios：**1.6.x**

工程化推荐（可选，但建议启用）：
- TypeScript：**5.5.x**（推荐用 TS 提升长期维护性）
- ESLint：**9.x**
- Prettier：**3.x**

## 2) 后端（`blog-api`）

- JDK：**11 (LTS)**（建议 `11.0.x`）
- Spring Boot：**2.7.18**（与 JDK11 兼容、成熟）
- Spring Security：随 Spring Boot 2.7.18
- JWT：`jjwt 0.11.5`（或同级方案，后续在实现阶段落地）
- MyBatis-Plus：**3.5.5**
- MySQL：**5.5.x**（按需求）

依赖建议：
- MySQL JDBC Driver：建议选择兼容 MySQL 5.5 的版本（实现时再确认，避免不兼容）
- Lombok：可选（团队习惯决定）

## 3) 部署（简单优先）

- 阿里云 ECS（Linux）：Ubuntu 22.04 LTS（建议统一）
- Nginx：稳定版本（系统包管理安装即可）
- 运行方式：后端 Jar + systemd；前端 dist 静态文件

## 4) 重要兼容性说明（MySQL 5.5）

MySQL 5.5 较老，实践中建议注意：

- 字符集：若遇到 `utf8mb4` 支持问题，可先用 `utf8`，并规避 4 字节 emoji。
- 时间字段/时区：统一使用 `DATETIME` 并在应用层统一时区处理。
- 索引长度：长 varchar + utf8/utf8mb4 在老版本上可能触发索引长度限制。

> 如果将来允许升级数据库，优先考虑 MySQL 5.7/8.0 或阿里云 RDS。
