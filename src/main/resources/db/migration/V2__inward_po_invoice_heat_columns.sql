-- Repairs a partially applied 2026-10-23-inward-po-number.sql migration.
-- Select the application database first. Safe to re-run; preserves existing data.

SET @inward_repair_ddl = IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'trading_inward' AND COLUMN_NAME = 'po_number') = 0, 'ALTER TABLE trading_inward ADD COLUMN po_number VARCHAR(100) NULL', 'SELECT 1');
PREPARE inward_repair_stmt FROM @inward_repair_ddl;
EXECUTE inward_repair_stmt;
DEALLOCATE PREPARE inward_repair_stmt;

SET @inward_repair_ddl = IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'trading_inward' AND COLUMN_NAME = 'invoice_value') = 0, 'ALTER TABLE trading_inward ADD COLUMN invoice_value DECIMAL(19,2) NULL', 'SELECT 1');
PREPARE inward_repair_stmt FROM @inward_repair_ddl;
EXECUTE inward_repair_stmt;
DEALLOCATE PREPARE inward_repair_stmt;

SET @inward_repair_ddl = IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'trading_inward' AND COLUMN_NAME = 'received_date') = 0, 'ALTER TABLE trading_inward ADD COLUMN received_date DATETIME NULL', 'SELECT 1');
PREPARE inward_repair_stmt FROM @inward_repair_ddl;
EXECUTE inward_repair_stmt;
DEALLOCATE PREPARE inward_repair_stmt;

SET @inward_repair_ddl = IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'trading_inward' AND COLUMN_NAME = 'documents_json') = 0, 'ALTER TABLE trading_inward ADD COLUMN documents_json LONGTEXT NULL', 'SELECT 1');
PREPARE inward_repair_stmt FROM @inward_repair_ddl;
EXECUTE inward_repair_stmt;
DEALLOCATE PREPARE inward_repair_stmt;

SET @inward_repair_ddl = IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'trading_inward_items' AND COLUMN_NAME = 'heat_no') = 0, 'ALTER TABLE trading_inward_items ADD COLUMN heat_no VARCHAR(100) NULL', 'SELECT 1');
PREPARE inward_repair_stmt FROM @inward_repair_ddl;
EXECUTE inward_repair_stmt;
DEALLOCATE PREPARE inward_repair_stmt;

SET @inward_repair_ddl = IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'trading_inward_items' AND COLUMN_NAME = 'bundle_numbers') = 0, 'ALTER TABLE trading_inward_items ADD COLUMN bundle_numbers VARCHAR(1000) NULL', 'SELECT 1');
PREPARE inward_repair_stmt FROM @inward_repair_ddl;
EXECUTE inward_repair_stmt;
DEALLOCATE PREPARE inward_repair_stmt;

SET @inward_repair_ddl = IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'trading_inward_items' AND COLUMN_NAME = 'remark') = 0, 'ALTER TABLE trading_inward_items ADD COLUMN remark TEXT NULL', 'SELECT 1');
PREPARE inward_repair_stmt FROM @inward_repair_ddl;
EXECUTE inward_repair_stmt;
DEALLOCATE PREPARE inward_repair_stmt;

SET @inward_repair_ddl = IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'trading_inward_items' AND COLUMN_NAME = 'images_json') = 0, 'ALTER TABLE trading_inward_items ADD COLUMN images_json LONGTEXT NULL', 'SELECT 1');
PREPARE inward_repair_stmt FROM @inward_repair_ddl;
EXECUTE inward_repair_stmt;
DEALLOCATE PREPARE inward_repair_stmt;

ALTER TABLE trading_inward_items
    MODIFY COLUMN unit_volume DECIMAL(19,6) NULL,
    MODIFY COLUMN volume DECIMAL(19,2) NULL;
ALTER TABLE trading_inward
    MODIFY COLUMN total_inward_volume DECIMAL(19,2) NULL,
    MODIFY COLUMN total_volume DECIMAL(19,2) NULL;
