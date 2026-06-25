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
- 我的订单（支付、收货确认，多商品订单按明细展示）
- 个人中心（修改密码、个人信息）

### 后台管理端

- 管理员登录（超级管理员 / 运营人员）
- 运营人员注册与审核（通过 / 拒绝 / 撤销拒绝）
- 服装管理（上架、编辑、删除、多条件查询，支持 jpg/png/gif/webp 图片）
- 订单管理（查看、发货）
- 用户管理（增删改查）
- **数据隔离**：运营人员仅可见自己上架的服装和对应订单，超级管理员可见全部数据

## 系统设计

### 角色体系

| 角色       | 编号 | 权限                                 |
| ---------- | :--: | ------------------------------------ |
| 超级管理员 |  1   | 全部权限（服装 / 订单 / 用户管理）   |
| 普通用户   |  2   | 前台浏览、购物                       |
| 运营人员   |  3   | 服装上架管理、订单发货（仅自有商品） |

### 尺码体系

| 类型 | 尺码                 |
| ---- | -------------------- |
| 衣服 | XS, S, M, L, XL, XXL |
| 裤子 | XS, S, M, L, XL, XXL |
| 鞋   | 35-44                |
| 配饰 | 均码                 |

### 数据隔离

- `t_clothes` 表保留 `operator_id` 字段，记录服装归属的运营人员
- `t_order_item` 表拆分订单为行项目，每个行项目记录对应服装的 `operator_id`
- 运营人员登录后，服装列表和订单列表自动过滤为仅自有数据
- 前台用户端（未登录 / 普通用户）不受影响，可浏览全部服装
- 种子服装（10 件示例商品）统归超级管理员可见，运营人员不可见

## 快速开始

### 环境要求

- **Java** 25+
- **Node.js** 22+ (推荐 22.18.0) 或 24.12.0+
- **pnpm** 10.33+
- **MySQL** 8.0+
- **Maven** 3.8+ (或使用 `mvnw` 包装器)

### 1. 启动数据库

创建 MySQL 数据库并导入表结构和种子数据：

```sql
CREATE DATABASE IF NOT EXISTS wardrobe CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

然后导入 `online-wardrobe-backend/src/main/resources/schema.sql`（包含建表语句、服装类型和尺码种子数据）。

### 2. 启动后端

```bash
cd online-wardrobe-backend

# 配置环境变量（复制 .env.example 为 .env 并修改）
cp .env.example .env

# 启动服务
./mvnw spring-boot:run
```

后端启动后监听 `http://localhost:8080`，首次启动将自动创建 `admin` 管理员账号（默认密码 `admin123`）和 10 件示例服装。

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

## 已知局限性

| 问题               | 说明                                                                                               | 建议方案                                                                                                                                 |
| ------------------ | -------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------- |
| 前端分页           | 服装列表、订单列表均为前端分页（全量拉取后 slice），数据量大时性能不足。后台管理端和用户端均存在。 | 后端增加 `page` / `pageSize` 查询参数，MyBatis 使用 `LIMIT #{offset}, #{pageSize}` 实现服务端分页，同时返回 `total` 总量供前端渲染分页器 |
| 多商家订单整单流转 | 当前多商家订单以整单状态流转，任意商家发货后整单即标记为已发货。客户可提前确认收货。               | 生产环境建议将发货/收货粒度细化到 `OrderItem` 行级别                                                                                     |
| 手机号为纯文本     | 未做短信验证                                                                                       | 可接入第三方短信服务                                                                                                                     |
| 库存无管理         | 无库存数量字段，默认无限库存                                                                       | 在 `t_clothes` 表增加 `stock` 字段                                                                                                       |
| 支付为模拟         | 支付操作为即时确认，无真实支付网关                                                                 | 可接入支付宝 / 微信支付 SDK                                                                                                              |

## 相关文档

- [10.1 项目内容说明](docs/10.1项目内容说明.md)
- [10.2 项目开发准备工作](docs/10.2项目开发准备工作.md)
- [10.4 后台管理端](docs/10.4后台管理端.md)
- [10.5 安全增强与补充功能](docs/10.5安全增强与补充功能.md)
