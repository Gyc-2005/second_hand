-- 更新messages表，添加read字段
USE secondhand_db;

-- 添加read字段（如果不存在）
ALTER TABLE messages ADD COLUMN IF NOT EXISTS `read` TINYINT DEFAULT 0 COMMENT '是否已读：0-未读，1-已读';

-- 完成提示
SELECT 'messages表已添加read字段！' AS message;
