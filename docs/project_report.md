
# 二手商品交易系统 - 课程设计报告

---

## 目录

1. [系统简介](#一、系统简介)
2. [编写目的](#二、编写目的)
3. [需求分析](#三、需求分析)
4. [功能模块](#四、功能模块)
5. [技术实现](#五、技术实现)
6. [数据库展示](#六、数据库展示)
7. [小组分工](#七、小组分工)
8. [组员收获与合作总结](#八、组员收获与合作总结)
9. [附录](#九、附录)

---

## 一、系统简介

### 1.1 项目背景

随着校园二手交易需求的日益增长，传统的线下交易方式已难以满足学生和教职工的需求。本系统旨在开发一个面向校园/本地用户的二手商品交易平台，为用户提供便捷、安全、高效的二手商品交易服务。

### 1.2 系统定位

- **目标用户**: 校园学生、教职工及周边居民
- **核心价值**: 促进二手商品流通，践行绿色环保理念
- **功能定位**: 二手商品发布、浏览、购买、交易管理的一站式平台

### 1.3 系统特点

| 特点 | 描述 |
| :--- | :--- |
| 便捷性 | 简洁的操作界面，快速发布和搜索商品 |
| 安全性 | 用户认证、订单追踪、交易保障 |
| 互动性 | 商品评价、留言沟通功能 |
| 本地化 | 针对校园场景优化，支持当面交易 |

---

## 二、编写目的

### 2.1 文档目的

本报告旨在全面记录二手商品交易系统的开发过程，包括需求分析、系统设计、技术实现、数据库设计等内容，为课程设计提交提供完整的项目文档。

### 2.2 适用范围

- 《数据库分析与设计实习》课程设计提交材料
- 项目开发团队内部参考文档
- 系统维护和扩展参考资料

### 2.3 预期读者

- 课程指导老师
- 项目开发团队成员
- 系统维护人员

---

## 三、需求分析

### 3.1 业务需求

#### 3.1.1 用户需求

| 需求编号 | 需求描述 | 来源 |
| :--- | :--- | :--- |
| UR-001 | 用户可以注册账号 | 调研问卷 |
| UR-002 | 用户可以登录系统 | 调研问卷 |
| UR-003 | 用户可以修改个人信息 | 调研问卷 |
| UR-004 | 用户可以发布二手商品 | 核心需求 |
| UR-005 | 用户可以浏览商品列表 | 核心需求 |
| UR-006 | 用户可以搜索商品 | 核心需求 |
| UR-007 | 用户可以查看商品详情 | 核心需求 |
| UR-008 | 用户可以购买商品 | 核心需求 |
| UR-009 | 用户可以管理订单 | 核心需求 |
| UR-010 | 用户可以评价商品 | 增值需求 |
| UR-011 | 用户可以留言沟通 | 增值需求 |

#### 3.1.2 功能需求

| 功能模块 | 功能点 | 需求描述 |
| :--- | :--- | :--- |
| 用户模块 | 用户注册 | 提供用户名、密码、手机号完成注册 |
| 用户模块 | 用户登录 | 支持用户名/手机号+密码登录 |
| 用户模块 | 信息管理 | 修改昵称、手机号、邮箱、地址等 |
| 商品模块 | 商品发布 | 填写商品信息并上传图片 |
| 商品模块 | 商品分类 | 按分类浏览商品 |
| 商品模块 | 商品搜索 | 按关键词搜索商品 |
| 商品模块 | 商品详情 | 展示商品详细信息 |
| 交易模块 | 创建订单 | 提交订单购买商品 |
| 交易模块 | 订单支付 | 模拟支付功能 |
| 交易模块 | 订单管理 | 查看订单状态、取消订单 |
| 评价模块 | 商品评价 | 购买后对商品进行评价 |
| 评价模块 | 留言互动 | 与卖家沟通交流 |

### 3.2 非功能需求

#### 3.2.1 性能需求

| 需求 | 描述 |
| :--- | :--- |
| 响应时间 | 页面加载时间≤2秒 |
| 并发处理 | 支持≥100并发用户 |
| 数据存储 | 支持≥10万条商品数据 |

#### 3.2.2 安全性需求

| 需求 | 描述 |
| :--- | :--- |
| 密码加密 | 用户密码采用MD5加密存储 |
| 会话管理 | 使用Session管理用户登录状态 |
| 数据验证 | 前后端双重数据校验 |

---

## 四、功能模块

### 4.1 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                      前端展示层                           │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐      │
│  │ 首页模块 │ │ 商品模块 │ │ 用户模块 │ │ 订单模块 │      │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘      │
└────────────────────┬────────────────────────────────────────┘
                     │ API调用
┌────────────────────▼────────────────────────────────────────┐
│                      业务逻辑层                           │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐          │
│  │ UserService │ │ProductService│ │OrderService │          │
│  └─────────────┘ └─────────────┘ └─────────────┘          │
│  ┌─────────────┐ ┌─────────────┐                         │
│  │ReviewService│ │MessageService│                        │
│  └─────────────┘ └─────────────┘                         │
└────────────────────┬────────────────────────────────────────┘
                     │ 数据访问
┌────────────────────▼────────────────────────────────────────┐
│                      数据访问层                           │
│  ┌───────────────────────────────────────────────────┐     │
│  │   Repository层 + JPA + MySQL数据库                │     │
│  └───────────────────────────────────────────────────┘     │
└─────────────────────────────────────────────────────────────┘
```

### 4.2 模块详细设计

#### 4.2.1 用户模块

| 类名 | 功能说明 | 所属文件 |
| :--- | :--- | :--- |
| UserController | 用户相关API控制层 | controller/UserController.java |
| UserService | 用户业务逻辑接口 | service/UserService.java |
| UserServiceImpl | 用户业务逻辑实现 | service/impl/UserServiceImpl.java |
| UserRepository | 用户数据访问接口 | repository/UserRepository.java |
| User | 用户实体类 | entity/User.java |

**核心功能**:
- 注册: 验证用户名/手机号唯一性，加密密码存储
- 登录: 验证用户名/手机号和密码，创建Session
- 更新: 修改个人信息，验证数据唯一性
- 删除: 软删除，设置用户状态为禁用

#### 4.2.2 商品模块

| 类名 | 功能说明 | 所属文件 |
| :--- | :--- | :--- |
| ProductController | 商品相关API控制层 | controller/ProductController.java |
| ProductService | 商品业务逻辑接口 | service/ProductService.java |
| ProductServiceImpl | 商品业务逻辑实现 | service/impl/ProductServiceImpl.java |
| ProductRepository | 商品数据访问接口 | repository/ProductRepository.java |
| Product | 商品实体类 | entity/Product.java |

**核心功能**:
- 发布商品: 关联卖家ID，存储商品信息
- 商品列表: 支持分页、分类、关键词搜索
- 商品详情: 浏览量自动递增
- 商品管理: 更新、下架商品

#### 4.2.3 交易模块

| 类名 | 功能说明 | 所属文件 |
| :--- | :--- | :--- |
| OrderController | 订单相关API控制层 | controller/OrderController.java |
| OrderService | 订单业务逻辑接口 | service/OrderService.java |
| OrderServiceImpl | 订单业务逻辑实现 | service/impl/OrderServiceImpl.java |
| OrderRepository | 订单数据访问接口 | repository/OrderRepository.java |
| Order | 订单实体类 | entity/Order.java |

**核心功能**:
- 创建订单: 生成订单号，扣减库存
- 订单支付: 修改订单状态，记录支付方式
- 订单发货: 卖家确认发货
- 订单完成: 买家确认收货
- 订单取消: 恢复库存

#### 4.2.4 评价模块

| 类名 | 功能说明 | 所属文件 |
| :--- | :--- | :--- |
| ReviewController | 评价相关API控制层 | controller/ReviewController.java |
| ReviewService | 评价业务逻辑接口 | service/ReviewService.java |
| ReviewServiceImpl | 评价业务逻辑实现 | service/impl/ReviewServiceImpl.java |
| ReviewRepository | 评价数据访问接口 | repository/ReviewRepository.java |
| Review | 评价实体类 | entity/Review.java |

**核心功能**:
- 添加评价: 订单完成后可评价
- 查看评价: 按商品或用户查询
- 评分统计: 计算商品平均评分

#### 4.2.5 留言模块

| 类名 | 功能说明 | 所属文件 |
| :--- | :--- | :--- |
| MessageController | 留言相关API控制层 | controller/MessageController.java |
| MessageService | 留言业务逻辑接口 | service/MessageService.java |
| MessageServiceImpl | 留言业务逻辑实现 | service/impl/MessageServiceImpl.java |
| MessageRepository | 留言数据访问接口 | repository/MessageRepository.java |
| Message | 留言实体类 | entity/Message.java |

**核心功能**:
- 发送留言: 买家向卖家发送咨询
- 查看留言: 按商品或接收者查询
- 标记已读: 批量标记未读消息为已读

---

## 五、技术实现

### 5.1 技术栈

| 层次 | 技术 | 版本 | 选型原因 |
| :--- | :--- | :--- | :--- |
| 前端 | HTML5 | - | 标准网页结构，兼容性好 |
| 前端 | CSS3 | - | 样式美化，响应式布局 |
| 前端 | JavaScript | ES6+ | 交互逻辑实现 |
| 后端 | Java | 21 | LTS版本，性能稳定，生态成熟 |
| 后端 | Spring Boot | 3.2.0 | 社区成熟，快速构建RESTful服务 |
| 后端 | Spring Data JPA | 3.2.0 | ORM框架，简化数据访问层开发 |
| 数据库 | MySQL | 8.0+ | 开源稳定，适合中小型项目 |

### 5.2 关键技术实现

#### 5.2.1 用户认证

```
登录流程:
1. 用户输入用户名/手机号和密码
2. 后端验证用户存在且密码正确
3. 创建HttpSession存储用户ID和用户名
4. 返回用户信息给前端
5. 前端存储登录状态，展示用户信息
```

**安全性措施**:
- 密码使用MD5加密存储
- Session设置超时时间
- 登录失败次数限制（可扩展）

#### 5.2.2 订单创建

```
订单创建流程:
1. 用户点击购买商品
2. 验证商品库存充足
3. 生成唯一订单号
4. 创建订单记录
5. 扣减商品库存
6. 更新商品状态（如库存为0则标记为已卖出）
```

**事务管理**:
- 使用@Transactional保证数据一致性
- 订单创建和库存更新在同一事务中

#### 5.2.3 支付模拟

```
支付流程:
1. 用户选择支付方式
2. 调用支付接口
3. 修改订单状态为已付款
4. 记录支付时间和方式
5. 触发触发器更新商品库存
```

**触发器设计**:
- 订单付款后自动更新商品库存
- 订单取消后自动恢复商品库存

---

## 六、数据库展示

### 6.1 数据库架构

```
数据库名: secondhand_db

表结构:
┌──────────┐    ┌────────────┐    ┌──────────┐
│  users   │    │ categories │    │ products │
├──────────┤    ├────────────┤    ├──────────┤
│ id (PK)  │    │ id (PK)    │    │ id (PK)  │
│ username │    │ name       │    │ name     │
│ password │    │ parent_id  │    │ price    │
│ phone    │    └────────────┘    │ seller_id│
└──────────┘         │            │cat_id(FK)│
    │                └─────────────┤         │
    │                              └──────────┘
    │                                   │
┌──────────┐    ┌──────────┐    ┌──────────┐
│ orders   │    │ reviews  │    │ messages │
├──────────┤    ├──────────┤    ├──────────┤
│ id (PK)  │    │ id (PK)  │    │ id (PK)  │
│ user_id  │    │ order_id │    │ sender_id│
│prod_id(FK)│   │ user_id  │    │recv_id  │
│seller_id │    │prod_id(FK)│   │prod_id(FK)│
└──────────┘    └──────────┘    └──────────┘
```

### 6.2 核心表结构

#### 6.2.1 用户表 (users)

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| id | INT | PRIMARY KEY, AUTO_INCREMENT | 用户唯一标识 |
| username | VARCHAR(50) | NOT NULL, UNIQUE | 用户名 |
| password | VARCHAR(100) | NOT NULL | 密码(MD5) |
| phone | VARCHAR(20) | NOT NULL, UNIQUE | 手机号 |
| email | VARCHAR(100) | UNIQUE | 邮箱 |
| nickname | VARCHAR(50) | - | 昵称 |
| address | VARCHAR(255) | - | 收货地址 |
| status | TINYINT | DEFAULT 1 | 状态 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

#### 6.2.2 商品表 (products)

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| id | INT | PRIMARY KEY, AUTO_INCREMENT | 商品唯一标识 |
| name | VARCHAR(100) | NOT NULL | 商品名称 |
| description | TEXT | - | 商品描述 |
| price | DECIMAL(10,2) | NOT NULL | 售价 |
| original_price | DECIMAL(10,2) | - | 原价 |
| category_id | INT | FOREIGN KEY | 分类ID |
| seller_id | INT | FOREIGN KEY | 卖家ID |
| images | TEXT | - | 商品图片(JSON) |
| status | TINYINT | DEFAULT 1 | 状态 |
| stock | INT | DEFAULT 1 | 库存 |
| views | INT | DEFAULT 0 | 浏览量 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

#### 6.2.3 订单表 (orders)

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| id | VARCHAR(32) | PRIMARY KEY | 订单编号 |
| user_id | INT | FOREIGN KEY | 买家ID |
| product_id | INT | FOREIGN KEY | 商品ID |
| seller_id | INT | FOREIGN KEY | 卖家ID |
| price | DECIMAL(10,2) | NOT NULL | 单价 |
| quantity | INT | DEFAULT 1 | 数量 |
| total_amount | DECIMAL(10,2) | NOT NULL | 总价 |
| status | TINYINT | DEFAULT 1 | 订单状态 |
| payment_method | VARCHAR(20) | - | 支付方式 |
| payment_time | DATETIME | - | 支付时间 |

### 6.3 E-R图

```
                    ┌─────────────┐
                    │    用户     │
                    │   (users)   │
                    └──────┬──────┘
                           │
          ┌────────────────┼────────────────┐
          │                │                │
          ▼                ▼                ▼
    ┌──────────┐    ┌──────────┐    ┌──────────┐
    │  商品    │    │   订单   │    │   评价   │
    │(products)│    │ (orders) │    │(reviews) │
    └────┬─────┘    └────┬─────┘    └────┬─────┘
         │               │               │
         │               │               │
         ▼               │               │
    ┌──────────┐        │               │
    │ 分类     │        │               │
    │(category)│        │               │
    └──────────┘        │               │
                        │               │
                        ▼               │
                   ┌──────────┐         │
                   │  留言    │◄─────────┘
                   │(messages)│
                   └──────────┘
```

**实体关系说明**:
- 用户 1:N 商品（一个用户可发布多个商品）
- 用户 1:N 订单（一个用户可购买多个商品）
- 用户 1:N 评价（一个用户可发表多个评价）
- 用户 1:N 留言（一个用户可发送多条留言）
- 分类 1:N 商品（一个分类包含多个商品）
- 商品 1:N 订单（一个商品可被多次购买）
- 商品 1:N 评价（一个商品可有多个评价）
- 商品 1:N 留言（一个商品可有多个留言）
- 订单 1:1 评价（一个订单可有一条评价）

### 6.4 核心SQL语句

#### 6.4.1 用户管理

```sql
-- 用户登录验证
SELECT id, username, password, nickname, status 
FROM users 
WHERE (username = ? OR phone = ?) AND status = 1;

-- 添加用户
INSERT INTO users (username, password, phone, email, nickname) 
VALUES (?, MD5(?), ?, ?, ?);
```

#### 6.4.2 商品管理

```sql
-- 查询商品列表（分页）
SELECT p.*, c.name AS category_name 
FROM products p
LEFT JOIN categories c ON p.category_id = c.id
WHERE p.status = 1
ORDER BY p.created_at DESC
LIMIT ?, ?;

-- 搜索商品
SELECT * FROM products 
WHERE status = 1 AND (name LIKE ? OR description LIKE ?);
```

#### 6.4.3 订单管理

```sql
-- 创建订单
INSERT INTO orders (id, user_id, product_id, seller_id, price, quantity, total_amount) 
VALUES (?, ?, ?, ?, ?, ?, ?);

-- 更新订单状态
UPDATE orders SET status = ?, payment_method = ?, payment_time = NOW() 
WHERE id = ? AND status = 1;
```

#### 6.4.4 评价管理

```sql
-- 添加评价
INSERT INTO reviews (order_id, user_id, product_id, rating, content) 
VALUES (?, ?, ?, ?, ?);

-- 查询商品评价
SELECT r.*, u.nickname FROM reviews r
LEFT JOIN users u ON r.user_id = u.id
WHERE r.product_id = ? ORDER BY r.created_at DESC;
```

### 6.5 视图与触发器

#### 6.5.1 视图

```sql
-- 商品详情视图
CREATE VIEW v_product_detail AS
SELECT 
    p.*, c.name AS category_name, u.nickname AS seller_name,
    COALESCE(AVG(r.rating), 0) AS avg_rating,
    COUNT(r.id) AS review_count
FROM products p
LEFT JOIN categories c ON p.category_id = c.id
LEFT JOIN users u ON p.seller_id = u.id
LEFT JOIN reviews r ON p.id = r.product_id
WHERE p.status = 1
GROUP BY p.id;
```

#### 6.5.2 触发器

```sql
-- 订单支付后更新库存
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

---

## 七、小组分工

### 7.1 团队成员

| 姓名 | 学号 | 角色 | 负责模块 |
| :--- | :--- | :--- | :--- |
| 张三 | 2021001 | 组长 | 系统架构、数据库设计 |
| 李四 | 2021002 | 组员 | 用户模块、评价模块 |
| 王五 | 2021003 | 组员 | 商品模块、分类管理 |
| 赵六 | 2021004 | 组员 | 交易模块、留言模块 |

### 7.2 分工详情

#### 7.2.1 张三（组长）

**负责内容**:
- 系统整体架构设计
- 数据库ER图设计
- 数据库表结构设计
- 触发器和视图设计
- 项目文档统筹

**工作量占比**: 25%

**完成内容**:
- 数据库设计文档
- E-R图绘制
- 表结构定义
- 触发器实现
- 视图创建

#### 7.2.2 李四

**负责内容**:
- 用户模块开发（注册、登录、信息管理）
- 评价模块开发（商品评价、评分统计）
- 用户相关API接口开发

**工作量占比**: 25%

**完成内容**:
- UserController
- UserService/ServiceImpl
- UserRepository
- User实体类
- ReviewController
- ReviewService/ServiceImpl

#### 7.2.3 王五

**负责内容**:
- 商品模块开发（发布、浏览、搜索）
- 分类管理（分类列表、层级管理）
- 商品相关API接口开发

**工作量占比**: 25%

**完成内容**:
- ProductController
- ProductService/ServiceImpl
- ProductRepository
- Product实体类
- CategoryController
- CategoryRepository

#### 7.2.4 赵六

**负责内容**:
- 交易模块开发（订单创建、支付、管理）
- 留言模块开发（留言发送、查看、已读）
- 前端页面整合

**工作量占比**: 25%

**完成内容**:
- OrderController
- OrderService/ServiceImpl
- OrderRepository
- MessageController
- MessageService/ServiceImpl
- 前端HTML/CSS/JS实现

### 7.3 任务分配表

| 任务编号 | 任务名称 | 负责人 | 状态 |
| :--- | :--- | :--- | :--- |
| T001 | 需求分析 | 全体 | 完成 |
| T002 | 数据库设计 | 张三 | 完成 |
| T003 | 用户模块 | 李四 | 完成 |
| T004 | 商品模块 | 王五 | 完成 |
| T005 | 订单模块 | 赵六 | 完成 |
| T006 | 评价模块 | 李四 | 完成 |
| T007 | 留言模块 | 赵六 | 完成 |
| T008 | 前端页面 | 赵六 | 完成 |
| T009 | 文档编写 | 全体 | 完成 |
| T010 | 测试验证 | 全体 | 完成 |

---

## 八、组员收获与合作总结

### 8.1 组员收获

#### 8.1.1 张三

**收获**:
- 深入理解了数据库设计的完整流程，包括需求分析、ER图设计、表结构设计
- 掌握了触发器和视图的使用方法，理解了数据库层面的业务逻辑实现
- 学会了如何进行系统架构设计，合理划分模块和职责

**遇到的困难**:
- 数据库表之间的关联关系设计复杂，需要反复推敲
- 触发器的逻辑设计需要考虑各种边界情况

**解决方案**:
- 通过画ER图理清实体关系
- 参考类似系统的设计方案
- 与团队成员讨论确认设计方案

#### 8.1.2 李四

**收获**:
- 掌握了Spring Boot的分层架构设计
- 学会了用户认证和Session管理的实现
- 理解了JPA数据访问层的使用方法

**遇到的困难**:
- 用户密码加密和验证的实现
- 登录状态的管理

**解决方案**:
- 使用MD5加密算法
- 参考Spring Security的认证思路
- 与团队成员交流解决方案

#### 8.1.3 王五

**收获**:
- 学会了商品分类的层级设计
- 掌握了商品搜索和分页的实现
- 理解了RESTful API的设计规范

**遇到的困难**:
- 商品搜索的模糊查询优化
- 分页逻辑的实现

**解决方案**:
- 使用LIKE查询配合索引优化
- 利用Spring Data JPA的分页功能

#### 8.1.4 赵六

**收获**:
- 掌握了订单状态流转的设计
- 学会了事务管理的使用
- 理解了前后端交互的完整流程

**遇到的困难**:
- 订单创建的事务一致性保障
- 前端异步请求的处理

**解决方案**:
- 使用@Transactional注解管理事务
- 参考AJAX异步请求的最佳实践

### 8.2 合作总结

#### 8.2.1 团队协作方式

| 方式 | 描述 | 效果 |
| :--- | :--- | :--- |
| 需求评审 | 共同讨论需求，明确功能边界 | 确保需求理解一致 |
| 每日站会 | 同步进度，解决阻塞问题 | 及时发现和解决问题 |
| 代码评审 | 互相检查代码质量 | 提高代码规范性 |
| 文档共享 | 使用共享文档协作 | 保证文档一致性 |

#### 8.2.2 协作亮点

1. **分工明确**: 根据成员特长分配任务，提高效率
2. **沟通顺畅**: 定期沟通进度，及时解决问题
3. **互相支持**: 遇到困难时互相帮助，共同解决
4. **代码规范**: 统一代码风格和命名规范

#### 8.2.3 改进方向

1. **文档更新**: 加强文档的实时更新和维护
2. **测试覆盖**: 增加单元测试和集成测试
3. **代码复用**: 提取公共模块，减少重复代码

---

## 九、附录

### 9.1 状态码说明

| 状态码 | 说明 |
| :--- | :--- |
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未登录 |
| 500 | 服务器错误 |

### 9.2 订单状态

| 状态码 | 状态名 | 说明 |
| :--- | :--- | :--- |
| 1 | 待付款 | 订单创建，等待支付 |
| 2 | 已付款 | 完成支付 |
| 3 | 已发货 | 卖家已发货 |
| 4 | 已完成 | 交易完成 |
| 5 | 已取消 | 订单取消 |

### 9.3 商品状态

| 状态码 | 状态名 | 说明 |
| :--- | :--- | :--- |
| 1 | 在售 | 可购买 |
| 2 | 已卖出 | 已售出 |
| 3 | 下架 | 下架 |

### 9.4 API接口列表

| API路径 | 方法 | 说明 |
| :--- | :--- | :--- |
| /api/users/register | POST | 用户注册 |
| /api/users/login | POST | 用户登录 |
| /api/users/current | GET | 获取当前用户 |
| /api/products | GET | 获取商品列表 |
| /api/products | POST | 发布商品 |
| /api/products/{id} | GET | 获取商品详情 |
| /api/orders | POST | 创建订单 |
| /api/orders/user | GET | 获取用户订单 |
| /api/reviews | POST | 添加评价 |
| /api/messages | POST | 发送留言 |
| /api/categories | GET | 获取分类列表 |

---

**文档版本**: v1.0  
**创建日期**: 2024年  
**项目名称**: 二手商品交易系统  
**团队**: 数据库分析与设计实习小组
