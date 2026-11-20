SET @doc_id_type = (SELECT COLUMN_TYPE FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'trading_stock_document' AND COLUMN_NAME = 'document_id');
SET @ddl = CONCAT('CREATE TABLE IF NOT EXISTS trading_stock_document_line (',
    'line_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, ',
    'document_id ', @doc_id_type, ' NOT NULL, ',
    'line_no INT NOT NULL, ',
    'material_id VARCHAR(64) NULL, ',
    'item_name VARCHAR(255) NULL, ',
    'heat_no VARCHAR(100) NULL, ',
    'system_weight DECIMAL(19,3) NULL, ',
    'system_pieces INT NULL, ',
    'physical_weight DECIMAL(19,3) NULL, ',
    'physical_pieces INT NULL, ',
    'before_weight DECIMAL(19,3) NULL, ',
    'pieces INT NULL, ',
    'verified DECIMAL(19,3) NULL, ',
    'add_weight DECIMAL(19,3) NULL, ',
    'deduct_weight DECIMAL(19,3) NULL, ',
    'remarks TEXT NULL, ',
    'KEY idx_stock_document_line_document (document_id, line_no), ',
    'KEY idx_stock_document_line_material (material_id), ',
    'CONSTRAINT fk_stock_document_line_document FOREIGN KEY (document_id) REFERENCES trading_stock_document (document_id) ON DELETE CASCADE',
    ') ENGINE=InnoDB');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @lines_type = (SELECT COLUMN_TYPE FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'trading_stock_document' AND COLUMN_NAME = 'lines_json');
SET @ddl = IF(@lines_type IS NULL, 'SELECT 1', CONCAT('ALTER TABLE trading_stock_document MODIFY COLUMN lines_json ', @lines_type, ' NULL'));
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @inward_id_type = (SELECT COLUMN_TYPE FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'trading_inward' AND COLUMN_NAME = 'inward_id');
SET @itemchild_id_type = (SELECT COLUMN_TYPE FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'trading_inward_items' AND COLUMN_NAME = 'itemchild_id');
SET @ddl = CONCAT('CREATE TABLE IF NOT EXISTS trading_inward_attachment (',
    'attachment_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, ',
    'inward_id ', @inward_id_type, ' NOT NULL, ',
    'itemchild_id ', @itemchild_id_type, ' NULL, ',
    'attachment_type VARCHAR(30) NOT NULL, ',
    'file_key VARCHAR(500) NOT NULL, ',
    'file_name VARCHAR(255) NULL, ',
    'file_size BIGINT NULL, ',
    'mime_type VARCHAR(100) NULL, ',
    'sort_order INT NOT NULL, ',
    'KEY idx_inward_attachment_inward (inward_id, attachment_type), ',
    'KEY idx_inward_attachment_item (itemchild_id, attachment_type), ',
    'CONSTRAINT fk_inward_attachment_inward FOREIGN KEY (inward_id) REFERENCES trading_inward (inward_id) ON DELETE CASCADE, ',
    'CONSTRAINT fk_inward_attachment_item FOREIGN KEY (itemchild_id) REFERENCES trading_inward_items (itemchild_id) ON DELETE CASCADE',
    ') ENGINE=InnoDB');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @item_id_type = (SELECT COLUMN_TYPE FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'trading_material_master' AND COLUMN_NAME = 'item_id');
SET @ddl = CONCAT('CREATE TABLE IF NOT EXISTS trading_material_spec (',
    'spec_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, ',
    'item_id ', @item_id_type, ' NOT NULL, ',
    'spec_key VARCHAR(100) NULL, ',
    'spec_name VARCHAR(255) NULL, ',
    'value_number DECIMAL(19,6) NULL, ',
    'value_text VARCHAR(255) NULL, ',
    'unit VARCHAR(50) NULL, ',
    'sort_order INT NOT NULL, ',
    'KEY idx_material_spec_item (item_id, sort_order), ',
    'CONSTRAINT fk_material_spec_item FOREIGN KEY (item_id) REFERENCES trading_material_master (item_id) ON DELETE CASCADE',
    ') ENGINE=InnoDB');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl = CONCAT('CREATE TABLE IF NOT EXISTS trading_material_unit_weight (',
    'unit_weight_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, ',
    'item_id ', @item_id_type, ' NOT NULL, ',
    'basis VARCHAR(30) NOT NULL, ',
    'weight_value DECIMAL(19,6) NOT NULL, ',
    'is_active BIT(1) NOT NULL DEFAULT 1, ',
    'sort_order INT NOT NULL, ',
    'KEY idx_material_unit_weight_item (item_id, basis), ',
    'CONSTRAINT fk_material_unit_weight_item FOREIGN KEY (item_id) REFERENCES trading_material_master (item_id) ON DELETE CASCADE',
    ') ENGINE=InnoDB');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl = CONCAT('CREATE TABLE IF NOT EXISTS trading_material_process (',
    'material_process_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, ',
    'item_id ', @item_id_type, ' NOT NULL, ',
    'process_name VARCHAR(255) NOT NULL, ',
    'sort_order INT NOT NULL, ',
    'KEY idx_material_process_item (item_id), ',
    'CONSTRAINT fk_material_process_item FOREIGN KEY (item_id) REFERENCES trading_material_master (item_id) ON DELETE CASCADE',
    ') ENGINE=InnoDB');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
