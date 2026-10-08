CREATE DATABASE IF NOT EXISTS `mall` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `mall`;

CREATE TABLE IF NOT EXISTS `pms_comment` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `product_id` bigint DEFAULT NULL,
    `member_id` bigint DEFAULT NULL,
    `member_nick_name` varchar(50) DEFAULT NULL,
    `member_avatar` varchar(255) DEFAULT NULL,
    `order_id` bigint DEFAULT NULL,
    `order_item_id` bigint DEFAULT NULL,
    `star` tinyint DEFAULT 5,
    `content` varchar(500) DEFAULT NULL,
    `pics` text DEFAULT NULL,
    `member_ip` varchar(50) DEFAULT NULL,
    `show_status` tinyint DEFAULT 1,
    `reply_count` int DEFAULT 0,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_member_id` (`member_id`),
    KEY `idx_show_status` (`show_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `pms_comment_replay` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `comment_id` bigint DEFAULT NULL,
    `content` varchar(500) DEFAULT NULL,
    `admin_id` bigint DEFAULT NULL,
    `admin_name` varchar(50) DEFAULT NULL,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_comment_id` (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `pms_comment` (`product_id`, `member_id`, `member_nick_name`, `member_avatar`, `order_id`, `order_item_id`, `star`, `content`, `pics`, `show_status`, `reply_count`) VALUES
(1, 1, '测试用户', NULL, 1, 1, 5, '商品质量很好，物流也快，非常满意！', NULL, 1, 1),
(1, 2, '张三', NULL, 2, 2, 4, '东西不错，就是包装有点简陋。', NULL, 1, 0),
(1, 3, '李四', NULL, 3, 3, 5, '第二次买了，一如既往的好！', 'url1,url2', 1, 0),
(2, 1, '测试用户', NULL, 4, 4, 3, '一般般吧，性价比还行。', NULL, 0, 0);

INSERT INTO `pms_comment_replay` (`comment_id`, `content`, `admin_id`, `admin_name`) VALUES
(1, '感谢您的好评，欢迎下次光临！', 1, 'admin');
