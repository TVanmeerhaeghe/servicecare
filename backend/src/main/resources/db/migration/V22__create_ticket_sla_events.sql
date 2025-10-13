CREATE TABLE IF NOT EXISTS ticket_sla_events (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  ticket_id BIGINT NOT NULL,
  type ENUM('WAIT_START','WAIT_END','STATUS_CHANGE','PRIORITY_CHANGE') NOT NULL,
  happened_at DATETIME NOT NULL,
  actor_user_id BIGINT NULL,
  note VARCHAR(255) NULL,
  payload JSON NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_ticket_sla_events_ticket
    FOREIGN KEY (ticket_id) REFERENCES tickets(id)
    ON DELETE CASCADE
);

CREATE INDEX idx_ticket_sla_events_ticket ON ticket_sla_events(ticket_id);
CREATE INDEX idx_ticket_sla_events_type ON ticket_sla_events(type);
CREATE INDEX idx_ticket_sla_events_happened ON ticket_sla_events(happened_at);