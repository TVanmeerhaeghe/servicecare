ALTER TABLE users
  ADD COLUMN client_id BIGINT NULL,
  ADD CONSTRAINT fk_users_client
    FOREIGN KEY (client_id) REFERENCES clients(id);
