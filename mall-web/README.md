# MallShop 前端项目 - 快速启动说明

## 项目概述

本项目包含两个前端应用：

| 项目 | 技术栈 | 端口 | 说明 |
|------|--------|------|------|
| mall-admin-web | Vue 3 + Element Plus + ECharts | 5174 | 后台管理系统 |
| mall-portal-web | Vue 3 + Tailwind CSS | 5173 | 前台商城 |

## 环境要求

- Node.js >= 18.0.0
- npm >= 9.0.0
- 后端服务运行在 http://localhost:8080

## 目录结构

```
mall-web/
├── mall-admin-web/          # 后台管理系统
│   ├── src/
│   │   ├── api/             # API 接口
│   │   ├── components/      # 公共组件
│   │   ├── layouts/         # 布局组件
│   │   ├── router/          # 路由配置
│   │   ├── stores/          # Pinia 状态管理
│   │   ├── utils/           # 工具函数
│   │   └── views/           # 页面组件
│   │       ├── login/       # 登录页
│   │       ├── dashboard/   # 首页概览
│   │       ├── product/     # 商品管理
│   │       ├── order/       # 订单管理
│   │       ├── member/      # 会员管理
│   │       ├── stat/        # 数据统计
│   │       └── system/      # 系统管理
│   ├── package.json
│   └── vite.config.js
│
└── mall-portal-web/         # 前台商城
    ├── src/
    │   ├── api/             # API 接口
    │   ├── components/      # 公共组件
    │   ├── layouts/         # 布局组件
    │   ├── router/          # 路由配置
    │   ├── stores/          # Pinia 状态管理
    │   ├── utils/           # 工具函数
    │   └── views/           # 页面组件
    │       ├── auth/        # 登录注册
    │       ├── home/        # 首页
    │       ├── product/     # 商品相关
    │       ├── cart/        # 购物车
    │       ├── order/       # 订单相关
    │       ├── pay/         # 支付
    │       └── user/        # 个人中心
    ├── package.json
    ├── vite.config.js
    └── tailwind.config.js
```

## 快速启动

### 1. 安装依赖

**Windows (Git Bash):**
```bash
# 进入后台管理项目
cd mall-web/mall-admin-web
npm install

# 进入前台商城项目
cd ../mall-portal-web
npm install
```

### 2. 启动开发服务器

**后台管理系统 (端口 5174):**
```bash
cd mall-web/mall-admin-web
npm run dev
```
访问: http://localhost:5174

**前台商城 (端口 5173):**
```bash
cd mall-web/mall-portal-web
npm run dev
```
访问: http://localhost:5173

### 3. 构建生产环境

```bash
# 后台管理
cd mall-web/mall-admin-web
npm run build

# 前台商城
cd mall-web/mall-portal-web
npm run build
```

## 功能清单

### 后台管理系统 (mall-admin-web)

| 模块 | 功能 |
|------|------|
| 登录 | 管理员账号密码登录、JWT Token 认证 |
| 首页概览 | 销售统计卡片、销售趋势图、热销商品 TOP5 |
| 商品管理 | 商品列表、新增/编辑/删除、上下架状态、批量操作 |
| 分类管理 | 树形分类列表、新增/编辑/删除、层级展示 |
| 品牌管理 | 品牌列表、新增/编辑/删除、推荐设置 |
| 订单管理 | 订单列表、详情查看、发货处理、退款审批 |
| 会员管理 | 会员列表、详情查看 |
| 数据统计 | 订单统计(日/周/月)、商品统计、用户统计 |
| 系统管理 | 管理员管理、角色管理、菜单管理、权限分配 |

### 前台商城 (mall-portal-web)

| 模块 | 功能 |
|------|------|
| 首页 | Banner、分类入口、热销推荐、品牌专区 |
| 商品列表 | 分类/品牌筛选、排序、分页 |
| 商品详情 | 图片轮播、SKU 选择、加入购物车、立即购买 |
| 购物车 | 数量调整、选中结算、删除商品 |
| 订单确认 | 地址选择、商品清单、订单备注 |
| 支付 | 支付宝支付 |
| 订单管理 | 订单列表、状态筛选、取消/确认收货 |
| 个人中心 | 用户信息、订单入口、地址管理、购物车 |
| 收货地址 | 新增/编辑/删除、默认地址设置 |

## 技术架构

### 后台管理系统
- **Vue 3** - 前端框架 (Composition API)
- **Element Plus** - UI 组件库
- **Vue Router 4** - 路由管理
- **Pinia** - 状态管理
- **Axios** - HTTP 客户端
- **ECharts + vue-echarts** - 图表可视化
- **Vite 5** - 构建工具

### 前台商城
- **Vue 3** - 前端框架 (Composition API)
- **Tailwind CSS** - 原子化 CSS 框架
- **Vue Router 4** - 路由管理
- **Pinia** - 状态管理
- **Axios** - HTTP 客户端
- **Vite 5** - 构建工具

## 设计规范

- **主色调**: 中国红 (#E11D48)
- **背景色**: 浅粉 (#FFF1F2)
- **文字色**: 深红 (#881337)
- **字体**: Rubik (标题) + Nunito Sans (正文)
- **风格**: 扁平化 + 圆角卡片 + 柔和阴影

## 后端 API 代理配置

两个项目的 Vite 配置中均已配置代理：
```javascript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

确保后端服务运行在 `http://localhost:8080`。

## 注意事项

1. 首次运行前请确保后端服务已启动
2. 后台管理登录需要管理员账号
3. 前台商城用户需要注册/登录后才能下单
4. 支付功能使用支付宝沙箱环境
