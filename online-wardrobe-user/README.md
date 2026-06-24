# online-wardrobe-user

网上衣橱前台用户端，基于 Vue 3 + TypeScript + Vite + Element Plus。

## 技术栈

| 类别        | 技术                    | 版本    |
| ----------- | ----------------------- | ------- |
| 框架        | Vue 3 (Composition API) | ^3.5.34 |
| 构建工具    | Vite                    | ^8.0.12 |
| 语言        | TypeScript              | ~6.0    |
| UI 组件库   | Element Plus            | ^2.14.2 |
| 状态管理    | Pinia                   | ^3.0.4  |
| 路由        | Vue Router              | ^4.6.4  |
| HTTP 客户端 | Axios                   | ^1.18.0 |
| 图标        | @element-plus/icons-vue | ^2.3.2  |
| 包管理器    | pnpm                    | 10.33+  |

## 项目结构

```
src/
├── main.ts                  # 入口 (Vue, Pinia, Router, ElementPlus)
├── App.vue                  # 根组件
├── api/
│   └── index.ts             # 所有 API 请求封装
├── assets/
│   ├── main.css
│   └── theme.css
├── components/
│   ├── AppHeader.vue        # 顶部导航栏
│   ├── AppLayout.vue        # 布局容器（含 Header）
│   ├── OrderCard.vue        # 订单卡片
│   └── ProductCard.vue      # 服装商品卡片
├── router/
│   └── index.ts             # 路由配置 (history 模式, 鉴权守卫)
├── stores/
│   └── auth.ts              # 用户状态 (token, 登录/登出)
├── utils/
└── views/
    ├── Home.vue             # 首页 (服装列表, 分类/风格筛选)
    ├── ClothDetail.vue      # 服装详情 (尺码选择, 加入购物车)
    ├── Cart.vue             # 购物车 (修改数量, 删除, 结算)
    ├── Orders.vue           # 我的订单 (按状态筛选, 支付, 确认收货)
    ├── Login.vue            # 用户登录
    ├── Register.vue         # 用户注册
    ├── Profile.vue          # 个人中心 (修改密码, 修改信息)
    └── NotFound.vue         # 404 页面
```

## 路由守卫

使用 `beforeEach` 导航守卫，访问 Cart、Orders、Profile 页面时如无 token 则重定向到 `/login`。

## 快速开始

```bash
pnpm install
pnpm dev        # 启动开发服务器，监听 http://localhost:7070
pnpm build      # 类型检查 + 生产构建
pnpm preview    # 预览生产构建
```

## 环境变量

`.env.development`（开发环境）:

```env
VITE_API_BASE_URL=http://localhost:8080/api
VITE_IMAGE_BASE_URL=http://localhost:8080/images
```

`.env.production`（生产环境）:

```env
VITE_API_BASE_URL=/api
VITE_IMAGE_BASE_URL=/images
```

## 页面功能

| 页面     | 路径         | 功能                                                    |
| -------- | ------------ | ------------------------------------------------------- |
| 首页     | `/`          | 服装列表、按类别/风格筛选、关键词搜索、查看详情         |
| 服装详情 | `/cloth/:id` | 服装大图、风格/类别信息、尺码选择、加入购物车           |
| 购物车   | `/cart`      | 查看购物车、修改数量、删除商品、批量结算                |
| 我的订单 | `/orders`    | 订单列表（未支付/未发货/已发货/已收货）、支付、确认收货 |
| 个人中心 | `/profile`   | 查看/修改个人信息、修改密码                             |
| 登录     | `/login`     | 用户名+密码 或 手机号+密码登录                          |
| 注册     | `/register`  | 用户名、密码、手机号、地址注册                          |
