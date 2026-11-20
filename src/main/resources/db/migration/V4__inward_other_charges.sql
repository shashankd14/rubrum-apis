-- Adds trading_inward.other_charges. Safe to re-run.
SET @ddl = IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'trading_inward' AND COLUMN_NAME = 'other_charges') = 0, 'ALTER TABLE trading_inward ADD COLUMN other_charges DECIMAL(19,2) NULL', 'SELECT 1');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
