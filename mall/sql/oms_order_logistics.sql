CREATE DATABASE IF NOT EXISTS `mall` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `mall`;

CREATE TABLE IF NOT EXISTS `oms_order_logistics` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `order_id` bigint NOT NULL,
    `delivery_company` varchar(50) DEFAULT NULL COMMENT '物流公司',
    `delivery_sn` varchar(50) DEFAULT NULL COMMENT '物流单号',
    `status` tinyint DEFAULT 0 COMMENT '物流状态 0运输中 1已签收 2异常',
    `detail` text DEFAULT NULL COMMENT '物流详情JSON',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_id` (`order_id`),
    KEY `idx_delivery_sn` (`delivery_sn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `oms_order_logistics` (`order_id`, `delivery_company`, `delivery_sn`, `status`, `detail`) VALUES
(1, '顺丰速运', 'SF1234567890', 1, '[{"time":"2026-05-20 10:00:00","desc":"已签收，签收人：本人"},{"time":"2026-05-20 08:30:00","desc":"快递员正在派送中"},{"time":"2026-05-20 06:00:00","desc":"到达【北京市朝阳区营业点】"},{"time":"2026-05-19 22:00:00","desc":"离开【上海转运中心】，发往北京"},{"time":"2026-05-19 18:00:00","desc":"【上海转运中心】已揽收"}]'),
(2, '京东物流', 'JD9876543210', 0, '[{"time":"2026-05-21 14:00:00","desc":"到达【广州市天河区营业点】"},{"time":"2026-05-21 08:00:00","desc":"离开【深圳转运中心】，发往广州"},{"time":"2026-05-20 20:00:00","desc":"【深圳转运中心】已揽收"}]');
