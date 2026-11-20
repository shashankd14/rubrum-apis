-- Apply to the application database before running the updated backend.
-- Adds QC fields only; safe to re-run after a partial application.

SET @inward_qc_ddl = IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'trading_inward' AND COLUMN_NAME = 'qc_certificate_no') = 0, 'ALTER TABLE trading_inward ADD COLUMN qc_certificate_no VARCHAR(100) NULL', 'SELECT 1');
PREPARE inward_qc_stmt FROM @inward_qc_ddl;
EXECUTE inward_qc_stmt;
DEALLOCATE PREPARE inward_qc_stmt;

SET @inward_qc_ddl = IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'trading_inward' AND COLUMN_NAME = 'qc_status') = 0, 'ALTER TABLE trading_inward ADD COLUMN qc_status VARCHAR(20) NULL', 'SELECT 1');
PREPARE inward_qc_stmt FROM @inward_qc_ddl;
EXECUTE inward_qc_stmt;
DEALLOCATE PREPARE inward_qc_stmt;

SET @inward_qc_ddl = IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'trading_inward' AND COLUMN_NAME = 'qc_yield_strength') = 0, 'ALTER TABLE trading_inward ADD COLUMN qc_yield_strength DECIMAL(19,6) NULL', 'SELECT 1');
PREPARE inward_qc_stmt FROM @inward_qc_ddl;
EXECUTE inward_qc_stmt;
DEALLOCATE PREPARE inward_qc_stmt;

SET @inward_qc_ddl = IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'trading_inward' AND COLUMN_NAME = 'qc_tensile_strength') = 0, 'ALTER TABLE trading_inward ADD COLUMN qc_tensile_strength DECIMAL(19,6) NULL', 'SELECT 1');
PREPARE inward_qc_stmt FROM @inward_qc_ddl;
EXECUTE inward_qc_stmt;
DEALLOCATE PREPARE inward_qc_stmt;

SET @inward_qc_ddl = IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'trading_inward' AND COLUMN_NAME = 'qc_elongation') = 0, 'ALTER TABLE trading_inward ADD COLUMN qc_elongation DECIMAL(9,6) NULL', 'SELECT 1');
PREPARE inward_qc_stmt FROM @inward_qc_ddl;
EXECUTE inward_qc_stmt;
DEALLOCATE PREPARE inward_qc_stmt;

SET @inward_qc_ddl = IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'trading_inward' AND COLUMN_NAME = 'qc_remark') = 0, 'ALTER TABLE trading_inward ADD COLUMN qc_remark TEXT NULL', 'SELECT 1');
PREPARE inward_qc_stmt FROM @inward_qc_ddl;
EXECUTE inward_qc_stmt;
DEALLOCATE PREPARE inward_qc_stmt;

SET @inward_qc_ddl = IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'trading_inward' AND COLUMN_NAME = 'qc_certificates_json') = 0, 'ALTER TABLE trading_inward ADD COLUMN qc_certificates_json LONGTEXT NULL', 'SELECT 1');
PREPARE inward_qc_stmt FROM @inward_qc_ddl;
EXECUTE inward_qc_stmt;
DEALLOCATE PREPARE inward_qc_stmt;
