CREATE TABLE IF NOT EXISTS `sms_coupon` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(100) DEFAULT NULL,
    `type` tinyint DEFAULT 1,
    `amount` decimal(10,2) DEFAULT NULL,
    `min_point` decimal(10,2) DEFAULT NULL,
    `max_discount` decimal(10,2) DEFAULT NULL,
    `total_count` int DEFAULT 0,
    `remain_count` int DEFAULT 0,
    `per_limit` int DEFAULT 1,
    `start_time` datetime DEFAULT NULL,
    `end_time` datetime DEFAULT NULL,
    `use_type` tinyint DEFAULT 0,
    `status` tinyint DEFAULT 1,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_start_end` (`start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `ums_member_coupon` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `member_id` bigint DEFAULT NULL,
    `coupon_id` bigint DEFAULT NULL,
    `order_id` bigint DEFAULT NULL,
    `status` tinyint DEFAULT 0,
    `get_time` datetime DEFAULT NULL,
    `use_time` datetime DEFAULT NULL,
    `expire_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_member_id` (`member_id`),
    KEY `idx_coupon_id` (`coupon_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `sms_coupon` (`name`, `type`, `amount`, `min_point`, `max_discount`, `total_count`, `remain_count`, `per_limit`, `start_time`, `end_time`, `use_type`, `status`) VALUES
('新用户专享券', 3, 10.00, 0.00, NULL, 1000, 1000, 1, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 0, 1),
('满100减20', 1, 20.00, 100.00, NULL, 500, 500, 3, '2026-05-01 00:00:00', '2026-06-30 23:59:59', 0, 1),
('满200打8折', 2, 0.20, 200.00, 50.00, 300, 300, 2, '2026-05-01 00:00:00', '2026-07-31 23:59:59', 0, 1),
('满50减5', 1, 5.00, 50.00, NULL, 2000, 2000, 5, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 0, 1),
('限时满300减60', 1, 60.00, 300.00, NULL, 100, 100, 1, '2026-05-14 00:00:00', '2026-05-21 23:59:59', 0, 1);
