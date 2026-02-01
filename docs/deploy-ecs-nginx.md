# 阿里云 ECS 简单部署（Ubuntu + Nginx + 前端静态站点 + 后端 Jar + MySQL）

目标：部署方式尽量简单、便于你一个人维护。

本文以 **Ubuntu 22.04 LTS** 为例（其他 Ubuntu 版本流程类似）。

> 本仓库结构：
> - `blog-web/`：用户前台（Vite + Vue3）
> - `blog-admin-web/`：后台管理（Vite + Vue3）
> - `blog-api/`：后端（Spring Boot）
> - 上传目录建议使用服务器本地磁盘 ` /opt/blog/uploads/ `，由 Nginx 暴露 `/uploads/**`。

---

## 0) 部署目标架构（最终效果）

- Nginx 监听 `80`（可选 `443`）
  - `/` → 前台 `blog-web`（静态站点）
  - `/admin/` → 后台 `blog-admin-web`（静态站点）
  - `/api/` → 反代到后端 `http://127.0.0.1:8080`（后端接口以 `/api/...` 开头）
  - `/uploads/` → 映射到磁盘目录 `/opt/blog/uploads/`
- 后端：`java -jar ...`（建议 systemd 守护）监听 `8080`
- MySQL 5.7：监听 `3306`（建议仅本机访问，不对公网开放）

---

## 1) 资源与网络

### 1.1 ECS 选型建议

- 系统：Ubuntu 22.04 LTS
- 配置：1C2G 起步（个人项目够用），磁盘 40G+（视图片资源量调整）

### 1.2 安全组放行（入方向）

- 22（SSH）：建议仅放行你的公网 IP
- 80（HTTP）：必须
- 443（HTTPS）：如果你要 https
- 8080：**不建议对公网开放**（由 Nginx 反代即可）
- 3306：**不建议对公网开放**（同机部署无需开放）

---

## 2) 你需要准备的发布产物（本机生成）

### 2.1 后端 jar

在本机 `blog-api/` 打包：

```bash
mvn -DskipTests package
```

> 通常会有两个 jar，部署时优先使用 `*-exec.jar`（可执行 fat jar）。

### 2.2 前台/后台 dist

在本机分别构建：

```bash
cd blog-web
npm install
npm run build

cd ../blog-admin-web
npm install
npm run build
```

产物：
- `blog-web/dist/`
- `blog-admin-web/dist/`

---

## 3) Windows → Ubuntu 上传文件（三选一）

你用 Windows 开发，部署到 Ubuntu，常用三种方式：

### 3.1 方式 A：WinSCP / FinalShell（最省心）

- 连接方式：SFTP
- 主机：你的 ECS 公网 IP
- 端口：22
- 用户：`root`
- 把下列文件/目录上传到服务器 ` /opt/blog/backup/ `：
  - `blog-web/dist/`（整个目录内容）
  - `blog-admin-web/dist/`（整个目录内容）
  - `blog-api/target/*-exec.jar`
  - `deploy/sql/init.sql`
  - （如果你要直接复制 Nginx 配置）`deploy/nginx/blog.conf.example`

### 3.2 方式 B：scp（命令行）

PowerShell 示例（把 `YOUR_ECS_IP` 换成你的 ECS 公网 IP 即可）：

```powershell
# 上传后端 jar
scp E:\blog-project\blog-api\target\blog-api-0.0.1-SNAPSHOT-exec.jar root@YOUR_ECS_IP:/opt/blog/backup/app.jar

# 上传 SQL
scp E:\blog-project\deploy\sql\init.sql root@YOUR_ECS_IP:/opt/blog/backup/init.sql
```

dist 目录建议先压缩再传：

```powershell
# 打包前台 dist
cd E:\blog-project\blog-web
powershell -Command "Compress-Archive -Path dist\* -DestinationPath dist.zip -Force"
scp dist.zip root@YOUR_ECS_IP:/opt/blog/backup/blog-web-dist.zip

# 打包后台 dist
cd E:\blog-project\blog-admin-web
powershell -Command "Compress-Archive -Path dist\* -DestinationPath dist.zip -Force"
scp dist.zip root@YOUR_ECS_IP:/opt/blog/backup/blog-admin-web-dist.zip
```

服务器上解压：

```bash
sudo apt install -y unzip
unzip -o /opt/blog/backup/blog-web-dist.zip -d /opt/blog/www/blog-web
unzip -o /opt/blog/backup/blog-admin-web-dist.zip -d /opt/blog/www/blog-admin-web
```

### 3.3 方式 C：Git 拉取（适合你熟悉 git/服务器可访问仓库）

只建议用于拉代码，不建议在服务器上跑 npm build（会占资源、也不稳定）。

---

## 4) 目录规划（建议）

在服务器上统一放到 `/opt/blog`：

- `/opt/blog/www/blog-web/`：前台 dist
- `/opt/blog/www/blog-admin-web/`：后台 dist
- `/opt/blog/blog-api/`：后端 jar + 配置
- `/opt/blog/uploads/`：上传文件
- `/opt/blog/backup/`：备份目录
- `/var/log/blog/`：后端日志

创建目录：

```bash
sudo mkdir -p /opt/blog/www/blog-web
sudo mkdir -p /opt/blog/www/blog-admin-web
sudo mkdir -p /opt/blog/blog-api
sudo mkdir -p /opt/blog/uploads
sudo mkdir -p /opt/blog/backup
sudo mkdir -p /var/log/blog

# 让当前登录用户也能写（按你的实际用户名调整）
sudo chown -R $USER:$USER /opt/blog
sudo chown -R $USER:$USER /var/log/blog
```

---

## 5) 服务器初始化与软件安装

### 5.1 基础更新

```bash
sudo apt update
sudo apt -y upgrade
```

### 5.2 安装 Nginx

```bash
sudo apt install -y nginx
sudo systemctl enable nginx
sudo systemctl start nginx
sudo nginx -v
```

### 5.3 安装 Java 11（运行后端）

```bash
sudo apt install -y openjdk-11-jre
java -version
```

> 说明：你的后端技术栈要求 JDK 11。生产运行只需要 JRE 也可以。

---

## 6) MySQL 5.7（推荐使用服务器已安装的 MySQL）

Ubuntu 服务器上如果你已经装好了 **MySQL 5.7**，建议直接用它（最简单）。

> 说明：当前后端使用 `mysql-connector-java 5.1.49`，驱动类为 `com.mysql.jdbc.Driver`，与 MySQL 5.7 兼容。

### 6.1 检查是否已安装 MySQL 5.7

```bash
mysql --version
```

如果已安装，输出类似：

```
mysql  Ver 14.14 Distrib 5.7.xx, for Linux (x86_64) using  EditLine wrapper
```

### 6.2 启动 MySQL 服务

```bash
sudo systemctl start mysql
sudo systemctl enable mysql
```

### 6.3 登录 MySQL

```bash
mysql -u root -p
```

输入密码后，进入 MySQL 命令行。

### 6.4 创建数据库和用户

```sql
CREATE DATABASE blog_db;
CREATE USER 'blog_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON blog_db.* TO 'blog_user'@'localhost';
FLUSH PRIVILEGES;
```

> 请务必替换 `your_password` 为强密码。

### 6.5 导入初始数据

退出 MySQL 命令行：

```sql
exit
```

将 `init.sql` 上传到服务器后，执行：

```bash
mysql -u root -p blog_db < /opt/blog/backup/init.sql
```

### 6.6 连通性验证

```bash
mysql -h 127.0.0.1 -P 3306 -u blog_user -p blog_db
```

输入密码后，执行一条简单 SQL 确认：

```sql
SELECT 1;
```

### 6.7（可选）如果你仍想使用 MySQL 5.5

如果你强制要与需求文档的 MySQL 5.5 对齐，推荐用 Docker 运行 `mysql:5.5`（不污染系统、版本可控）。

> 这部分不是必须；当你选方案 1（使用现有 MySQL 5.7）时可以跳过。

#### 6.7.1 安装 Docker（如未安装）

```bash
sudo apt update
sudo apt install -y docker.io
sudo systemctl enable docker
sudo systemctl start docker
```

#### 6.7.2 启动 MySQL 5.5 容器（仅本机访问）

```bash
docker run -d --name mysql55 \
  -p 127.0.0.1:3306:3306 \
  -e MYSQL_ROOT_PASSWORD=ghl200224 \
  -e MYSQL_DATABASE=blog_db \
  --restart unless-stopped \
  mysql:5.5
```

#### 6.7.3 导入初始化 SQL

```bash
docker cp /opt/blog/backup/init.sql mysql55:/init.sql

docker exec -i mysql55 sh -c 'mysql -uroot -p"ghl200224" blog_db < /init.sql'
```

#### 6.7.4 备份（Docker 方式）

```bash
DATE=$(date +%Y%m%d)
BACKUP_DIR=/opt/blog/backup
mkdir -p $BACKUP_DIR

docker exec mysql55 sh -c 'mysqldump -uroot -p"ghl200224" blog_db' > $BACKUP_DIR/blog_db_$DATE.sql
```

---

## 7) 后端部署（Jar + application-prod.yml + systemd）

目标：后端作为系统服务运行，开机自启，异常自动重启。

### 7.1 放置后端 jar

把你的 `*-exec.jar` 上传后，放到：

- `/opt/blog/blog-api/app.jar`

示例（在服务器上执行）：

```bash
cp /opt/blog/backup/app.jar /opt/blog/blog-api/app.jar
```

### 7.2 生产配置 application-prod.yml（必须确认）

你仓库里已有：`blog-api/src/main/resources/application-prod.yml`。

生产环境建议将配置外置到服务器：
- `/opt/blog/blog-api/application-prod.yml`

需要你在服务器上确认/修改的关键项：

- 数据库密码：
  - `spring.datasource.password: ghl200224`
- JWT 密钥：
  - `app.jwt.secret: <强随机字符串>`（至少 32 位，越长越好）
- 上传目录：
  - `app.upload.dir: /opt/blog/uploads`
- 上传大小：
  - `spring.servlet.multipart.max-file-size: 10MB`
- **不使用 context-path**：
  - `server.servlet.context-path` 不要配置（你已经确认了）

> 强烈建议：把 JWT secret 换掉之后再上线，否则 token 安全性不够。

### 7.3 创建 systemd 服务

创建 `/etc/systemd/system/blog-api.service`：

```ini
[Unit]
Description=blog-api (Spring Boot)
After=network.target

[Service]
Type=simple
WorkingDirectory=/opt/blog/blog-api

Environment="JAVA_OPTS=-Xms128m -Xmx512m"

ExecStart=/usr/bin/java $JAVA_OPTS -jar /opt/blog/blog-api/app.jar \
  --spring.profiles.active=prod \
  --spring.config.additional-location=/opt/blog/blog-api/

Restart=always
RestartSec=3

StandardOutput=append:/var/log/blog/blog-api.log
StandardError=append:/var/log/blog/blog-api.err.log

User=root

[Install]
WantedBy=multi-user.target
```

并启动：

```bash
sudo systemctl daemon-reload
sudo systemctl enable blog-api
sudo systemctl start blog-api
sudo systemctl status blog-api --no-pager
```

### 7.4 验证后端

```bash
curl -i http://127.0.0.1:8080/api/health
```

---

## 8) 前端部署（dist）

把 dist 上传到：
- `/opt/blog/www/blog-web/`
- `/opt/blog/www/blog-admin-web/`

建议发布前做备份：

```bash
TS=$(date +%Y%m%d%H%M%S)
cp -r /opt/blog/www/blog-web /opt/blog/backup/blog-web-$TS || true
cp -r /opt/blog/www/blog-admin-web /opt/blog/backup/blog-admin-web-$TS || true
```

---

## 9) Nginx 配置（单域名 /admin 模式，直接可用）

把以下内容保存为：`/etc/nginx/sites-available/blog.conf`。

> 你目前没有域名：保持 `server_name _;` 即可（接收任何 Host），直接用“公网 IP”访问。
> 如果后续你买了域名，再把 `server_name` 改成你的域名即可。

```nginx
server {
    listen 80;
    server_name _;

    root /opt/blog/www/blog-web;
    index index.html;

    # proxy headers
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;

    # /api -> backend (keep /api prefix)
    location /api/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_http_version 1.1;
        proxy_connect_timeout 5s;
        proxy_read_timeout 60s;
    }

    # /uploads -> disk
    location /uploads/ {
        alias /opt/blog/uploads/;
        autoindex off;
        access_log off;
        try_files $uri =404;

        # 如果你会"删除图片"或"同名覆盖"，建议不要 immutable。
        add_header Cache-Control "public, max-age=3600";
    }

    # admin SPA under /admin/
    location ^~ /admin/ {
        alias /opt/blog/www/blog-admin-web/;
        try_files $uri $uri/ /admin/index.html;

        location ~* \.html$ {
            add_header Cache-Control "no-store";
        }

        location ~* \.(js|css|png|jpg|jpeg|gif|svg|ico|webp|woff2?)$ {
            add_header Cache-Control "public, max-age=31536000, immutable";
            access_log off;
        }
    }

    # blog-web SPA
    location / {
        try_files $uri $uri/ /index.html;
    }

    location ~* \.html$ {
        add_header Cache-Control "no-store";
    }

    location ~* \.(js|css|png|jpg|jpeg|gif|svg|ico|webp|woff2?)$ {
        add_header Cache-Control "public, max-age=31536000, immutable";
        access_log off;
    }

    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript;
    gzip_min_length 1024;
}
```

启用配置并重载：

```bash
sudo ln -sf /etc/nginx/sites-available/blog.conf /etc/nginx/sites-enabled/blog.conf
sudo rm -f /etc/nginx/sites-enabled/default

sudo nginx -t
sudo systemctl reload nginx
```

---

## 10) HTTPS（可选，但推荐）

你目前没有域名（仅公网 IP）：一般 **不建议** 折腾 HTTPS，也 **无法使用 Let’s Encrypt** 这类免费证书的常规申请流程（需要域名校验）。

- 如果你只是想先跑通功能：继续使用 HTTP 即可。
- 如果你后续购买了域名：可以再开启 HTTPS（推荐）。

### 10.1 后续有域名后（推荐做法）

1) 安全组放行 `443`
2) 把 Nginx 的 `server_name` 改为你的域名
3) 安装并申请证书（Let’s Encrypt + certbot）：

```bash
sudo apt update
sudo apt install -y certbot python3-certbot-nginx

# 运行后按提示输入域名并完成验证
sudo certbot --nginx
```

4) 验证自动续期：

```bash
sudo certbot renew --dry-run
```

> 自签证书仅适合测试（浏览器会提示不安全），不建议用于对外访问。

---

## 11) 验收清单（上线前自测）

在服务器本机验证：

```bash
# 后端
curl -i http://127.0.0.1:8080/api/health

# nginx 端口
sudo ss -lntp | grep -E ":80|:8080|:3306" || true
```

在浏览器验证：

- `http://你的公网 IP/` 能打开前台
- `http://你的公网 IP/admin/` 能打开后台
- `http://你的公网 IP/api/health` 返回 200
- 上传图片后：`http://你的公网 IP/uploads/...` 可直接访问

---

## 12) 数据库备份（简单方案）

每天备份一次，保留 7 份：

```bash
DATE=$(date +%Y%m%d)
BACKUP_DIR=/opt/blog/backup

mkdir -p $BACKUP_DIR

mysqldump -uroot -p"ghl200224" blog_db > $BACKUP_DIR/blog_db_$DATE.sql

ls -1t $BACKUP_DIR/blog_db_*.sql | tail -n +8 | xargs -r rm -f
```

---

## 13) 回滚策略（简单但有效）

### 13.1 前端回滚

- 每次发布前备份 `/opt/blog/www/blog-web` 与 `/opt/blog/www/blog-admin-web`
- 出问题后恢复备份目录并 `sudo systemctl reload nginx`

### 13.2 后端回滚

发布前备份 jar：

```bash
TS=$(date +%Y%m%d%H%M%S)
cp /opt/blog/blog-api/app.jar /opt/blog/backup/app-$TS.jar || true
```

回滚：恢复旧 jar 后重启：

```bash
sudo systemctl restart blog-api
```

---

## 14) 常见排错

### 14.1 Nginx 语法错误

```bash
sudo nginx -t
sudo journalctl -u nginx -n 200 --no-pager
```

### 14.2 80 端口被占用

```bash
sudo ss -lntp | grep :80
```

### 14.3 /api 404

- 先确认后端：`curl http://127.0.0.1:8080/api/health`
- 再确认 Nginx：`location /api/ { proxy_pass http://127.0.0.1:8080; }`

### 14.4 uploads 访问 404

- 确认文件是否在 `/opt/blog/uploads`
- 确认 Nginx 用的是 `alias /opt/blog/uploads/;`
- 确认目录权限（至少 Nginx 可读；后端可写）

### 14.5 后端启动失败

```bash
sudo systemctl status blog-api --no-pager
sudo tail -n 200 /var/log/blog/blog-api.err.log
sudo journalctl -u blog-api -n 200 --no-pager
```

---

## 15) 部署前必须替换的变量清单

- Nginx `server_name`
- `application-prod.yml`：
  - `spring.datasource.password`
  - `app.jwt.secret`
  - （确认）`app.upload.dir=/opt/blog/uploads`
