CREATE DATABASE IF NOT EXISTS `mall` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `mall`;

CREATE TABLE IF NOT EXISTS `ums_member_message` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `member_id` bigint NOT NULL,
    `title` varchar(100) NOT NULL,
    `content` text DEFAULT NULL,
    `type` tinyint DEFAULT 1 COMMENT '类型 1订单 2系统 3活动',
    `is_read` tinyint DEFAULT 0 COMMENT '是否已读 0否 1是',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_member_id` (`member_id`),
    KEY `idx_is_read` (`is_read`),
    KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `ums_member_message` (`member_id`, `title`, `content`, `type`, `is_read`) VALUES
(1, '订单支付成功', '您的订单 #202605200001 已支付成功，我们将尽快为您发货。', 1, 0),
(1, '订单已发货', '您的订单 #202605200001 已发货，物流公司：顺丰速运，运单号：SF1234567890。', 1, 0),
(1, '系统维护通知', '系统将于今晚凌晨2:00-4:00进行例行维护，期间部分功能可能无法使用。', 2, 1),
(2, '订单支付成功', '您的订单 #202605200002 已支付成功，我们将尽快为您发货。', 1, 0),
(2, '新人专享活动', '欢迎加入MallShop！新用户专享满100减20优惠券已发放到您的账户。', 3, 0);
