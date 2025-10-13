SET @col_exists := (
  SELECT COUNT(*)
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'ticket_sla_events'
    AND COLUMN_NAME = 'actor_user_name'
);

SET @ddl := IF(@col_exists = 0,
  'ALTER TABLE ticket_sla_events ADD COLUMN actor_user_name VARCHAR(255) NULL',
  'SELECT 1'
);

PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;