CREATE TABLE ticket_work_logs (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  ticket_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  started_at DATETIME NOT NULL,
  ended_at DATETIME NULL,
  minutes INT NULL,
  note LONGTEXT NULL,

  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,

  INDEX idx_twl_ticket (ticket_id),
  INDEX idx_twl_user (user_id),
  INDEX idx_twl_started (started_at),
  INDEX idx_twl_deleted (deleted_at),

  CONSTRAINT fk_twl_ticket FOREIGN KEY (ticket_id) REFERENCES tickets(id),
  CONSTRAINT fk_twl_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
