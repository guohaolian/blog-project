# 阿里云 ECS 简单部署（Nginx + 前端静态站点 + 后端 Jar + MySQL）

目标：部署方式尽量简单、便于你一个人维护。

## 1) 资源与网络

- ECS（建议 Ubuntu 22.04 LTS）
- 安全组放行：
  - 22（SSH，仅你的 IP）
  - 80（HTTP）
  - 443（HTTPS）
  - 3306（不建议对公网开放；同机部署无需开放）

## 2) 目录规划（建议）

- `/opt/blog/www/blog-web/`：前台 dist
- `/opt/blog/www/blog-admin-web/`：后台 dist
- `/opt/blog/blog-api/`：后端 jar + 配置
- `/opt/blog/uploads/`：上传文件
- `/opt/blog/backup/`：备份目录
- `/var/log/blog/`：后端日志

## 3) 后端部署（Jar + systemd）

思路：后端作为系统服务运行，开机自启、异常自动拉起。

关键点：
- 生产配置外置（例如 `application-prod.yml`）
- JWT 密钥、数据库密码不要写死代码仓库

（模板文件建议放在 `deploy/systemd/`，后续生成骨架时一起提供）

## 4) 前端部署（dist + Nginx）

- 本地构建得到 `dist/`
- 上传到 ECS 指定目录

Nginx 要点：
- `root` 指向 dist
- SPA 路由：`try_files $uri $uri/ /index.html;`
- `/api/` 反代到后端：`http://127.0.0.1:8080/`

## 5) HTTPS

- 优先 Let’s Encrypt（自动续期），或使用阿里云证书
- 80 强制跳转 443

## 6) 数据库初始化与备份

- 创建 `blog_db`
- 执行初始化 SQL（建议放 `deploy/sql/`）
- 定时备份：`mysqldump` 每天一次，保留 7～30 份

## 7) 回滚策略（简单但有效）

- 前端：保留最近 2~3 个 `dist` 目录（带时间戳）
- 后端：保留最近 2~3 个 jar（带时间戳）
- 配置：Nginx conf、prod yml 做单独备份
