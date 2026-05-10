-- 修复email唯一约束问题
USE secondhand_db;

-- 1. 删除旧的唯一约束
ALTER TABLE users DROP INDEX email;

-- 2. 添加普通索引（用于查询）
CREATE INDEX idx_email ON users(email);

-- 3. 更新已有的空email为NULL（可选，更规范）
UPDATE users SET email = NULL WHERE email = '' OR email IS NULL;

-- 完成提示
SELECT 'email唯一约束已移除，现在可以有多个用户没有填写email！' AS message;
