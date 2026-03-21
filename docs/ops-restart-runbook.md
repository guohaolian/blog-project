# 线上重启恢复 Runbook（断电/重启后快速验证与修复）

适用场景：服务器重启后（或你手动 stop/start 后），需要快速把 **MySQL + blog-api + Nginx + 前台/后台静态站点 + uploads** 全部恢复到可用状态。

> 目标：
> 1) 先用最少命令确认“哪里挂了”
> 2) 按依赖顺序恢复并验证
> 3) 遇到常见故障（端口占用、配置未生效、413、/uploads 404）能快速修复

---

## 0) 一分钟总览（推荐顺序）

依赖关系：

1. MySQL（数据库）
2. blog-api（后端依赖 DB、并提供 /api）
3. Nginx（反代 /api，暴露静态站点与 /uploads）
4. 前端静态站点（已构建的 dist 文件，只要磁盘在且 Nginx 配置正确即可）

---

## 1) 快速验证所有服务运行状态（第一步必做）

### 1.1 systemd 服务状态

```bash
sudo systemctl status mysql --no-pager || true
sudo systemctl status blog-api --no-pager || true
sudo systemctl status nginx --no-pager || true
```

期望：三者都处于 `active (running)`。

### 1.2 端口监听检查

```bash
sudo ss -lntp | grep -E ':80|:8080|:3306' || true
```

期望：
- MySQL: `:3306`
- 后端: `:8080`（本机监听即可，不建议对公网开放）
- Nginx: `:80`

### 1.3 健康检查（最关键的 3 个 URL）

在服务器本机验证：

```bash
# 后端直连
curl -i http://127.0.0.1:8080/api/health

# 走 nginx 反代（生产访问路径）
curl -i http://127.0.0.1/api/health

# uploads（随便换成一张真实存在的图片路径）
# curl -I http://127.0.0.1/uploads/202602/xxx.jpg
```

在浏览器验证：
- `http://<公网IP>/` 前台
- `http://<公网IP>/admin/` 后台
- `http://<公网IP>/api/health` 返回 200
- `http://<公网IP>/uploads/...` 能打开图片

---

## 2) 一键恢复（按依赖顺序启动）

### 2.1 启动数据库

```bash
sudo systemctl enable --now mysql
sudo systemctl status mysql --no-pager
```

如果 MySQL 启动失败：

```bash
sudo journalctl -u mysql -n 200 --no-pager
```

### 2.2 启动后端 blog-api

```bash
sudo systemctl enable --now blog-api
sudo systemctl status blog-api -l --no-pager
```

看后端日志：

```bash
sudo tail -n 200 /var/log/blog/blog-api.err.log
sudo tail -n 200 /var/log/blog/blog-api.out.log
```

验证后端：

```bash
curl -i http://127.0.0.1:8080/api/health
```

### 2.3 启动 Nginx

```bash
sudo systemctl enable --now nginx
sudo systemctl status nginx -l --no-pager
```

验证 Nginx 配置与 reload：

```bash
sudo nginx -t
sudo systemctl reload nginx
```

验证走 Nginx 的接口：

```bash
curl -i http://127.0.0.1/api/health
```

---

## 3) 常见故障快速修复

### 3.1 Nginx 启动失败（最常见：80 端口被 apache2 占用）

检查 80 端口：

```bash
sudo ss -lntp | grep :80 || true
```

如果看到 `apache2`：

```bash
sudo systemctl stop apache2
sudo systemctl disable apache2
sudo systemctl mask apache2

sudo systemctl restart nginx
sudo systemctl status nginx --no-pager
```

### 3.2 上传图片报 413（Request Entity Too Large）

原因：Nginx 默认限制请求体大小。

修复：在 `/etc/nginx/sites-available/blog.conf` 的 `server {}` 内添加（或确认存在）：

```nginx
client_max_body_size 20m;
```

然后：

```bash
sudo nginx -t
sudo systemctl reload nginx
```

> 说明：后端仍可能有自身限制（`spring.servlet.multipart.*` 和代码里的 10MB 限制）。

### 3.3 /uploads/** 404（上传成功、磁盘有文件，但预览 404）

先确认文件存在：

```bash
sudo ls -lh /opt/blog/uploads
sudo find /opt/blog/uploads -maxdepth 2 -type f | head -n 5
```

确认 Nginx 是否加载了 uploads 的 location：

```bash
sudo nginx -T 2>/dev/null | grep -n "location \^~ /uploads" -n -B2 -A8
```

推荐的稳定配置（公网 IP，无域名）：

```nginx
server {
    listen 80 default_server;
    server_name _;

    location ^~ /uploads/ {
        alias /opt/blog/uploads/;
        try_files $uri =404;
    }
}
```

> 常见坑：
> - 不要同时启用多段 server（例如示例里的 Option B 双域名），否则可能命中错误 server 块。
> - `alias` 目录必须以 `/` 结尾：`alias /opt/blog/uploads/;`

修改后：

```bash
sudo nginx -t
sudo systemctl reload nginx
```

然后验证：

```bash
# 把路径换成真实文件
curl -I http://127.0.0.1/uploads/202602/xxx.jpg
```

### 3.4 blog-api 启动失败/不断重启

查看状态与退出原因：

```bash
sudo systemctl status blog-api -l --no-pager
sudo tail -n 200 /var/log/blog/blog-api.err.log
sudo tail -n 200 /var/log/blog/blog-api.out.log
```

常见原因：
- jar 路径不对（`Unable to access jarfile`）
- 外置 `application-prod.yml` 写了 `spring.profiles.active`（Spring Boot 2.4+ 会直接报错）
- 数据库连接失败（账号/密码/地址）

外置配置建议：
- 文件：`/opt/blog/blog-api/application-prod.yml`
- 项目上传目录：`app.upload.dir: /opt/blog/uploads`

systemd 中建议参数（示例）：

```ini
ExecStart=/usr/bin/java -jar /opt/blog/blog-api/blog-api.jar \
  --spring.profiles.active=prod \
  --spring.config.additional-location=/opt/blog/blog-api/
```

改完 service 后：

```bash
sudo systemctl daemon-reload
sudo systemctl restart blog-api
```

---

## 4) 建议的最终验收清单（上线后每次重启都跑一遍）

```bash
# 1) 服务状态
sudo systemctl status mysql --no-pager
sudo systemctl status blog-api --no-pager
sudo systemctl status nginx --no-pager

# 2) 端口
sudo ss -lntp | grep -E ':80|:8080|:3306' || true

# 3) 健康

```

---

## 5) 域名访问验证与常见排错（`www.guohaolian.xyz`）

当服务器已绑定并解析域名后，建议把验收分成两层：

1) **服务器本机**验证（排除 DNS/证书/公网链路问题）
2) **域名外网**验证（确认 Nginx server_name/证书/跳转都正确）

### 5.1 服务器本机验证（不依赖 DNS）

```bash
# 后端直连：排除 nginx 干扰
curl -i http://127.0.0.1:8080/api/health

# 走 nginx（生产真实入口）
curl -i http://127.0.0.1/api/health

# 前台/后台的静态站点（只取响应头即可）
curl -I http://127.0.0.1/
curl -I http://127.0.0.1/admin/

# uploads（把路径换成真实存在的文件）
# curl -I http://127.0.0.1/uploads/202602/xxx.jpg
```

### 5.2 域名外网验证（从你的电脑/或服务器上带 Host）

在服务器上也可以直接用域名访问（前提是 DNS 已生效）：

```bash
curl -I http://www.guohaolian.xyz/
curl -I http://www.guohaolian.xyz/admin/
curl -i http://www.guohaolian.xyz/api/health
```

如果启用了 HTTPS，再补充：

```bash
curl -I https://www.guohaolian.xyz/
curl -I https://www.guohaolian.xyz/admin/
curl -i https://www.guohaolian.xyz/api/health
```

### 5.3 域名场景最常见的 4 类问题

#### (1) 命中错误的 Nginx server 块

现象：
- 首页能打开，但 `/uploads/**` 404 或 `/admin/` 刷新报 base URL；
- 或 `/api` 变成 404/502。

排查：

```bash
sudo nginx -T 2>/dev/null | grep -n "server_name" -n
sudo nginx -T 2>/dev/null | grep -n "server_name www.guohaolian.xyz" -n -B2 -A12
```

建议：同一个域名的 `/`、`/admin/`、`/api/`、`/uploads/` 尽量合并到一个 `server {}`。

#### (2) DNS 未生效 / 解析错误

```bash
nslookup www.guohaolian.xyz || true
```

期望返回你的 ECS 公网 IP。

#### (3) HTTPS 证书/续期问题

```bash
sudo certbot certificates || true
sudo certbot renew --dry-run || true
```

如果申请或续期失败，通常是 80/443 未放行，或 Nginx 配置/站点未正确启用。

#### (4) /uploads 404（磁盘有文件但访问不到）

```bash
sudo ls -lh /opt/blog/uploads | head
sudo nginx -T 2>/dev/null | grep -n "location \^~ /uploads/" -n -B2 -A8
```

关键点：
- 必须使用 `alias /opt/blog/uploads/;`（尾部 `/` 不能少）
- 确认你访问的 URL 是 `/uploads/...` 而不是 `/api/uploads/...`
curl -i http://127.0.0.1:8080/api/health
curl -i http://127.0.0.1/api/health

# 4) uploads（换成真实文件）
# curl -I http://127.0.0.1/uploads/202602/xxx.jpg
```

浏览器：
- `/`、`/admin/`、`/api/health`、`/uploads/...` 都正常。

---

## 5) 重要文件位置备忘（别重启后找不到）

- 后端 jar：`/opt/blog/blog-api/blog-api.jar`
- 后端外置配置：`/opt/blog/blog-api/application-prod.yml`
- 上传目录：`/opt/blog/uploads/`
- 后端日志：
  - `/var/log/blog/blog-api.out.log`
  - `/var/log/blog/blog-api.err.log`
- Nginx 站点配置：
  - `/etc/nginx/sites-available/blog.conf`
  - `/etc/nginx/sites-enabled/blog.conf`

---

## 6) 可选：把常用命令做成脚本（如果你想更省事）

你可以自己创建一个脚本（例如 `/opt/blog/bin/check.sh`）封装第 1 节与第 4 节命令。

如果你希望我直接在仓库里补一个脚本（针对 Ubuntu），告诉我你服务器的发行版和是否用 root 运行，我可以给出可直接复制的版本。
