CREATE TABLE interventions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  ticket_id BIGINT NOT NULL,
  type ENUM('ONSITE','REMOTE') NOT NULL DEFAULT 'REMOTE',
  status ENUM('PLANNED','IN_PROGRESS','DONE','CANCELED','NO_SHOW') NOT NULL DEFAULT 'PLANNED',
  technician_user_id BIGINT NULL,
  scheduled_start DATETIME NULL,
  scheduled_end DATETIME NULL,
  actual_start DATETIME NULL,
  actual_end DATETIME NULL,
  title VARCHAR(190) NULL,
  notes LONGTEXT NULL,
  report LONGTEXT NULL,
  travel_minutes INT DEFAULT 0,
  work_minutes INT DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,

  INDEX idx_interventions_ticket (ticket_id),
  INDEX idx_interventions_status (status),
  INDEX idx_interventions_sched (scheduled_start),
  INDEX idx_interventions_deleted (deleted_at),

  CONSTRAINT fk_interventions_ticket FOREIGN KEY (ticket_id) REFERENCES tickets(id),
  CONSTRAINT fk_interventions_technician FOREIGN KEY (technician_user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
