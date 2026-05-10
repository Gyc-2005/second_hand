-- 更新users表，添加管理员角色字段
USE secondhand_db;

-- 添加role字段（0-普通用户，1-管理员）
ALTER TABLE users ADD COLUMN IF NOT EXISTS `role` TINYINT DEFAULT 0 COMMENT '角色：0-普通用户，1-管理员';

-- 创建管理员账号（用户名：admin，密码：admin123，手机号：13800138000）
INSERT INTO users (username, password, phone, role, status) 
SELECT 'admin', '0192023a7bbd73250516f069df18b500', '13800138000', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

-- 完成提示
SELECT '管理员功能已添加！默认管理员账号：admin，密码：admin123' AS message;
