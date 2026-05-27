-- 拼豆商城数据库初始化脚本
CREATE DATABASE IF NOT EXISTS pindou_mall DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE pindou_mall;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码(MD5)',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(255) COMMENT '头像',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    role VARCHAR(20) DEFAULT 'USER' COMMENT '角色: USER/ADMIN',
    status INT DEFAULT 1 COMMENT '状态: 1启用 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 商品分类表
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    icon VARCHAR(255) COMMENT '分类图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status INT DEFAULT 1 COMMENT '状态: 1启用 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 商品表
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL COMMENT '商品名称',
    category_id BIGINT COMMENT '分类ID',
    price DECIMAL(10,2) NOT NULL COMMENT '售价',
    original_price DECIMAL(10,2) COMMENT '原价',
    stock INT DEFAULT 0 COMMENT '库存',
    sales INT DEFAULT 0 COMMENT '销量',
    main_image VARCHAR(255) COMMENT '主图',
    images TEXT COMMENT '商品图片(多张,逗号分隔)',
    description VARCHAR(500) COMMENT '简介',
    detail LONGTEXT COMMENT '详情(富文本)',
    ip_series VARCHAR(100) COMMENT 'IP系列',
    character_name VARCHAR(100) COMMENT '角色名称',
    status INT DEFAULT 1 COMMENT '状态: 1上架 0下架',
    is_hot INT DEFAULT 0 COMMENT '是否热门',
    is_new INT DEFAULT 0 COMMENT '是否新品',
    view_count INT DEFAULT 0 COMMENT '浏览量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category_id),
    INDEX idx_ip_series (ip_series)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 购物车表
CREATE TABLE IF NOT EXISTS carts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    quantity INT DEFAULT 1 COMMENT '数量',
    checked INT DEFAULT 1 COMMENT '是否选中',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(30) NOT NULL UNIQUE COMMENT '订单号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '总金额',
    status INT DEFAULT 0 COMMENT '状态: 0待付款 1已付款 2已发货 3已完成 4已取消',
    address VARCHAR(500) COMMENT '收货地址',
    phone VARCHAR(20) COMMENT '联系电话',
    receiver VARCHAR(50) COMMENT '收货人',
    remark VARCHAR(500) COMMENT '备注',
    pay_time DATETIME COMMENT '支付时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    INDEX idx_order_no (order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单详情表
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL COMMENT '订单ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    product_name VARCHAR(200) COMMENT '商品名称',
    product_image VARCHAR(255) COMMENT '商品图片',
    price DECIMAL(10,2) COMMENT '单价',
    quantity INT COMMENT '数量',
    INDEX idx_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单详情表';

-- 评价表
CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    order_id BIGINT COMMENT '订单ID',
    content TEXT COMMENT '评价内容',
    rating INT DEFAULT 5 COMMENT '评分(1-5)',
    images TEXT COMMENT '评价图片',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

-- 轮播图表
CREATE TABLE IF NOT EXISTS banners (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) COMMENT '标题',
    image VARCHAR(255) NOT NULL COMMENT '图片',
    url VARCHAR(255) COMMENT '链接',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status INT DEFAULT 1 COMMENT '状态: 1启用 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播图表';

-- 收货地址表
CREATE TABLE IF NOT EXISTS addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    receiver VARCHAR(50) COMMENT '收货人',
    phone VARCHAR(20) COMMENT '电话',
    province VARCHAR(50) COMMENT '省',
    city VARCHAR(50) COMMENT '市',
    district VARCHAR(50) COMMENT '区',
    detail VARCHAR(255) COMMENT '详细地址',
    is_default INT DEFAULT 0 COMMENT '是否默认',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

-- ========== 初始数据 ==========

-- 管理员账号 (密码: 123456)
INSERT INTO users (username, password, nickname, role, status) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', '管理员', 'ADMIN', 1);

-- 测试用户 (密码: 123456)
INSERT INTO users (username, password, nickname, role, status) VALUES
('user1', 'e10adc3949ba59abbe56e057f20f883e', '拼豆爱好者', 'USER', 1),
('user2', 'e10adc3949ba59abbe56e057f20f883e', '二次元少女', 'USER', 1);

-- 商品分类
INSERT INTO categories (name, icon, sort_order) VALUES
('动漫角色', '🎭', 1),
('游戏IP', '🎮', 2),
('卡通形象', '🐱', 3),
('风景建筑', '🏰', 4),
('节日主题', '🎄', 5),
('原创设计', '✨', 6),
('拼豆工具', '🔧', 7),
('拼豆材料', '📦', 8);

-- 商品数据
INSERT INTO products (name, category_id, price, original_price, stock, sales, main_image, description, ip_series, character_name, status, is_hot, is_new, view_count) VALUES
('海贼王路飞拼豆套装', 1, 29.90, 39.90, 100, 256, 'https://images.unsplash.com/photo-1608889175123-8ee362201f81?w=400&h=400&fit=crop', '经典海贼王路飞造型，包含所有颜色拼豆和图纸', '海贼王', '路飞', 1, 1, 0, 1520),
('火影忍者鸣人拼豆套装', 1, 32.90, 42.90, 80, 198, 'https://images.unsplash.com/photo-1594736797933-d0501ba2fe65?w=400&h=400&fit=crop', '火影忍者鸣人经典造型，含仙人模式图纸', '火影忍者', '鸣人', 1, 1, 0, 1280),
('初音未来拼豆大图', 1, 45.90, 59.90, 60, 312, 'https://images.unsplash.com/photo-1558618666-fcd25c85f82e?w=400&h=400&fit=crop', '初音未来经典造型大图拼豆套装，约30cm', 'VOCALOID', '初音未来', 1, 1, 1, 2100),
('原神可莉拼豆套装', 2, 35.90, 45.90, 90, 175, 'https://images.unsplash.com/photo-1566576912321-d58ddd7a6088?w=400&h=400&fit=crop', '原神可莉Q版造型拼豆套装', '原神', '可莉', 1, 1, 1, 980),
('原神钟离拼豆套装', 2, 38.90, 48.90, 70, 143, 'https://images.unsplash.com/photo-1612036782180-6f0b6cd846fe?w=400&h=400&fit=crop', '原神钟离帅气造型拼豆套装', '原神', '钟离', 1, 0, 1, 860),
('马里奥拼豆经典套装', 3, 19.90, 29.90, 150, 425, 'https://images.unsplash.com/photo-1551103782-8ab07afd45c1?w=400&h=400&fit=crop', '超级马里奥经典像素造型，新手入门推荐', '超级马里奥', '马里奥', 1, 1, 0, 3200),
('皮卡丘拼豆套装', 3, 22.90, 32.90, 120, 380, 'https://images.unsplash.com/photo-1613771404784-3a5686aa2be3?w=400&h=400&fit=crop', '宝可梦皮卡丘可爱造型拼豆', '宝可梦', '皮卡丘', 1, 1, 0, 2800),
('千与千寻场景拼豆', 4, 68.90, 88.90, 40, 89, 'https://images.unsplash.com/photo-1578632767115-351597cf2477?w=400&h=400&fit=crop', '宫崎骏千与千寻经典场景大图拼豆', '千与千寻', '千寻', 1, 0, 1, 650),
('圣诞主题拼豆套装', 5, 25.90, 35.90, 100, 156, 'https://images.unsplash.com/photo-1543589077-47d81606c1bf?w=400&h=400&fit=crop', '包含圣诞老人、雪人、圣诞树等多款图纸', '圣诞节', '圣诞系列', 1, 0, 0, 420),
('樱花猫咪原创拼豆', 6, 18.90, 25.90, 200, 267, 'https://images.unsplash.com/photo-1574158622682-e40e69881006?w=400&h=400&fit=crop', '原创设计樱花猫咪造型，清新可爱', '原创', '樱花猫', 1, 1, 1, 1560),
('5mm标准拼豆板(方形)', 7, 8.90, 12.90, 500, 890, 'https://images.unsplash.com/photo-1611532736597-de2d4265fba3?w=400&h=400&fit=crop', '标准5mm方形拼豆板，透明耐高温', '工具', '拼豆板', 1, 0, 0, 4500),
('24色拼豆基础套装', 8, 15.90, 22.90, 300, 678, 'https://images.unsplash.com/photo-1513364776144-60967b0f800f?w=400&h=400&fit=crop', '24色基础拼豆珠子套装，每色约200颗', '材料', '基础色', 1, 1, 0, 3800),
('48色拼豆进阶套装', 8, 28.90, 38.90, 200, 445, 'https://images.unsplash.com/photo-1502691876148-a84978e59af8?w=400&h=400&fit=crop', '48色进阶拼豆珠子套装，丰富色彩选择', '材料', '进阶色', 1, 1, 0, 2900),
('拼豆专用熨斗', 7, 35.90, 49.90, 100, 234, 'https://images.unsplash.com/photo-1558618666-fcd25c85f82e?w=400&h=400&fit=crop', '拼豆专用小型熨斗，温度可调，安全便携', '工具', '熨斗', 1, 0, 0, 1800),
('星之卡比拼豆套装', 3, 16.90, 24.90, 130, 289, 'https://images.unsplash.com/photo-1596854407944-bf87f6fdd49e?w=400&h=400&fit=crop', '星之卡比粉嫩可爱造型拼豆', '星之卡比', '卡比', 1, 0, 1, 1200);

-- 轮播图
INSERT INTO banners (title, image, url, sort_order) VALUES
('新品上线 - 原神系列', 'https://images.unsplash.com/photo-1509281373149-e957c6296406?w=1200&h=400&fit=crop', '/products?category=2', 1),
('热门推荐 - 动漫角色', 'https://images.unsplash.com/photo-1560393464-5c69a73c5770?w=1200&h=400&fit=crop', '/products?category=1', 2),
('拼豆入门套装特惠', 'https://images.unsplash.com/photo-1513542789411-b6a5d4f31634?w=1200&h=400&fit=crop', '/products?category=8', 3);
