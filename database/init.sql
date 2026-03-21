-- 海明火锅店点餐系统 数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS hotpot DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE hotpot;

-- 用户表（微信小程序用户）
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `openid` VARCHAR(64) NOT NULL COMMENT '微信openid',
  `nickname` VARCHAR(64) DEFAULT '' COMMENT '昵称',
  `avatar_url` VARCHAR(256) DEFAULT '' COMMENT '头像地址',
  `phone` VARCHAR(20) DEFAULT '' COMMENT '手机号',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_openid` (`openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 管理员表
CREATE TABLE IF NOT EXISTS `admin` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(32) NOT NULL COMMENT '登录账号',
  `password` VARCHAR(128) NOT NULL COMMENT '密码(BCrypt加密)',
  `name` VARCHAR(32) DEFAULT '' COMMENT '管理员姓名',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 菜品分类表
CREATE TABLE IF NOT EXISTS `category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(32) NOT NULL COMMENT '分类名称',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序权重(越小越靠前)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜品分类表';

-- 菜品表
CREATE TABLE IF NOT EXISTS `dish` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `category_id` BIGINT NOT NULL COMMENT '所属分类ID',
  `name` VARCHAR(64) NOT NULL COMMENT '菜品名称',
  `price` INT NOT NULL COMMENT '价格(单位:分)',
  `image` VARCHAR(256) DEFAULT '' COMMENT '图片地址',
  `description` VARCHAR(256) DEFAULT '' COMMENT '菜品描述',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:1上架 0下架',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序权重',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜品表';

-- 订单表
CREATE TABLE IF NOT EXISTS `orders` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号',
  `user_id` BIGINT NOT NULL COMMENT '下单用户ID',
  `total_amount` INT NOT NULL COMMENT '订单总金额(单位:分)',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态:0待支付 1已支付 2处理中 3已完成 4已取消',
  `table_no` VARCHAR(16) DEFAULT '' COMMENT '桌号',
  `remark` VARCHAR(256) DEFAULT '' COMMENT '备注',
  `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单明细表
CREATE TABLE IF NOT EXISTS `order_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT NOT NULL COMMENT '所属订单ID',
  `dish_id` BIGINT NOT NULL COMMENT '菜品ID',
  `dish_name` VARCHAR(64) NOT NULL COMMENT '菜品名称(冗余)',
  `dish_price` INT NOT NULL COMMENT '下单时单价(冗余,单位:分)',
  `quantity` INT NOT NULL COMMENT '数量',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- 评价表
CREATE TABLE IF NOT EXISTS `review` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT NOT NULL COMMENT '关联订单ID',
  `user_id` BIGINT NOT NULL COMMENT '评价用户ID',
  `rating` TINYINT NOT NULL COMMENT '评分(1-5)',
  `content` VARCHAR(512) DEFAULT '' COMMENT '评价内容',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

-- ===== 初始数据 =====

-- 默认管理员 (密码: admin123, BCrypt加密)
INSERT INTO `admin` (`username`, `password`, `name`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '超级管理员');

-- 默认菜品分类
INSERT INTO `category` (`name`, `sort_order`) VALUES
('锅底', 1),
('荤菜', 2),
('素菜', 3),
('丸滑类', 4),
('主食', 5),
('饮料', 6);

-- 示例菜品数据
INSERT INTO `dish` (`category_id`, `name`, `price`, `description`, `status`, `sort_order`) VALUES
(1, '麻辣红汤锅底', 5800, '经典川味麻辣锅底', 1, 1),
(1, '番茄锅底', 4800, '酸甜开胃番茄锅底', 1, 2),
(1, '菌汤锅底', 5800, '多种菌菇熬制', 1, 3),
(1, '鸳鸯锅底', 6800, '一锅两味,各取所好', 1, 4),
(2, '精品肥牛', 3800, '新鲜肥牛卷', 1, 1),
(2, '羊肉卷', 3800, '内蒙古羊肉卷', 1, 2),
(2, '鲜切牛肉', 4800, '现切新鲜牛肉', 1, 3),
(2, '耗儿鱼', 2800, '香辣耗儿鱼', 1, 4),
(2, '鲜鸭肠', 2800, '新鲜鸭肠', 1, 5),
(2, '毛肚', 3800, '鲜毛肚', 1, 6),
(3, '土豆片', 1200, '新鲜土豆切片', 1, 1),
(3, '藕片', 1200, '脆嫩藕片', 1, 2),
(3, '娃娃菜', 1200, '新鲜娃娃菜', 1, 3),
(3, '冬瓜', 1000, '新鲜冬瓜', 1, 4),
(3, '金针菇', 1200, '新鲜金针菇', 1, 5),
(3, '豆腐', 800, '嫩豆腐', 1, 6),
(4, '牛肉丸', 2800, '手打牛肉丸', 1, 1),
(4, '虾滑', 2800, '鲜虾滑', 1, 2),
(4, '鱼丸', 1800, '鲜鱼丸', 1, 3),
(5, '手工面', 1200, '手工拉面', 1, 1),
(5, '红薯粉', 1000, '正宗红薯粉', 1, 2),
(6, '王老吉', 800, '310ml', 1, 1),
(6, '酸梅汤', 1000, '自制酸梅汤', 1, 2),
(6, '豆浆', 600, '现磨豆浆', 1, 3);
