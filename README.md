# 网上衣橱 (Online Wardrobe)

全栈在线服装商城系统，包含前台用户端、后台管理端和 REST API 后端服务。

## 项目结构

```
online-wardrobe/
├── online-wardrobe-backend/   # Spring Boot 后端服务 (port 8080)
├── online-wardrobe-user/      # Vue 3 前台用户端 (port 7070)
├── online-wardrobe-admin/     # Vue 3 后台管理端 (port 7071)
└── docs/                      # 项目文档
```

## 技术栈

| 模块     | 技术                                                              |
| -------- | ----------------------------------------------------------------- |
| 后端     | Spring Boot 4.1, MyBatis 4.0, MySQL 8, JWT (jjwt 0.12), BCrypt    |
| 前台用户 | Vue 3, TypeScript, Vite 8, Element Plus, Pinia, Vue Router, Axios |
| 后台管理 | Vue 3, TypeScript, Vite 8, Element Plus, Pinia, Vue Router, Axios |

## 功能概览

### 前台用户端

- 用户注册 / 登录（用户名+密码 或 手机号+密码）
- 服装浏览（按类别、风格筛选，关键词搜索）
- 服装详情与尺码选择
- 购物车（增删改、批量结算）
- 我的订单（支付、收货确认）
- 个人中心（修改密码、个人信息）

### 后台管理端

- 管理员登录
- 服装管理（上架、编辑、删除、多条件查询）
- 订单管理（查看、发货）
- 用户管理（增删改查）

## 快速开始

### 环境要求

- **Java** 25+
- **Node.js** 22+ (推荐 22.18.0) 或 24.12.0+
- **pnpm** 10.33+
- **MySQL** 8.0+
- **Maven** 3.8+ (或使用 `mvnw` 包装器)

### 1. 启动数据库

创建 MySQL 数据库并导入表结构：

```sql
CREATE DATABASE IF NOT EXISTS wardrobe CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

建表脚本位于 `online-wardrobe-backend/src/main/resources/schema.sql`。

### 2. 启动后端

```bash
cd online-wardrobe-backend

# 配置环境变量（复制 .env.example 为 .env 并修改）
cp .env.example .env

# 启动服务
./mvnw spring-boot:run
```

后端启动后监听 `http://localhost:8080`，首次启动将自动创建 `admin` 管理员账号。

### 3. 启动前台用户端

```bash
cd online-wardrobe-user
pnpm install
pnpm dev
```

访问 `http://localhost:7070`。

### 4. 启动后台管理端

```bash
cd online-wardrobe-admin
pnpm install
pnpm dev
```

访问 `http://localhost:7071`。

## 环境变量

### 后端 (`.env`)

| 变量                  | 说明               | 默认值                            |
| --------------------- | ------------------ | --------------------------------- |
| `DB_URL`              | 数据库连接 URL     | `jdbc:mysql://localhost:3306/...` |
| `DB_USERNAME`         | 数据库用户名       | `one`                             |
| `DB_PASSWORD`         | 数据库密码         | `123456`                          |
| `JWT_SECRET`          | JWT 签名密钥       | `wardrobe-jwt-secret-key-...`     |
| `JWT_EXPIRATION`      | JWT 过期时间 (ms)  | `3600000`                         |
| `ADMIN_SEED_PASSWORD` | 首次启动管理员密码 | `admin123`                        |

### 前台用户端 (`.env.development`)

| 变量                  | 说明         | 默认值                         |
| --------------------- | ------------ | ------------------------------ |
| `VITE_API_BASE_URL`   | API 基础地址 | `http://localhost:8080/api`    |
| `VITE_IMAGE_BASE_URL` | 图片基础地址 | `http://localhost:8080/images` |

## 相关文档

- [10.1 项目内容说明](docs/10.1项目内容说明.md)
- [10.2 项目开发准备工作](docs/10.2项目开发准备工作.md)
- [10.4 后台管理端](docs/10.4后台管理端.md)
- [10.5 安全增强与补充功能](docs/10.5安全增强与补充功能.md)
- [后端代码审查报告](docs/后端代码审查报告.md)
