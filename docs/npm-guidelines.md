# npm 使用约定（本项目统一）

## 1) 为什么统一 npm

- Node 自带，无需额外安装
- 对个人项目与简单部署足够

## 2) 统一约定

- 不使用 pnpm/yarn（避免 lockfile 混乱）
- 每个前端子项目使用自己的 `package-lock.json`
- Node 版本建议固定在 20 LTS（团队统一）

## 3) 常用命令（将来骨架生成后）

安装依赖：

```powershell
npm install
```

启动开发：

```powershell
npm run dev
```

构建生产包：

```powershell
npm run build
```

本地预览：

```powershell
npm run preview
```

## 4) 依赖升级策略

- 首选升级 patch/minor
- 升级前跑：typecheck/lint/build
- 重要升级（Vue/Vite 大版本）单独开分支测试
