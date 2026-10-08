# MallShop ROADMAP

> 项目进度快照源。完成影响项目状态的开发、修复或文档变更后必须更新。

## 项目当前状态

- 后端：Spring Boot 3.2.5 + Java 17 多模块（mall-common / mall-mbg / mall-security / mall-admin / mall-portal / mall-search），MySQL + Redis + RabbitMQ + ES + MinIO。
- 前端：mall-admin-web（Vue 3 + Element Plus）、mall-portal-web（Vue 3 + Tailwind CSS）。
- 验证入口：后端 `cd mall && mvn -o compile -DskipTests`、`mvn -o -pl mall-portal test`；启动流程见 `mall/docs/快速启动说明文档.md`。

## 进行中

- 秒杀链路未闭环：`SeckillServiceImpl.seckill()` Step 5 的 MQ 异步创建订单仍未实现（代码内注释标注），秒杀成功只写 Redis 结果，不会真正生成订单。

## 最近完成

- 2026-10-08 21:43 chore: 开源工程化初始化——`git init`（main 分支）并添加 `.gitignore`（排除 target/、node_modules/、日志、.env、.claude/ 等）；添加 MIT `LICENSE` 和根 `README.md`（项目介绍、快速开始、文档导航、已知问题披露）；清理 `mall.log` 与 `.DS_Store`；`mall-admin` 的 `jwt.secret` 改为 `${JWT_SECRET:开发默认值}` 环境变量注入并同步 `人工配置文档.md`。验证：`mvn -o compile -DskipTests` 全模块通过，`mvn -o -pl mall-portal test` 49 个测试全部通过。
- 2026-09-24 16:31 fix: 修复下单流程两处库存泄漏——优惠券校验前置到库存扣减之前（`OmsOrderPortalServiceImpl.create()`），DB 乐观锁扣减中途失败时补偿回滚已扣减的 DB 库存；修复超时取消订单不回滚库存/不返还优惠券（`OrderTimeoutListener` 对齐用户取消逻辑）；修复 `payStatus` 未校验订单归属的越权查询。验证：`mvn -o compile -DskipTests` 全模块通过，`mvn -o -pl mall-portal test` 49 个测试全部通过。

## 已知问题（待确认/未修复）

- `mall-common/src/main/java/com/mall/common/util/JwtUtil.java` 无任何业务代码引用（死代码），且内置硬编码 JWT 密钥，建议删除；实际使用的是 `mall-security` 的 `JwtTokenUtil`（密钥走配置）。
- `CouponExpireTask` 注入了未使用的 `SmsCouponMapper` 字段。
- 限流切面 `RateLimitAspect` 直接信任 `X-Forwarded-For` 头，客户端可伪造 IP 绕过限流；`X-User-Id` 头同样可伪造。
- 修改密码后（`UmsMemberPortalServiceImpl.updatePassword`）未使旧 JWT 失效。
- 支付宝下单 `subject` 手工拼接 JSON，商品名含双引号会破坏 bizContent（商品名来自管理端，风险低）。
- 支付宝回调验签使用 `rsaCheckV1`，若配置 sign_type=RSA2 建议改用 `rsaCheckV2` 语义对应的校验（待确认当前配置）。
