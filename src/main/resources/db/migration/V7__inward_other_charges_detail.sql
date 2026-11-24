-- Inward "Other Charges": one row per charge. trading_inward.other_charges keeps the total.
-- Safe to re-run.

CREATE TABLE IF NOT EXISTS trading_inward_other_charge (
    other_charge_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    inward_id INT NOT NULL,
    label VARCHAR(100) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    sort_order INT NOT NULL,
    KEY idx_inward_other_charge_inward (inward_id)
);

-- Old inwards have only a total: copy it as one "Other charges" row.
INSERT INTO trading_inward_other_charge (inward_id, label, amount, sort_order)
SELECT i.inward_id, 'Other charges', i.other_charges, 1
FROM trading_inward i
WHERE i.other_charges > 0
  AND NOT EXISTS (SELECT 1 FROM trading_inward_other_charge c WHERE c.inward_id = i.inward_id);
