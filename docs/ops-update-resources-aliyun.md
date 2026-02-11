# 线上资源更新（阿里云 ECS）操作手册

适用项目：`blog-project`

本文用于把本地/测试环境产生的**上传资源（uploads）**、以及（可选）**前台/后台静态站点（dist）**更新到阿里云 ECS 上。

> 约定均与 `docs/deploy-ecs-nginx.md` 保持一致。

---

## 0. 部署架构与关键目录（与部署文档一致）

Nginx：
- `/` → 前台静态站点
- `/admin/` → 后台静态站点
- `/api/` → 反代后端 `http://127.0.0.1:8080`
- `/uploads/` → 映射到磁盘目录 `/opt/blog/uploads/`

服务器目录建议：
- 前台 dist：`/opt/blog/www/blog-web/`
- 后台 dist：`/opt/blog/www/blog-admin-web/`
- 后端 jar + 配置：`/opt/blog/blog-api/`
- 上传目录：`/opt/blog/uploads/`
- 备份目录：`/opt/blog/backup/`

---

## 1) 只更新 uploads（最常见：补图片/资源）

### 1.1 什么时候需要做

- 你本地有一批图片/资源文件，需要同步到线上 `uploads` 目录。
- 或者从别处迁移了资源（比如从旧服务器/本机复制）。

> 一般情况下：**只更新 uploads 不需要重启后端和 Nginx**（除非你改了 Nginx 配置或后端上传目录配置）。

### 1.2 Windows（PowerShell）用 scp 上传到服务器

把 `<ECS_IP>` 替换成你的公网 IP。

```powershell
# 上传整个 uploads（增量能力弱，但最简单）
scp -r E:\blog-project\blog-api\uploads\* root@<ECS_IP>:/opt/blog/uploads/
```

指定 SSH 端口（非 22 才需要）：

```powershell
scp -P <SSH_PORT> -r E:\blog-project\blog-api\uploads\* root@<ECS_IP>:/opt/blog/uploads/
```

### 1.3 推荐：先上传到 backup，再在服务器上移动（更安全）

适用于：文件很多、或者你不想在传到一半时就被线上访问到。

Windows 上传到临时目录：

```powershell
scp -r E:\blog-project\blog-api\uploads\* root@<ECS_IP>:/opt/blog/backup/uploads-staging/
```

在服务器上执行（SSH 登录后）：

```bash
sudo mkdir -p /opt/blog/uploads
sudo rsync -av --progress /opt/blog/backup/uploads-staging/ /opt/blog/uploads/
```

---

## 2) 上传后如何验证 uploads 可访问

在服务器本机：

```bash
# 任选一张你刚传的图片做验证
curl -I http://127.0.0.1/uploads/202602/xxx.jpg
```

在浏览器：

- `http://<ECS_IP>/uploads/202602/xxx.jpg` 能直接打开图片

如果 404：
1. 文件是否真的存在于 `/opt/blog/uploads/...`
2. Nginx 是否启用了 `location ^~ /uploads/ { alias /opt/blog/uploads/; }`
3. Nginx 是否正确 reload：`sudo nginx -t && sudo systemctl reload nginx`

---

## 3) 更新前台/后台静态站点（dist）

### 3.1 本地构建

前台：

```powershell
cd E:\blog-project\blog-web
npm install
npm run build
```

后台：

```powershell
cd E:\blog-project\blog-admin-web
npm install
npm run build
```

### 3.2 上传 dist（推荐：先压缩再传，速度快）

前台：

```powershell
cd E:\blog-project\blog-web
powershell -Command "Compress-Archive -Path dist\* -DestinationPath dist.zip -Force"
scp dist.zip root@<ECS_IP>:/opt/blog/backup/blog-web-dist.zip
```

后台：

```powershell
cd E:\blog-project\blog-admin-web
powershell -Command "Compress-Archive -Path dist\* -DestinationPath dist.zip -Force"
scp dist.zip root@<ECS_IP>:/opt/blog/backup/blog-admin-web-dist.zip
```

服务器上解压覆盖：

```bash
sudo apt install -y unzip
sudo mkdir -p /opt/blog/www/blog-web
sudo mkdir -p /opt/blog/www/blog-admin-web

sudo unzip -o /opt/blog/backup/blog-web-dist.zip -d /opt/blog/www/blog-web
sudo unzip -o /opt/blog/backup/blog-admin-web-dist.zip -d /opt/blog/www/blog-admin-web

sudo nginx -t
sudo systemctl reload nginx
```

---

## 4) 更新后端（仅当你改了 blog-api 代码/配置时）

### 4.1 本地打包 jar

```powershell
cd E:\blog-project\blog-api
mvn -DskipTests package
```

### 4.2 上传 jar 并重启服务

```powershell
scp E:\blog-project\blog-api\target\blog-api-0.0.1-SNAPSHOT-exec.jar root@<ECS_IP>:/opt/blog/backup/app.jar
```

服务器上替换并重启：

```bash
sudo cp /opt/blog/backup/app.jar /opt/blog/blog-api/app.jar
sudo systemctl restart blog-api
sudo systemctl status blog-api --no-pager

# 看日志（异常时）
sudo journalctl -u blog-api -n 200 --no-pager
sudo tail -n 200 /var/log/blog/blog-api.err.log
```

---

## 5) 常见问题排查

### 5.1 图片上传成功但前台看不到

- 先确认文章内容里图片 URL 形如：`/uploads/202602/xxx.jpg` 或 `https://域名/uploads/...`
- 再确认 Nginx `/uploads/` 映射目录是 `/opt/blog/uploads/`

### 5.2 /uploads 访问 404

按顺序查：
1. 文件是否存在：`ls -al /opt/blog/uploads/202602/xxx.jpg`
2. Nginx 配置是否加载：`sudo nginx -T | grep -n "\^~ /uploads" -n -B2 -A8`
3. Nginx 是否 reload：`sudo nginx -t && sudo systemctl reload nginx`

### 5.3 传完文件但浏览器还是旧内容

- 静态站点：可能是缓存。你已经在示例里对 `*.html` 做了 `no-store`，但 js/css 可长缓存。
  - 最简单验证：浏览器强制刷新或开无痕。

---

## 6) 建议的上线 SOP（可复用）

1. **备份**：
   - 静态站点：备份 `/opt/blog/www/blog-web`、`/opt/blog/www/blog-admin-web`
   - 后端 jar：备份 `/opt/blog/blog-api/app.jar`
2. **上传产物到 `/opt/blog/backup/`**
3. **服务器上解压/替换**
4. **reload nginx / restart blog-api（按需）**
5. **验收**：
   - `/api/health` 200
   - `/admin/` 能打开
   - `/uploads/...` 能打开

---

## 7) 你需要替换的变量

- `<ECS_IP>`：阿里云公网 IP
- `<SSH_PORT>`：SSH 端口（默认 22）
- 如果你不是 root 用户，把上传命令里的 `root@...` 改成你的用户名

