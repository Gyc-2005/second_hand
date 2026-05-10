
CREATE DATABASE IF NOT EXISTS secondhand_db 
DEFAULT CHARACTER SET utf8mb4 
DEFAULT COLLATE utf8mb4_unicode_ci;

USE secondhand_db;

CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户唯一标识',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码（MD5加密）',
    phone VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像URL',
    nickname VARCHAR(50) COMMENT '昵称',
    address VARCHAR(255) COMMENT '收货地址',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_phone (phone),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

CREATE TABLE IF NOT EXISTS categories (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '分类唯一标识',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '分类名称',
    parent_id INT DEFAULT 0 COMMENT '父分类ID',
    description VARCHAR(255) COMMENT '分类描述',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

CREATE TABLE IF NOT EXISTS products (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '商品唯一标识',
    name VARCHAR(100) NOT NULL COMMENT '商品名称',
    description TEXT COMMENT '商品描述',
    price DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    original_price DECIMAL(10,2) COMMENT '原价',
    category_id INT COMMENT '分类ID',
    seller_id INT COMMENT '卖家ID',
    images TEXT COMMENT '商品图片（JSON格式）',
    status TINYINT DEFAULT 1 COMMENT '状态：1-在售，2-已卖出，3-下架',
    stock INT DEFAULT 1 COMMENT '库存数量',
    views INT DEFAULT 0 COMMENT '浏览次数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category (category_id),
    INDEX idx_seller (seller_id),
    INDEX idx_status (status),
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT fk_product_seller FOREIGN KEY (seller_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

CREATE TABLE IF NOT EXISTS orders (
    id VARCHAR(32) PRIMARY KEY COMMENT '订单编号',
    user_id INT COMMENT '买家ID',
    product_id INT COMMENT '商品ID',
    seller_id INT COMMENT '卖家ID',
    price DECIMAL(10,2) NOT NULL COMMENT '订单金额',
    quantity INT DEFAULT 1 COMMENT '购买数量',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总价',
    status TINYINT DEFAULT 1 COMMENT '状态：1-待付款，2-已付款，3-已发货，4-已完成，5-已取消',
    payment_method VARCHAR(20) COMMENT '支付方式',
    payment_time DATETIME COMMENT '支付时间',
    shipping_address VARCHAR(255) COMMENT '收货地址',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user (user_id),
    INDEX idx_seller (seller_id),
    INDEX idx_product (product_id),
    INDEX idx_status (status),
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_order_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_order_seller FOREIGN KEY (seller_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

CREATE TABLE IF NOT EXISTS reviews (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '评价唯一标识',
    order_id VARCHAR(32) COMMENT '订单ID',
    user_id INT COMMENT '评价用户ID',
    product_id INT COMMENT '商品ID',
    rating TINYINT NOT NULL COMMENT '评分（1-5星）',
    content TEXT COMMENT '评价内容',
    images TEXT COMMENT '评价图片（JSON格式）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_order (order_id),
    INDEX idx_user (user_id),
    INDEX idx_product (product_id),
    CONSTRAINT fk_review_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_review_product FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评价表';

CREATE TABLE IF NOT EXISTS messages (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '留言唯一标识',
    product_id INT COMMENT '商品ID',
    sender_id INT COMMENT '发送者ID',
    receiver_id INT COMMENT '接收者ID',
    content TEXT NOT NULL COMMENT '留言内容',
    status TINYINT DEFAULT 0 COMMENT '状态：0-未读，1-已读',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_product (product_id),
    INDEX idx_sender (sender_id),
    INDEX idx_receiver (receiver_id),
    CONSTRAINT fk_message_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_message_sender FOREIGN KEY (sender_id) REFERENCES users(id),
    CONSTRAINT fk_message_receiver FOREIGN KEY (receiver_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='留言表';

DELIMITER //

CREATE TRIGGER trg_order_update_stock AFTER UPDATE ON orders
FOR EACH ROW
BEGIN
    IF NEW.status = 2 AND OLD.status = 1 THEN
        UPDATE products 
        SET stock = stock - NEW.quantity,
            status = CASE WHEN stock - NEW.quantity <= 0 THEN 2 ELSE status END
        WHERE id = NEW.product_id;
    END IF;
END //

CREATE TRIGGER trg_order_cancel_restore_stock AFTER UPDATE ON orders
FOR EACH ROW
BEGIN
    IF NEW.status = 5 AND OLD.status = 1 THEN
        UPDATE products 
        SET stock = stock + NEW.quantity,
            status = 1
        WHERE id = NEW.product_id;
    END IF;
END //

DELIMITER ;

CREATE VIEW v_product_detail AS
SELECT 
    p.id, p.name, p.description, p.price, p.original_price,
    p.images, p.views, p.created_at,
    c.name AS category_name,
    u.nickname AS seller_name, u.phone AS seller_phone, u.avatar AS seller_avatar,
    COALESCE(AVG(r.rating), 0) AS avg_rating,
    COALESCE(COUNT(r.id), 0) AS review_count
FROM products p
LEFT JOIN categories c ON p.category_id = c.id
LEFT JOIN users u ON p.seller_id = u.id
LEFT JOIN reviews r ON p.id = r.product_id
WHERE p.status = 1
GROUP BY p.id;

CREATE VIEW v_user_orders AS
SELECT 
    o.id AS order_id, o.status, o.price, o.quantity, o.total_amount,
    o.created_at, o.payment_time, o.shipping_address,
    p.id AS product_id, p.name AS product_name, p.images AS product_images,
    s.nickname AS seller_name, s.phone AS seller_phone
FROM orders o
LEFT JOIN products p ON o.product_id = p.id
LEFT JOIN users s ON o.seller_id = s.id;

INSERT INTO categories (name, parent_id, description, sort_order) VALUES 
('数码产品', 0, '手机、电脑、相机等数码设备', 1),
('手机', 1, '智能手机', 1),
('电脑', 1, '笔记本、台式机', 2),
('相机', 1, '数码相机、单反', 3),
('生活用品', 0, '日常用品', 2),
('家具家电', 0, '家具、家用电器', 3),
('学习用品', 0, '书籍、文具等', 4),
('服装鞋帽', 0, '衣服、鞋子、帽子', 5),
('其他', 0, '其他二手商品', 6);
