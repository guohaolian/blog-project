# 开发路线图（以当前代码实现为准）
> 说明：本文用于记录“已经做到了哪里、下一步做什么”。
> 历史的里程碑编号在项目早期有价值，但随着闭环逐步完成，更适合用“Done / In progress / Next”表达。
## ✅ Done（已完成）
### 1) 工程与规范
- 三项目结构（`blog-web` / `blog-admin-web` / `blog-api`）与基础文档
- 前后端分离：/api 统一前缀，开发期 Vite proxy，生产期 Nginx 反代
### 2) 后端（blog-api）
- 健康检查：`GET /api/health`
- 统一响应体 ApiResponse + 全局异常
- Admin 登录：Spring Security + JWT（`/api/admin/auth/login`、`/api/admin/auth/me`）
- 文章：后台 CRUD + 发布/撤回；前台已发布列表/详情
- 阅读量：访问详情累加；热门文章按阅读量
- 分类/标签：后台 CRUD；前台列表与筛选
- 评论：匿名提交（默认 PENDING）；后台审核/删除；前台仅展示 APPROVED
- 上传：图片上传（<=10MB）返回 `/uploads/**`
- `/uploads/**` 静态映射（后端映射 + 生产期 Nginx 静态映射）
- 资源管理：上传落库到 `file_resource`，资源分页列表与删除
- 站点设置：`/api/site` + `/api/admin/site`（含 bannerUrl）
### 3) 管理端（blog-admin-web）
- 登录页 + 路由守卫
- Dashboard（统计+图表）
- Posts 管理（列表/编辑/发布/撤回/删除，封面上传）
- Categories/Tags 管理
- Comments 审核
- Resources 列表与删除
- Settings（站点名/公告/关于/SEO/页脚/Banner 上传与配置）
- Admins 管理（新增/重置密码/状态）
- 暗色模式：支持 Light/Dark/Auto；Auto 为按时间窗自动切换
### 4) 前台（blog-web）
- 首页文章列表（分页 + 搜索）
- 文章详情（Markdown 渲染：代码高亮/表格/任务列表）
- 分类/标签列表页 + 筛选列表页 + 路由完善
- 归档页、搜索页、热门文章
- 站点设置实时刷新（site store 定时刷新）
- 首页全屏 Banner（可通过后台上传配置，首屏占满 + 向下按钮）
- Banner 透出导航栏（半透明 topbar/notice 透出 Banner）
- 暗色模式：支持 Light/Dark/Auto；Auto 为按时间窗自动切换
## 🔄 In progress（进行中）
- 部署到 Ubuntu/Nginx/systemd 的线上演练与最终固化（见 `docs/deploy-ecs-nginx.md`）
- 进一步 UI 美化与响应式细节打磨
## ⏭ Next（下一步）
- 生产构建体积优化（manualChunks/按需加载等）
- 站点设置更多扩展位（如 banner 文案、按钮链接、多个 banner 轮播等，可选）
- （可选）RSS/站内订阅