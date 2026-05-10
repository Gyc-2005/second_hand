-- 更新messages表，将read字段改名为is_read
USE secondhand_db;

-- 如果存在read字段，改名为is_read
ALTER TABLE messages CHANGE COLUMN `read` `is_read` TINYINT DEFAULT 0 COMMENT '是否已读：0-未读，1-已读';

-- 完成提示
SELECT 'messages表的read字段已改名为is_read！' AS message;
