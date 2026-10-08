# MallShop 电商实战项目

基于 Spring Boot 3 + Vue 3 的全栈电商系统，包含前台商城与后台管理，覆盖商品、订单、购物车、秒杀、优惠券、支付（支付宝沙箱）、全文搜索等核心电商业务场景，适合作为 Java 全栈学习与实战参考项目。

> 本项目由 AI 辅助（Claude Code）开发完成。

## 功能概览

**前台商城（mall-portal-web）**

- 商品浏览、分类、品牌、ES 全文搜索（IK 中文分词）
- 购物车、下单、支付宝沙箱支付、订单超时自动取消
- 优惠券（领取 / 使用 / 过期回收）、商品评价
- 秒杀（Redis 预扣库存，MQ 异步下单）

**后台管理（mall-admin-web）**

- 商品 / 分类 / 品牌管理，图片上传（MinIO）
- 订单管理、会员管理
- 数据统计（ECharts 可视化）、系统管理

## 技术栈

| 层 | 技术 |
|----|------|
| 后端 | Spring Boot 3.2.5、Java 17、MyBatis-Plus 3.5.6、Spring Security + JWT |
| 中间件 | MySQL 8、Redis 7、RabbitMQ（死信队列）、Elasticsearch 7.x、MinIO |
| 前端 | Vue 3、Vite、Pinia、Element Plus（后台）、Tailwind CSS（前台）、ECharts |
| 其他 | SpringDoc（Swagger）、Docker Compose |

## 项目结构

```
ai-test-mall/
├── mall/                    # 后端（多模块 Maven 工程）
│   ├── mall-common/         # 公共模块（统一返回、工具、全局异常）
│   ├── mall-mbg/            # 数据层（实体 + Mapper + 分页配置）
│   ├── mall-security/       # 安全模块（JWT、认证过滤器、SecurityConfig）
│   ├── mall-admin/          # 后台管理 API（主启动模块，端口 8080）
│   ├── mall-portal/         # 前台商城 API（订单、秒杀、优惠券、支付）
│   ├── mall-search/         # ES 搜索服务
│   ├── sql/                 # 数据库初始化脚本（19 张表 + 初始数据）
│   └── docker-compose.yml   # 中间件容器编排
├── mall-web/                # 前端
│   ├── mall-admin-web/      # 后台管理（Vue 3 + Element Plus，端口 5174）
│   └── mall-portal-web/     # 前台商城（Vue 3 + Tailwind，端口 5173）
├── docs/                    # 需求与技术文档（需求规格、技术方案、API 文档等）
├── design-system/           # UI 设计规范
└── ROADMAP.md               # 项目进度快照
```

## 快速开始

### 环境要求

JDK 17+、Maven 3.8+、Node.js 18+、Docker 20+ / Docker Compose 2.0+

### 后端

```bash
cd mall

# 1. 启动中间件（MySQL/Redis/ES/RabbitMQ/MinIO）
docker-compose up -d

# 2. 初始化数据库（应列出 19 张表）
mysql -h 127.0.0.1 -u root -proot < sql/mall.sql

# 3. 编译并启动（主启动模块为 mall-admin，端口 8080）
mvn clean install -DskipTests
cd mall-admin && mvn spring-boot:run
```

启动成功后访问 Swagger：http://localhost:8080/swagger-ui.html

默认管理员账号：`admin / 123456`。ES IK 分词插件安装、MinIO Bucket 创建、支付宝沙箱配置等步骤详见 [mall/docs/快速启动说明文档.md](mall/docs/快速启动说明文档.md)，配置项详解见 [mall/docs/人工配置文档.md](mall/docs/人工配置文档.md)。

### 前端

```bash
cd mall-web/mall-portal-web && npm install && npm run dev   # 前台商城 http://localhost:5173
cd mall-web/mall-admin-web  && npm install && npm run dev   # 后台管理 http://localhost:5174
```

前端依赖后端运行在 http://localhost:8080，详细说明见 [mall-web/README.md](mall-web/README.md)。

## 已知问题

项目仍在迭代中，已知待处理事项（详见 [ROADMAP.md](ROADMAP.md)）：

- 秒杀链路未闭环：秒杀成功只写 Redis 结果，MQ 异步创建订单未实现
- 限流切面信任 `X-Forwarded-For` 头，存在 IP 伪造绕过限流的可能
- 修改密码后旧 JWT 不失效
- `mall-common` 中的 `JwtUtil` 为无引用死代码（含硬编码密钥），待删除

## 文档导航

| 文档 | 说明 |
|------|------|
| [mall/docs/快速启动说明文档.md](mall/docs/快速启动说明文档.md) | 后端 9 步启动流程、常见问题排查 |
| [mall/docs/人工配置文档.md](mall/docs/人工配置文档.md) | 中间件、支付宝沙箱、配置项详解 |
| [mall-web/README.md](mall-web/README.md) | 前端两个应用的启动说明 |
| [docs/需求规格文档.md](docs/需求规格文档.md) | 产品需求规格 |
| [docs/技术方案文档.md](docs/技术方案文档.md) | 架构与技术选型 |
| [docs/API接口文档.md](docs/API接口文档.md) | 接口文档 |
| [ROADMAP.md](ROADMAP.md) | 项目进度与已知问题 |

## 许可证

[MIT License](LICENSE)
