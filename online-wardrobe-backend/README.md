# online-wardrobe-backend

网上衣橱 REST API 后端服务，基于 Spring Boot 4.1 + MyBatis + MySQL 8。

## 技术栈

| 类别     | 技术                            | 版本   |
| -------- | ------------------------------- | ------ |
| 框架     | Spring Boot (WebMVC)            | 4.1.0  |
| ORM      | MyBatis Spring Boot Starter     | 4.0.1  |
| 数据库   | MySQL                           | 8.x    |
| 认证     | JJWT (JSON Web Token)           | 0.12.6 |
| 密码加密 | Spring Security Crypto (BCrypt) | —      |
| 环境变量 | spring-dotenv                   | 4.0.0  |
| 语言     | Java                            | 25     |
| 构建工具 | Maven                           | 3.8+   |

## 项目结构

```
src/main/java/com/wardrobe/backend/
├── OnlineWardrobeBackendApplication.java   # 启动入口
├── config/
│   ├── ClothesDataInitializer.java         # 服装类型/尺码种子数据
│   ├── CorsConfig.java                     # 跨域配置
│   ├── DataInitializer.java                # 管理员种子账号
│   ├── JwtConfig.java                      # JWT 拦截器 & 权限控制
│   └── SecurityConfig.java                 # BCrypt 密码编码器
├── controller/
│   ├── AdminAuthController.java            # /api/admin/login
│   ├── AdminUserController.java            # /api/admin/users CRUD
│   ├── CartController.java                 # /api/cart 购物车 CRUD
│   ├── ClothesController.java              # /api/clothes 服装 CRUD + /api/types + /api/sizes
│   ├── OrderController.java                # /api/orders 订单管理
│   └── UserController.java                 # /api/user 注册/登录/个人信息
├── dto/
│   └── Result.java                         # 统一响应 {code, message, data}
├── entity/                                 # 数据实体 (Cart, Clothes, Order, Size, Type, User)
├── enums/
│   └── RolePermission.java                 # 角色枚举 (SUPER_ADMIN=1, USER=2, OPERATOR=3)
├── exception/
│   ├── AuthenticationException.java        # 401 未授权
│   └── ForbiddenException.java             # 403 禁止访问
├── mapper/                                 # MyBatis Mapper 接口 & XML
├── service/                                # 业务逻辑层
└── utils/
    ├── AuthUtils.java                      # 从请求上下文获取用户信息
    └── JwtUtils.java                       # JWT 生成/解析/校验
```

## 数据库

数据库名：`wardrobe`，字符集：`utf8mb4`。

建表脚本位于 `src/main/resources/schema.sql`，包含以下表：

| 表名        | 说明             |
| ----------- | ---------------- |
| `t_user`    | 用户和管理员     |
| `t_type`    | 服装类别         |
| `t_size`    | 尺码（关联类别） |
| `t_clothes` | 服装商品         |
| `t_cart`    | 购物车           |
| `t_order`   | 订单             |

## 快速开始

### 1. 环境配置

```bash
cp .env.example .env
# 编辑 .env 填入数据库密码和 JWT 密钥
```

### 2. 创建数据库

```sql
CREATE DATABASE IF NOT EXISTS wardrobe CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 启动服务

```bash
./mvnw spring-boot:run
```

服务启动后监听 `http://localhost:8080`。首次启动自动创建管理员账号 `admin`（密码由 `ADMIN_SEED_PASSWORD` 环境变量指定）及服装类别种子数据。

## API 接口

### 鉴权说明

- `Authorization: Bearer <token>` 请求头传递 JWT
- 公开 GET 接口：`/api/clothes/**`、`/api/types`、`/api/sizes`、`/images/**`
- 公开 POST 接口：`/api/user/login`、`/api/user/register`、`/api/admin/login`
- `/api/admin/**` 需要管理员角色（role 1 或 3）
- 其他接口需要登录

### 用户接口 (`/api/user`)

| 方法 | 路径        | 说明         | 鉴权 |
| ---- | ----------- | ------------ | ---- |
| POST | `/login`    | 用户登录     | 否   |
| POST | `/register` | 用户注册     | 否   |
| PUT  | `/password` | 修改密码     | 是   |
| PUT  | `/profile`  | 更新个人信息 | 是   |
| GET  | `/orders`   | 查询我的订单 | 是   |
| POST | `/register-operator` | 注册运营人员 | 否   |

### 管理接口 (`/api/admin`)

| 方法   | 路径          | 说明         | 鉴权   |
| ------ | ------------- | ------------ | ------ |
| POST   | `/login`      | 管理员登录（status≠1 拒绝）| 否     |
| GET    | `/users`      | 查询用户列表 | 管理员 |
| POST   | `/users`      | 添加用户     | 管理员 |
| PUT    | `/users/{id}` | 修改用户     | 管理员 |
| DELETE | `/users/{id}` | 删除用户     | 管理员 |
| PUT    | `/users/{id}/approve` | 审核通过 | 管理员 |
| PUT    | `/users/{id}/reject` | 审核拒绝 | 管理员 |
| PUT    | `/users/{id}/undo-reject` | 撤销拒绝 | 管理员 |

### 服装接口 (`/api/clothes`)

| 方法   | 路径              | 说明     | 鉴权   |
| ------ | ----------------- | -------- | ------ |
| GET    | `/clothes`        | 服装列表 | 否     |
| GET    | `/clothes/search` | 搜索服装 | 否     |
| GET    | `/clothes/{id}`   | 服装详情 | 否     |
| POST   | `/clothes`        | 上架服装 | 管理员 |
| PUT    | `/clothes/{id}`   | 修改服装 | 管理员 |
| DELETE | `/clothes/{id}`   | 删除服装 | 管理员 |

### 类别/尺码接口

| 方法 | 路径             | 说明             | 鉴权 |
| ---- | ---------------- | ---------------- | ---- |
| GET  | `/types`         | 获取所有服装类别 | 否   |
| GET  | `/sizes?typeId=` | 获取指定类别尺码 | 否   |

### 购物车接口 (`/api/cart`)

| 方法   | 路径             | 说明       | 鉴权 |
| ------ | ---------------- | ---------- | ---- |
| GET    | `/cart`          | 查询购物车 | 是   |
| POST   | `/cart`          | 加入购物车 | 是   |
| PUT    | `/cart/{id}`     | 修改数量   | 是   |
| DELETE | `/cart/{id}`     | 删除商品   | 是   |
| POST   | `/cart/checkout` | 结算       | 是   |

### 订单接口 (`/api/orders`)

| 方法 | 路径                  | 说明         | 鉴权   |
| ---- | --------------------- | ------------ | ------ |
| GET  | `/orders`             | 订单列表     | 管理员 |
| PUT  | `/orders/{id}/ship`   | 发货         | 管理员 |

## 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```
