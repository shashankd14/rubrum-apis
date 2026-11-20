-- Masters fix: Customer, Vendor and Location save their additional contacts and addresses
-- into these two shared tables. Material and Weighbridge do not use them, which is why
-- only those two were saving. Definitions match ContactMasterEntity / AddressMasterEntity.
-- Also adds the columns the Location, Vendor and Weighbridge screens send but were never stored.
-- Safe to re-run: tables are created only if missing, columns are added only if missing.

CREATE TABLE IF NOT EXISTS trading_contact_master (
    contact_id INT NOT NULL AUTO_INCREMENT,
    reference_type VARCHAR(30) NOT NULL,
    reference_id INT NOT NULL,
    contact_name VARCHAR(255) NULL,
    phone_no VARCHAR(20) NULL,
    alternate_phone_no VARCHAR(20) NULL,
    email_id VARCHAR(255) NULL,
    is_deleted BIT(1) NULL,
    created_by INT NULL,
    updated_by INT NULL,
    created_on DATETIME NULL,
    updated_on DATETIME NULL,
    PRIMARY KEY (contact_id),
    KEY idx_contact_reference (reference_type, reference_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS trading_address_master (
    address_id INT NOT NULL AUTO_INCREMENT,
    reference_type VARCHAR(30) NOT NULL,
    reference_id INT NOT NULL,
    address1 VARCHAR(500) NULL,
    address2 VARCHAR(500) NULL,
    city VARCHAR(100) NULL,
    state VARCHAR(100) NULL,
    pincode VARCHAR(10) NULL,
    is_deleted BIT(1) NULL,
    created_by INT NULL,
    updated_by INT NULL,
    created_on DATETIME NULL,
    updated_on DATETIME NULL,
    PRIMARY KEY (address_id),
    KEY idx_address_reference (reference_type, reference_id)
) ENGINE=InnoDB;

-- Location main contact
SET @ddl = IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'trading_location_master' AND COLUMN_NAME = 'contact_name') = 0,
    'ALTER TABLE trading_location_master ADD COLUMN contact_name VARCHAR(255) NULL', 'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @ddl = IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'trading_location_master' AND COLUMN_NAME = 'phone_no') = 0,
    'ALTER TABLE trading_location_master ADD COLUMN phone_no VARCHAR(20) NULL', 'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @ddl = IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'trading_location_master' AND COLUMN_NAME = 'contact_no') = 0,
    'ALTER TABLE trading_location_master ADD COLUMN contact_no VARCHAR(20) NULL', 'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @ddl = IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'trading_location_master' AND COLUMN_NAME = 'email_id') = 0,
    'ALTER TABLE trading_location_master ADD COLUMN email_id VARCHAR(255) NULL', 'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Vendor options (same columns as the customer master)
SET @ddl = IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'trading_vendor_master' AND COLUMN_NAME = 'include_ratesin_dc') = 0,
    'ALTER TABLE trading_vendor_master ADD COLUMN include_ratesin_dc VARCHAR(10) NULL', 'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @ddl = IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'trading_vendor_master' AND COLUMN_NAME = 'purchase_report') = 0,
    'ALTER TABLE trading_vendor_master ADD COLUMN purchase_report VARCHAR(255) NULL', 'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Weighbridge capacity
SET @ddl = IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'trading_weighbridge' AND COLUMN_NAME = 'capacity_in_tons') = 0,
    'ALTER TABLE trading_weighbridge ADD COLUMN capacity_in_tons DECIMAL(10,3) NULL', 'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;
