# 数据库表结构草案（建议补充项）

你现在的文档里已经有数据模型“范围”，但距离直接写 MyBatis-Plus 表与 SQL，通常还缺一份：

- 完整建表 SQL（MySQL 5.5 兼容）
- 字段类型、索引、约束（唯一键、外键是否使用等）
- 初始化数据（默认管理员、默认站点配置）

## 建议：先以“最小可用 + 可演进”方式落地

### 1) admin_user
- username 唯一
- password 使用 BCrypt hash

### 2) post
- status：DRAFT/PUBLISHED
- is_deleted：逻辑删除
- published_at：发布时写入

### 3) category / tag / post_tag
- post_tag 建联合唯一键（post_id, tag_id）

### 4) comment
- status：PENDING/APPROVED/REJECTED
- 建议有 post_id 索引

### 5) site_setting
- 固定一行（id=1）

### 6) file_resource
- 记录 url、原始名、size、content_type

## 下一步
如果你希望我继续自动化推进：我可以直接生成一份 MySQL 5.5 兼容的 `deploy/sql/init.sql`（包含建表 + 初始化数据），并在 PRD/验收里引用它。
