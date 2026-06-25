# online-wardrobe-admin

网上衣橱后台管理端，基于 Vue 3 + TypeScript + Vite + Element Plus。

## 技术栈

| 类别         | 技术                    | 版本    |
| ------------ | ----------------------- | ------- |
| 框架         | Vue 3 (Composition API) | ^3.5.34 |
| 构建工具     | Vite                    | ^8.0.12 |
| 语言         | TypeScript              | ~6.0    |
| UI 组件库    | Element Plus            | ^2.14.2 |
| 状态管理     | Pinia                   | ^3.0.4  |
| 路由         | Vue Router              | ^4.6.4  |
| HTTP 客户端  | Axios                   | ^1.18.0 |
| 图标         | @element-plus/icons-vue | ^2.3.2  |
| 包管理器     | pnpm                    | 10.33+  |

## 项目结构

```
src/
├── main.ts                  # 入口 (Vue, Pinia, Router, ElementPlus)
├── App.vue                  # 根组件
├── api/
│   └── index.ts             # 管理端 API 请求封装（含 OrderItem 类型）
├── assets/
├── components/
│   └── AdminLayout.vue      # 管理端侧边栏布局
├── constants/
│   └── order.ts             # 订单状态常量
├── layout/                  # (预留) 布局组件
├── router/
│   └── index.ts             # 路由配置 (hash 模式, 鉴权守卫，用户管理仅超管可访问)
├── stores/
│   └── auth.ts              # 管理员状态 (token, 登录/登出)
├── style.css
└── views/
    ├── Dashboard.vue        # 仪表盘 / 首页
    ├── Login.vue            # 管理员登录（含服务器错误消息展示）
    ├── RegisterOperator.vue # 运营人员自助注册
    ├── Clothes.vue          # 服装管理（CRUD, 搜索, 图片上传含 webp）
    ├── Orders.vue           # 订单管理（列表, 发货, 订单明细展示）
    └── Users.vue            # 用户管理（CRUD, 运营人员审核：通过/拒绝/撤销拒绝）
```

## 角色体系

| 角色 | 编号 | 可访问页面 |
|------|:--:|------------|
| 超级管理员 | 1 | 全部页面（仪表盘、服装、订单、用户管理） |
| 运营人员 | 3 | 仪表盘、服装（仅自有）、订单（仅自有商品相关） |
| 普通用户 | 2 | 无法登录管理后台 |

## 路由

使用 hash 模式（`createWebHashHistory`），无需服务端路由配置。

- `/login` — 管理员登录（公开）
- `/register` — 运营人员注册（公开，提交后需超级管理员审核）
- `/` 仪表盘 — 需登录
- `/clothes` 服装管理 — 需登录（运营人员仅可见自有服装）
- `/orders` 订单管理 — 需登录（运营人员仅可见自有商品相关订单）
- `/users` 用户管理 — 需登录（仅超级管理员可访问）

## 快速开始

```bash
pnpm install
pnpm dev        # 启动开发服务器，监听 http://localhost:7071
pnpm build      # 类型检查 + 生产构建
pnpm preview    # 预览生产构建
```

> 开发环境 API 地址由 `.env.development` 中的 `VITE_API_BASE_URL` 指定，默认 `http://localhost:8080/api`。生产构建时由 `.env.production` 指定。

## 页面功能

| 页面       | 路径         | 功能 |
| ---------- | ------------ | ---- |
| 登录       | `/login`     | 管理员用户名+密码登录（含服务器错误提示） |
| 运营人员注册 | `/register` | 运营人员自助注册（提交后审核状态，超管在用户管理中审核） |
| 仪表盘     | `/dashboard` | 管理后台首页 |
| 服装管理   | `/clothes`   | 服装列表、多条件搜索（名称/类别/风格）、上架（含图片上传 jpg/png/gif/webp）、编辑、删除；运营人员仅见自有服装 |
| 订单管理   | `/orders`    | 订单列表、按用户/状态筛选、订单明细展示、发货操作；运营人员仅见自有商品相关订单 |
| 用户管理   | `/users`     | 用户列表（含手机号脱敏）、按用户名/手机号搜索、添加用户（含手机号）、编辑用户、删除；运营人员审核：通过/拒绝/撤销拒绝（仅超管） |

## 运营人员审核流程

1. 运营人员在 `/register` 页面提交注册申请 → 状态为"待审核"（status=0）
2. 超级管理员在"用户管理"页面查看待审核的运营人员
3. 点击"通过" → 状态变为"已通过"（status=1），运营人员可登录管理后台
4. 点击"拒绝" → 状态变为"已拒绝"（status=2），运营人员无法登录
5. 可对已拒绝用户执行"撤销拒绝"恢复为已通过状态
