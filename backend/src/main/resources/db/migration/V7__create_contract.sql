CREATE TABLE contracts (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  client_id BIGINT NOT NULL,
  name VARCHAR(190) NOT NULL,
  description TEXT,

  start_date DATE NOT NULL,
  end_date   DATE NULL,
  auto_renew BOOLEAN NOT NULL DEFAULT FALSE,
  notice_days INT NOT NULL DEFAULT 30,

  timezone VARCHAR(50) NOT NULL DEFAULT 'Europe/Paris',
  support_days ENUM('MON_FRI','SEVEN_DAYS') NOT NULL DEFAULT 'MON_FRI',
  support_hours_start TIME NOT NULL DEFAULT '09:00:00',
  support_hours_end   TIME NOT NULL DEFAULT '18:00:00',
  measure_window ENUM('BUSINESS_HOURS','CALENDAR') NOT NULL DEFAULT 'BUSINESS_HOURS', 
  pause_on_waiting BOOLEAN NOT NULL DEFAULT TRUE, 

  resp_crit_hours   INT NOT NULL DEFAULT 1,
  resp_high_hours   INT NOT NULL DEFAULT 4,
  resp_medium_hours INT NOT NULL DEFAULT 8,
  resp_low_hours    INT NOT NULL DEFAULT 24,

  reso_crit_hours   INT NOT NULL DEFAULT 4,
  reso_high_hours   INT NOT NULL DEFAULT 16,
  reso_medium_hours INT NOT NULL DEFAULT 40,
  reso_low_hours    INT NOT NULL DEFAULT 120,

  included_hours_month INT NOT NULL DEFAULT 0, 
  max_tickets_month    INT NOT NULL DEFAULT 0, 
  overtime_rate DECIMAL(10,2) NULL,           
  emergency_rate DECIMAL(10,2) NULL,          

  status ENUM('ACTIVE','INACTIVE','EXPIRED') NOT NULL DEFAULT 'ACTIVE',

  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  created_by BIGINT NULL,
  updated_by BIGINT NULL,

  CONSTRAINT fk_contracts_client   FOREIGN KEY (client_id) REFERENCES clients(id),
  CONSTRAINT fk_ctr_created_by     FOREIGN KEY (created_by) REFERENCES users(id),
  CONSTRAINT fk_ctr_updated_by     FOREIGN KEY (updated_by) REFERENCES users(id),

  UNIQUE KEY uk_contracts_client_name (client_id, name),
  INDEX idx_contracts_client (client_id),
  INDEX idx_contracts_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE contract_sites (
  contract_id BIGINT NOT NULL,
  site_id BIGINT NOT NULL,
  PRIMARY KEY (contract_id, site_id),
  CONSTRAINT fk_ctr_sites_contract FOREIGN KEY (contract_id) REFERENCES contracts(id) ON DELETE CASCADE,
  CONSTRAINT fk_ctr_sites_site     FOREIGN KEY (site_id) REFERENCES sites(id)     ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
