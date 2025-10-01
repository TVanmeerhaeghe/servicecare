ALTER TABLE tickets
  ADD COLUMN deleted_at DATETIME NULL;

CREATE INDEX idx_tickets_deleted_at ON tickets (deleted_at);
