CREATE DATABASE IF NOT EXISTS `mall` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE `mall`;

CREATE TABLE `ums_member` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `phone` varchar(20) DEFAULT NULL,
    `password` varchar(100) DEFAULT NULL,
    `nickname` varchar(50) DEFAULT NULL,
    `avatar` varchar(255) DEFAULT NULL,
    `gender` tinyint DEFAULT 0,
    `birthday` date DEFAULT NULL,
    `status` tinyint DEFAULT 1,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `login_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `ums_member_receive_address` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `member_id` bigint DEFAULT NULL,
    `name` varchar(50) DEFAULT NULL,
    `phone` varchar(20) DEFAULT NULL,
    `province` varchar(20) DEFAULT NULL,
    `city` varchar(20) DEFAULT NULL,
    `district` varchar(20) DEFAULT NULL,
    `detail_address` varchar(200) DEFAULT NULL,
    `default_status` tinyint DEFAULT 0,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_member_id` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `pms_product` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `category_id` bigint DEFAULT NULL,
    `brand_id` bigint DEFAULT NULL,
    `name` varchar(200) DEFAULT NULL,
    `subtitle` varchar(200) DEFAULT NULL,
    `product_sn` varchar(50) DEFAULT NULL,
    `pic` varchar(255) DEFAULT NULL,
    `pics` text DEFAULT NULL,
    `price` decimal(10,2) DEFAULT NULL,
    `original_price` decimal(10,2) DEFAULT NULL,
    `cost_price` decimal(10,2) DEFAULT NULL,
    `stock` int DEFAULT 0,
    `sale` int DEFAULT 0,
    `unit` varchar(20) DEFAULT NULL,
    `description` longtext DEFAULT NULL,
    `album_pics` text DEFAULT NULL,
    `detail_title` varchar(200) DEFAULT NULL,
    `detail_desc` longtext DEFAULT NULL,
    `publish_status` tinyint DEFAULT 0,
    `new_status` tinyint DEFAULT 0,
    `recommend_status` tinyint DEFAULT 0,
    `sort` int DEFAULT 0,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_brand_id` (`brand_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `pms_product_category` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `parent_id` bigint DEFAULT 0,
    `name` varchar(50) DEFAULT NULL,
    `icon` varchar(255) DEFAULT NULL,
    `sort` int DEFAULT 0,
    `show_status` tinyint DEFAULT 1,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `pms_brand` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(50) DEFAULT NULL,
    `first_letter` varchar(1) DEFAULT NULL,
    `logo` varchar(255) DEFAULT NULL,
    `description` text DEFAULT NULL,
    `recommend_status` tinyint DEFAULT 0,
    `sort` int DEFAULT 0,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `pms_sku` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `product_id` bigint DEFAULT NULL,
    `sku_code` varchar(50) DEFAULT NULL,
    `price` decimal(10,2) DEFAULT NULL,
    `sp_data` json DEFAULT NULL,
    `pic` varchar(255) DEFAULT NULL,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `pms_sku_stock` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `sku_id` bigint DEFAULT NULL,
    `stock` int DEFAULT 0,
    `lock_stock` int DEFAULT 0,
    `low_stock` int DEFAULT 0,
    `version` int DEFAULT 1 COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    KEY `idx_sku_id` (`sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `oms_order` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `order_sn` varchar(30) DEFAULT NULL,
    `member_id` bigint DEFAULT NULL,
    `total_amount` decimal(10,2) DEFAULT NULL,
    `freight_amount` decimal(10,2) DEFAULT 0,
    `pay_amount` decimal(10,2) DEFAULT NULL,
    `pay_type` tinyint DEFAULT NULL,
    `status` tinyint DEFAULT 0,
    `receiver_name` varchar(50) DEFAULT NULL,
    `receiver_phone` varchar(20) DEFAULT NULL,
    `receiver_province` varchar(20) DEFAULT NULL,
    `receiver_city` varchar(20) DEFAULT NULL,
    `receiver_district` varchar(20) DEFAULT NULL,
    `receiver_detail_address` varchar(200) DEFAULT NULL,
    `note` varchar(200) DEFAULT NULL,
    `pay_time` datetime DEFAULT NULL,
    `delivery_time` datetime DEFAULT NULL,
    `receive_time` datetime DEFAULT NULL,
    `close_time` datetime DEFAULT NULL,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `delete_status` tinyint DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_sn` (`order_sn`),
    KEY `idx_member_id` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `oms_order_item` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `order_id` bigint DEFAULT NULL,
    `order_sn` varchar(30) DEFAULT NULL,
    `product_id` bigint DEFAULT NULL,
    `product_pic` varchar(255) DEFAULT NULL,
    `product_name` varchar(200) DEFAULT NULL,
    `product_sn` varchar(50) DEFAULT NULL,
    `product_price` decimal(10,2) DEFAULT NULL,
    `product_quantity` int DEFAULT NULL,
    `sku_id` bigint DEFAULT NULL,
    `sp_data` json DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `oms_order_operate_history` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `order_id` bigint DEFAULT NULL,
    `operate_man` varchar(50) DEFAULT NULL,
    `order_status` tinyint DEFAULT NULL,
    `note` varchar(200) DEFAULT NULL,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `oms_cart` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `member_id` bigint DEFAULT NULL,
    `product_id` bigint DEFAULT NULL,
    `sku_id` bigint DEFAULT NULL,
    `quantity` int DEFAULT 1,
    `price` decimal(10,2) DEFAULT NULL,
    `product_name` varchar(200) DEFAULT NULL,
    `product_pic` varchar(255) DEFAULT NULL,
    `sp_data` json DEFAULT NULL,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_member_id` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `sys_admin` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `username` varchar(50) DEFAULT NULL,
    `password` varchar(100) DEFAULT NULL,
    `nickname` varchar(50) DEFAULT NULL,
    `avatar` varchar(255) DEFAULT NULL,
    `email` varchar(100) DEFAULT NULL,
    `status` tinyint DEFAULT 1,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `login_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `sys_role` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(50) DEFAULT NULL,
    `description` varchar(200) DEFAULT NULL,
    `sort` int DEFAULT 0,
    `status` tinyint DEFAULT 1,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `sys_menu` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `parent_id` bigint DEFAULT 0,
    `name` varchar(50) DEFAULT NULL,
    `url` varchar(200) DEFAULT NULL,
    `component` varchar(200) DEFAULT NULL,
    `icon` varchar(50) DEFAULT NULL,
    `sort` int DEFAULT 0,
    `type` tinyint DEFAULT NULL,
    `permission` varchar(100) DEFAULT NULL,
    `status` tinyint DEFAULT 1,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `sys_role_menu` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `role_id` bigint DEFAULT NULL,
    `menu_id` bigint DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `sys_admin_role` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `admin_id` bigint DEFAULT NULL,
    `role_id` bigint DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_admin_id` (`admin_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `sys_dict` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `type` varchar(50) DEFAULT NULL,
    `code` varchar(50) DEFAULT NULL,
    `value` varchar(255) DEFAULT NULL,
    `sort` int DEFAULT 0,
    `status` tinyint DEFAULT 1,
    `description` varchar(200) DEFAULT NULL,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `sys_config` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `config_key` varchar(100) DEFAULT NULL,
    `config_value` varchar(255) DEFAULT NULL,
    `description` varchar(200) DEFAULT NULL,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `sys_operation_log` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `admin_id` bigint DEFAULT NULL,
    `admin_name` varchar(50) DEFAULT NULL,
    `operation` varchar(200) DEFAULT NULL,
    `method` varchar(200) DEFAULT NULL,
    `params` text DEFAULT NULL,
    `ip` varchar(50) DEFAULT NULL,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_admin_id` (`admin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `sys_admin` (`id`, `username`, `password`, `nickname`, `avatar`, `email`, `status`) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6CQHRosPYrHGO6VnKGK7/SBOK', '超级管理员', NULL, 'admin@mallshop.com', 1);

INSERT INTO `sys_role` (`id`, `name`, `description`, `sort`, `status`) VALUES
(1, '超级管理员', '拥有系统全部权限', 0, 1),
(2, '运营人员', '负责商品与订单管理', 1, 1);

INSERT INTO `sys_admin_role` (`admin_id`, `role_id`) VALUES
(1, 1);

INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `url`, `component`, `icon`, `sort`, `type`, `permission`, `status`) VALUES
(1, 0, '系统管理', '/system', NULL, 'setting', 0, 0, NULL, 1),
(2, 1, '管理员列表', '/system/admin', 'system/admin/index', 'user', 0, 1, 'sys:admin:list', 1),
(3, 1, '角色管理', '/system/role', 'system/role/index', 'peoples', 1, 1, 'sys:role:list', 1),
(4, 1, '菜单管理', '/system/menu', 'system/menu/index', 'tree-table', 2, 1, 'sys:menu:list', 1),
(5, 1, '字典管理', '/system/dict', 'system/dict/index', 'dict', 3, 1, 'sys:dict:list', 1),
(6, 1, '系统配置', '/system/config', 'system/config/index', 'config', 4, 1, 'sys:config:list', 1),
(7, 1, '操作日志', '/system/log', 'system/log/index', 'log', 5, 1, 'sys:log:list', 1),
(8, 0, '商品管理', '/pms', NULL, 'shop', 1, 0, NULL, 1),
(9, 8, '商品列表', '/pms/product', 'pms/product/index', 'product', 0, 1, 'pms:product:list', 1),
(10, 8, '商品分类', '/pms/category', 'pms/category/index', 'category', 1, 1, 'pms:category:list', 1),
(11, 8, '品牌管理', '/pms/brand', 'pms/brand/index', 'brand', 2, 1, 'pms:brand:list', 1),
(12, 0, '订单管理', '/oms', NULL, 'order', 2, 0, NULL, 1),
(13, 12, '订单列表', '/oms/order', 'oms/order/index', 'order-list', 0, 1, 'oms:order:list', 1),
(14, 12, '订单历史', '/oms/history', 'oms/history/index', 'history', 1, 1, 'oms:history:list', 1),
(15, 0, '会员管理', '/ums', NULL, 'member', 3, 0, NULL, 1),
(16, 15, '会员列表', '/ums/member', 'ums/member/index', 'member-list', 0, 1, 'ums:member:list', 1),
(17, 15, '收货地址', '/ums/address', 'ums/address/index', 'address', 1, 1, 'ums:address:list', 1);

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(1, 1),(1, 2),(1, 3),(1, 4),(1, 5),(1, 6),(1, 7),(1, 8),(1, 9),(1, 10),(1, 11),(1, 12),(1, 13),(1, 14),(1, 15),(1, 16),(1, 17),
(2, 8),(2, 9),(2, 10),(2, 11),(2, 12),(2, 13),(2, 14);

INSERT INTO `sys_dict` (`type`, `code`, `value`, `sort`, `status`, `description`) VALUES
('gender', '0', '未知', 0, 1, '性别'),
('gender', '1', '男', 1, 1, '性别'),
('gender', '2', '女', 2, 1, '性别'),
('order_status', '0', '待付款', 0, 1, '订单状态'),
('order_status', '1', '待发货', 1, 1, '订单状态'),
('order_status', '2', '已发货', 2, 1, '订单状态'),
('order_status', '3', '已完成', 3, 1, '订单状态'),
('order_status', '4', '已关闭', 4, 1, '订单状态'),
('order_status', '5', '已退款', 5, 1, '订单状态'),
('pay_type', '0', '未支付', 0, 1, '支付方式'),
('pay_type', '1', '支付宝', 1, 1, '支付方式'),
('pay_type', '2', '微信支付', 2, 1, '支付方式'),
('publish_status', '0', '下架', 0, 1, '商品上架状态'),
('publish_status', '1', '上架', 1, 1, '商品上架状态');

INSERT INTO `sys_config` (`config_key`, `config_value`, `description`) VALUES
('home_page_new_count', '4', '首页新品展示数量'),
('home_page_recommend_count', '4', '首页推荐展示数量'),
('order_auto_close_minutes', '30', '订单自动关闭时间(分钟)'),
('order_auto_confirm_days', '7', '订单自动确认收货天数'),
('default_freight', '10.00', '默认运费');
