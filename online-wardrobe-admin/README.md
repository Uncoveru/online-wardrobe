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
│   └── index.ts             # 管理端 API 请求封装
├── assets/
├── components/
├── router/
│   └── index.ts             # 路由配置 (hash 模式, 鉴权守卫)
├── stores/
│   └── auth.ts              # 管理员状态 (token, 登录/登出)
├── style.css
└── views/
    ├── Dashboard.vue        # 仪表盘 / 首页
    ├── Login.vue            # 管理员登录
    ├── Clothes.vue          # 服装管理 (CRUD 表格, 搜索)
    ├── Orders.vue           # 订单管理 (列表, 发货)
    └── Users.vue            # 用户管理 (CRUD 表格, 搜索)
```

## 路由

使用 hash 模式（`createWebHashHistory`），无需服务端路由配置。

- `/login` — 管理员登录（公开）
- `/` — 仪表盘（需登录）
- `/clothes` — 服装管理（需登录）
- `/orders` — 订单管理（需登录）
- `/users` — 用户管理（需登录）

## 快速开始

```bash
pnpm install
pnpm dev        # 启动开发服务器，监听 http://localhost:7071
pnpm build      # 类型检查 + 生产构建
pnpm preview    # 预览生产构建
```

> 管理端 API 地址硬编码为 `http://localhost:8080/api`，部署时请修改 `src/api/index.ts` 中的 `BASE_URL`。

## 页面功能

| 页面     | 路径         | 功能                                                   |
| -------- | ------------ | ------------------------------------------------------ |
| 登录     | `/login`     | 管理员用户名+密码 或 手机号+密码登录                   |
| 仪表盘   | `/`          | 管理后台首页                                           |
| 服装管理 | `/clothes`   | 服装列表、多条件搜索、上架、编辑、删除                 |
| 订单管理 | `/orders`    | 订单列表、按用户/状态筛选、发货操作                    |
| 用户管理 | `/users`     | 用户列表、按用户名/手机号搜索、添加、编辑、删除        |
