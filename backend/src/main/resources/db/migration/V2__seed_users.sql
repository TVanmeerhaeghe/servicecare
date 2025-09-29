INSERT INTO users(email,password,first_name,last_name,phone,role,status,timezone)
VALUES
('admin@servicecare.local','$2a$10$QmXh2qvQ0b2eF6k0m7m4Re4hR7zZxS7wQh8R5zG7bqzYc5v7m2q1O','Admin','Root','+33600000001','ADMIN','ACTIVE','Europe/Paris'),
('agent1@servicecare.local','$2a$10$QmXh2qvQ0b2eF6k0m7m4Re4hR7zZxS7wQh8R5zG7bqzYc5v7m2q1O','Alice','Martin','+33600000002','AGENT','ACTIVE','Europe/Paris'),
('client1@acme.com','$2a$10$QmXh2qvQ0b2eF6k0m7m4Re4hR7zZxS7wQh8R5zG7bqzYc5v7m2q1O','Bob','Durand','+33142000000','CLIENT','INVITED','Europe/Paris');
