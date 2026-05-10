
# 二手商品交易系统 - 数据库设计文档

## 一、数据库概述

本系统采用 MySQL 数据库管理系统，数据库名称为 `secondhand_db`，用于存储二手商品交易系统的所有业务数据。

## 二、数据库创建语句

```sql
CREATE DATABASE IF NOT EXISTS secondhand_db 
DEFAULT CHARACTER SET utf8mb4 
DEFAULT COLLATE utf8mb4_unicode_ci;

USE secondhand_db;
```

## 三、数据表设计

### 3.1 用户表 (users)

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| id | INT | PRIMARY KEY, AUTO_INCREMENT | 用户唯一标识 |
| username | VARCHAR(50) | NOT NULL, UNIQUE | 用户名 |
| password | VARCHAR(100) | NOT NULL | 密码（MD5加密） |
| phone | VARCHAR(20) | NOT NULL, UNIQUE | 手机号 |
| email | VARCHAR(100) | UNIQUE | 邮箱 |
| avatar | VARCHAR(255) | | 头像URL |
| nickname | VARCHAR(50) | | 昵称 |
| address | VARCHAR(255) | | 收货地址 |
| status | TINYINT | DEFAULT 1 | 状态：1-正常，0-禁用 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**建表SQL：**

```sql
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户唯一标识',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码（MD5加密）',
    phone VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像URL',
    nickname VARCHAR(50) COMMENT '昵称',
    address VARCHAR(255) COMMENT '收货地址',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
```

### 3.2 商品分类表 (categories)

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| id | INT | PRIMARY KEY, AUTO_INCREMENT | 分类唯一标识 |
| name | VARCHAR(50) | NOT NULL, UNIQUE | 分类名称 |
| parent_id | INT | DEFAULT 0 | 父分类ID |
| description | VARCHAR(255) | | 分类描述 |
| sort_order | INT | DEFAULT 0 | 排序号 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

**建表SQL：**

```sql
CREATE TABLE IF NOT EXISTS categories (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '分类唯一标识',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '分类名称',
    parent_id INT DEFAULT 0 COMMENT '父分类ID',
    description VARCHAR(255) COMMENT '分类描述',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';
```

### 3.3 商品表 (products)

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| id | INT | PRIMARY KEY, AUTO_INCREMENT | 商品唯一标识 |
| name | VARCHAR(100) | NOT NULL | 商品名称 |
| description | TEXT | | 商品描述 |
| price | DECIMAL(10,2) | NOT NULL | 商品价格 |
| original_price | DECIMAL(10,2) | | 原价 |
| category_id | INT | FOREIGN KEY | 分类ID |
| seller_id | INT | FOREIGN KEY | 卖家ID |
| images | TEXT | | 商品图片（JSON格式） |
| status | TINYINT | DEFAULT 1 | 状态：1-在售，2-已卖出，3-下架 |
| stock | INT | DEFAULT 1 | 库存数量 |
| views | INT | DEFAULT 0 | 浏览次数 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**建表SQL：**

```sql
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
```

### 3.4 订单表 (orders)

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| id | VARCHAR(32) | PRIMARY KEY | 订单编号 |
| user_id | INT | FOREIGN KEY | 买家ID |
| product_id | INT | FOREIGN KEY | 商品ID |
| seller_id | INT | FOREIGN KEY | 卖家ID |
| price | DECIMAL(10,2) | NOT NULL | 订单金额 |
| quantity | INT | DEFAULT 1 | 购买数量 |
| total_amount | DECIMAL(10,2) | NOT NULL | 订单总价 |
| status | TINYINT | DEFAULT 1 | 状态：1-待付款，2-已付款，3-已发货，4-已完成，5-已取消 |
| payment_method | VARCHAR(20) | | 支付方式 |
| payment_time | DATETIME | | 支付时间 |
| shipping_address | VARCHAR(255) | | 收货地址 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**建表SQL：**

```sql
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
```

### 3.5 评价表 (reviews)

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| id | INT | PRIMARY KEY, AUTO_INCREMENT | 评价唯一标识 |
| order_id | VARCHAR(32) | FOREIGN KEY | 订单ID |
| user_id | INT | FOREIGN KEY | 评价用户ID |
| product_id | INT | FOREIGN KEY | 商品ID |
| rating | TINYINT | NOT NULL | 评分（1-5星） |
| content | TEXT | | 评价内容 |
| images | TEXT | | 评价图片（JSON格式） |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

**建表SQL：**

```sql
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
```

### 3.6 留言表 (messages)

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| id | INT | PRIMARY KEY, AUTO_INCREMENT | 留言唯一标识 |
| product_id | INT | FOREIGN KEY | 商品ID |
| sender_id | INT | FOREIGN KEY | 发送者ID |
| receiver_id | INT | FOREIGN KEY | 接收者ID |
| content | TEXT | NOT NULL | 留言内容 |
| status | TINYINT | DEFAULT 0 | 状态：0-未读，1-已读 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

**建表SQL：**

```sql
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
```

## 四、E-R图结构说明

### 4.1 实体关系

```
用户(users) 1:n 商品(products)     -- 一个用户可以发布多个商品
用户(users) 1:n 订单(orders)       -- 一个用户可以购买多个商品
用户(users) 1:n 评价(reviews)      -- 一个用户可以发表多个评价
用户(users) 1:n 留言(messages)     -- 一个用户可以发送多条留言
分类(categories) 1:n 商品(products) -- 一个分类可以包含多个商品
商品(products) 1:n 订单(orders)    -- 一个商品可以被多次购买
商品(products) 1:n 评价(reviews)   -- 一个商品可以有多个评价
商品(products) 1:n 留言(messages)  -- 一个商品可以有多条留言
订单(orders) 1:1 评价(reviews)     -- 一个订单可以有一条评价
```

### 4.2 E-R图符号说明

| 符号 | 含义 |
| :--- | :--- |
| 矩形 | 实体（表） |
| 椭圆形 | 属性（字段） |
| 菱形 | 关系 |
| 直线 | 连接关系 |

## 五、核心SQL语句

### 5.1 用户管理

**查询用户列表：**
```sql
SELECT id, username, nickname, phone, email, status, created_at 
FROM users 
WHERE status = 1 
ORDER BY created_at DESC;
```

**根据ID查询用户：**
```sql
SELECT id, username, nickname, phone, email, avatar, address, status 
FROM users 
WHERE id = ?;
```

**用户登录验证：**
```sql
SELECT id, username, password, nickname, status 
FROM users 
WHERE (username = ? OR phone = ?) AND status = 1;
```

**添加用户：**
```sql
INSERT INTO users (username, password, phone, email, nickname, avatar) 
VALUES (?, ?, ?, ?, ?, ?);
```

**更新用户信息：**
```sql
UPDATE users 
SET nickname = ?, phone = ?, email = ?, address = ?, avatar = ?, updated_at = NOW() 
WHERE id = ?;
```

**删除用户（软删除）：**
```sql
UPDATE users SET status = 0, updated_at = NOW() WHERE id = ?;
```

### 5.2 商品管理

**查询商品列表（带分页）：**
```sql
SELECT p.*, c.name AS category_name, u.nickname AS seller_name 
FROM products p
LEFT JOIN categories c ON p.category_id = c.id
LEFT JOIN users u ON p.seller_id = u.id
WHERE p.status = 1
ORDER BY p.created_at DESC
LIMIT ?, ?;
```

**根据分类查询商品：**
```sql
SELECT p.*, c.name AS category_name 
FROM products p
LEFT JOIN categories c ON p.category_id = c.id
WHERE p.category_id = ? AND p.status = 1
ORDER BY p.created_at DESC;
```

**搜索商品：**
```sql
SELECT p.*, c.name AS category_name 
FROM products p
LEFT JOIN categories c ON p.category_id = c.id
WHERE p.status = 1 
AND (p.name LIKE ? OR p.description LIKE ?)
ORDER BY p.created_at DESC;
```

**根据ID查询商品详情：**
```sql
SELECT p.*, c.name AS category_name, u.nickname AS seller_name, u.phone AS seller_phone 
FROM products p
LEFT JOIN categories c ON p.category_id = c.id
LEFT JOIN users u ON p.seller_id = u.id
WHERE p.id = ?;
```

**添加商品：**
```sql
INSERT INTO products (name, description, price, original_price, category_id, seller_id, images, stock) 
VALUES (?, ?, ?, ?, ?, ?, ?, ?);
```

**更新商品库存和状态：**
```sql
UPDATE products 
SET stock = stock - ?, 
    status = CASE WHEN stock - ? <= 0 THEN 2 ELSE status END,
    updated_at = NOW() 
WHERE id = ? AND stock >= ?;
```

**更新商品浏览量：**
```sql
UPDATE products SET views = views + 1 WHERE id = ?;
```

### 5.3 订单管理

**创建订单：**
```sql
INSERT INTO orders (id, user_id, product_id, seller_id, price, quantity, total_amount, shipping_address) 
VALUES (?, ?, ?, ?, ?, ?, ?, ?);
```

**支付订单：**
```sql
UPDATE orders 
SET status = 2, payment_method = ?, payment_time = NOW(), updated_at = NOW() 
WHERE id = ? AND status = 1;
```

**发货订单：**
```sql
UPDATE orders 
SET status = 3, updated_at = NOW() 
WHERE id = ? AND status = 2;
```

**完成订单：**
```sql
UPDATE orders 
SET status = 4, updated_at = NOW() 
WHERE id = ? AND status = 3;
```

**取消订单：**
```sql
UPDATE orders 
SET status = 5, updated_at = NOW() 
WHERE id = ? AND status = 1;
```

**查询用户订单列表：**
```sql
SELECT o.*, p.name AS product_name, p.images AS product_images, u.nickname AS seller_name 
FROM orders o
LEFT JOIN products p ON o.product_id = p.id
LEFT JOIN users u ON o.seller_id = u.id
WHERE o.user_id = ?
ORDER BY o.created_at DESC;
```

### 5.4 评价管理

**添加评价：**
```sql
INSERT INTO reviews (order_id, user_id, product_id, rating, content, images) 
VALUES (?, ?, ?, ?, ?, ?);
```

**查询商品评价列表：**
```sql
SELECT r.*, u.nickname AS user_name, u.avatar AS user_avatar 
FROM reviews r
LEFT JOIN users u ON r.user_id = u.id
WHERE r.product_id = ?
ORDER BY r.created_at DESC;
```

**查询商品平均评分：**
```sql
SELECT AVG(rating) AS avg_rating, COUNT(*) AS review_count 
FROM reviews 
WHERE product_id = ?;
```

### 5.5 留言管理

**发送留言：**
```sql
INSERT INTO messages (product_id, sender_id, receiver_id, content) 
VALUES (?, ?, ?, ?);
```

**查询商品留言：**
```sql
SELECT m.*, u.nickname AS sender_name, u.avatar AS sender_avatar 
FROM messages m
LEFT JOIN users u ON m.sender_id = u.id
WHERE m.product_id = ?
ORDER BY m.created_at DESC;
```

**查询用户未读留言数量：**
```sql
SELECT COUNT(*) AS unread_count 
FROM messages 
WHERE receiver_id = ? AND status = 0;
```

**标记留言为已读：**
```sql
UPDATE messages SET status = 1 WHERE receiver_id = ? AND status = 0;
```

## 六、视图设计

### 6.1 商品详情视图 (v_product_detail)

```sql
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
```

### 6.2 用户订单视图 (v_user_orders)

```sql
CREATE VIEW v_user_orders AS
SELECT 
    o.id AS order_id, o.status, o.price, o.quantity, o.total_amount,
    o.created_at, o.payment_time, o.shipping_address,
    p.id AS product_id, p.name AS product_name, p.images AS product_images,
    s.nickname AS seller_name, s.phone AS seller_phone
FROM orders o
LEFT JOIN products p ON o.product_id = p.id
LEFT JOIN users s ON o.seller_id = s.id;
```

## 七、触发器设计

### 7.1 订单完成后更新商品库存触发器

```sql
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
DELIMITER ;
```

### 7.2 订单取消后恢复商品库存触发器

```sql
DELIMITER //
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
```

## 八、事务设计

### 8.1 创建订单事务

```sql
START TRANSACTION;

INSERT INTO orders (id, user_id, product_id, seller_id, price, quantity, total_amount, shipping_address) 
VALUES (?, ?, ?, ?, ?, ?, ?, ?);

UPDATE products 
SET stock = stock - ?,
    status = CASE WHEN stock - ? <= 0 THEN 2 ELSE status END
WHERE id = ? AND stock >= ?;

IF ROW_COUNT() = 0 THEN
    ROLLBACK;
ELSE
    COMMIT;
END IF;
```

### 8.2 支付订单事务

```sql
START TRANSACTION;

UPDATE orders 
SET status = 2, payment_method = ?, payment_time = NOW() 
WHERE id = ? AND status = 1;

IF ROW_COUNT() = 0 THEN
    ROLLBACK;
ELSE
    COMMIT;
END IF;
```

## 九、索引优化建议

| 表名 | 索引名称 | 索引字段 | 说明 |
| :--- | :--- | :--- | :--- |
| users | idx_username | username | 加速用户名查询 |
| users | idx_phone | phone | 加速手机号查询 |
| products | idx_category | category_id | 加速分类查询 |
| products | idx_seller | seller_id | 加速卖家商品查询 |
| products | idx_status | status | 加速状态筛选 |
| orders | idx_user | user_id | 加速用户订单查询 |
| orders | idx_seller | seller_id | 加速卖家订单查询 |
| orders | idx_status | status | 加速状态筛选 |
| reviews | idx_product | product_id | 加速商品评价查询 |
| messages | idx_receiver | receiver_id | 加速收件人查询 |
| messages | idx_status | status | 加速未读消息查询 |

## 十、数据字典

### 10.1 用户状态 (user_status)

| 状态码 | 状态名称 | 说明 |
| :--- | :--- | :--- |
| 1 | 正常 | 用户可正常使用 |
| 0 | 禁用 | 用户被禁用 |

### 10.2 商品状态 (product_status)

| 状态码 | 状态名称 | 说明 |
| :--- | :--- | :--- |
| 1 | 在售 | 商品可购买 |
| 2 | 已卖出 | 商品已售出 |
| 3 | 下架 | 商品下架 |

### 10.3 订单状态 (order_status)

| 状态码 | 状态名称 | 说明 |
| :--- | :--- | :--- |
| 1 | 待付款 | 订单创建，等待付款 |
| 2 | 已付款 | 已完成支付 |
| 3 | 已发货 | 卖家已发货 |
| 4 | 已完成 | 交易完成 |
| 5 | 已取消 | 订单取消 |

### 10.4 评价评分 (review_rating)

| 评分值 | 说明 |
| :--- | :--- |
| 1 | 1星（差） |
| 2 | 2星（较差） |
| 3 | 3星（一般） |
| 4 | 4星（良好） |
| 5 | 5星（优秀） |

---

**文档版本**: v1.0  
**创建日期**: 2024年  
**适用系统**: 二手商品交易系统
