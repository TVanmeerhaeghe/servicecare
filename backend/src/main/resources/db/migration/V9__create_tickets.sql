CREATE TABLE tickets (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  client_id BIGINT NOT NULL,
  site_id BIGINT NULL,
  contract_id BIGINT NULL,
  title VARCHAR(190) NOT NULL,
  description TEXT NULL,
  priority ENUM('CRITICAL','HIGH','MEDIUM','LOW') NOT NULL DEFAULT 'MEDIUM',
  status ENUM('OPEN','ASSIGNED','IN_PROGRESS','WAITING','CLOSED','CANCELED') NOT NULL DEFAULT 'OPEN',
  waiting_reason VARCHAR(190) NULL,
  assignee_user_id BIGINT NULL,
  respond_by DATETIME NOT NULL,
  resolve_by DATETIME NOT NULL,
  responded_at DATETIME NULL,
  resolved_at DATETIME NULL,
  sla_breached BOOLEAN NOT NULL DEFAULT FALSE,
  paused_seconds INT NOT NULL DEFAULT 0,
  pause_started_at DATETIME NULL,
  created_by BIGINT NULL,
  updated_by BIGINT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_ticket_client FOREIGN KEY (client_id) REFERENCES clients(id),
  CONSTRAINT fk_ticket_site FOREIGN KEY (site_id) REFERENCES sites(id),
  CONSTRAINT fk_ticket_contract FOREIGN KEY (contract_id) REFERENCES contracts(id),
  CONSTRAINT fk_ticket_user FOREIGN KEY (assignee_user_id) REFERENCES users(id)
);

CREATE INDEX idx_ticket_filters ON tickets (status, priority, client_id, site_id, assignee_user_id, sla_breached);
