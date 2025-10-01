-- === CONTRATS ===
-- ACME Gold (client: Acme Corp)
INSERT INTO contracts (
  client_id, name, description,
  start_date, end_date, auto_renew, notice_days,
  timezone, support_days, support_hours_start, support_hours_end, measure_window, pause_on_waiting,
  resp_crit_hours, resp_high_hours, resp_medium_hours, resp_low_hours,
  reso_crit_hours, reso_high_hours, reso_medium_hours, reso_low_hours,
  included_hours_month, max_tickets_month, overtime_rate, emergency_rate,
  status
)
VALUES (
  (SELECT id FROM clients WHERE name = 'Acme Corp' LIMIT 1),
  'ACME Gold', 'Support premium 7j/7 avec délais agressifs',
  DATE_SUB(CURDATE(), INTERVAL 90 DAY), NULL, TRUE, 60,
  'Europe/Paris', 'SEVEN_DAYS', '08:00:00', '20:00:00', 'CALENDAR', TRUE,
  1, 2, 4, 8,
  4, 8, 24, 72,
  10, 0, 120.00, 250.00,
  'ACTIVE'
);

-- GLOBEX Standard (client: Globex)
INSERT INTO contracts (
  client_id, name, description,
  start_date, end_date, auto_renew, notice_days,
  timezone, support_days, support_hours_start, support_hours_end, measure_window, pause_on_waiting,
  resp_crit_hours, resp_high_hours, resp_medium_hours, resp_low_hours,
  reso_crit_hours, reso_high_hours, reso_medium_hours, reso_low_hours,
  included_hours_month, max_tickets_month, overtime_rate, emergency_rate,
  status
)
VALUES (
  (SELECT id FROM clients WHERE name = 'Globex' LIMIT 1),
  'Globex Standard', 'Support heures ouvrées, objectifs classiques',
  DATE_SUB(CURDATE(), INTERVAL 30 DAY), NULL, TRUE, 30,
  'Europe/Paris', 'MON_FRI', '09:00:00', '18:00:00', 'BUSINESS_HOURS', TRUE,
  4, 6, 8, 24,
  16, 40, 80, 160,
  5, 30, 90.00, 200.00,
  'ACTIVE'
);

-- INITECH Legacy (client: Initech) (bientôt expiré)
INSERT INTO contracts (
  client_id, name, description,
  start_date, end_date, auto_renew, notice_days,
  timezone, support_days, support_hours_start, support_hours_end, measure_window, pause_on_waiting,
  resp_crit_hours, resp_high_hours, resp_medium_hours, resp_low_hours,
  reso_crit_hours, reso_high_hours, reso_medium_hours, reso_low_hours,
  included_hours_month, max_tickets_month, overtime_rate, emergency_rate,
  status
)
VALUES (
  (SELECT id FROM clients WHERE name = 'Initech' LIMIT 1),
  'Initech Legacy', 'Ancien contrat en fin de vie',
  DATE_SUB(CURDATE(), INTERVAL 365 DAY), DATE_ADD(CURDATE(), INTERVAL 15 DAY), FALSE, 45,
  'Europe/Paris', 'MON_FRI', '09:00:00', '18:00:00', 'BUSINESS_HOURS', TRUE,
  8, 12, 24, 48,
  40, 80, 120, 200,
  0, 0, NULL, NULL,
  'INACTIVE'
);

-- === LIEN CONTRATS ↔ SITES ===
-- ACME Gold → Acme Main Website, Acme Shop
INSERT INTO contract_sites (contract_id, site_id) VALUES
(
  (SELECT c.id FROM contracts c
     WHERE c.name='ACME Gold' AND c.client_id=(SELECT id FROM clients WHERE name='Acme Corp' LIMIT 1)
     LIMIT 1),
  (SELECT s.id FROM sites s
     WHERE s.name='Acme Main Website' AND s.client_id=(SELECT id FROM clients WHERE name='Acme Corp' LIMIT 1)
     LIMIT 1)
),
(
  (SELECT c.id FROM contracts c
     WHERE c.name='ACME Gold' AND c.client_id=(SELECT id FROM clients WHERE name='Acme Corp' LIMIT 1)
     LIMIT 1),
  (SELECT s.id FROM sites s
     WHERE s.name='Acme Shop' AND s.client_id=(SELECT id FROM clients WHERE name='Acme Corp' LIMIT 1)
     LIMIT 1)
);

-- Globex Standard → Globex Landing, Globex API
INSERT INTO contract_sites (contract_id, site_id) VALUES
(
  (SELECT c.id FROM contracts c
     WHERE c.name='Globex Standard' AND c.client_id=(SELECT id FROM clients WHERE name='Globex' LIMIT 1)
     LIMIT 1),
  (SELECT s.id FROM sites s
     WHERE s.name='Globex Landing' AND s.client_id=(SELECT id FROM clients WHERE name='Globex' LIMIT 1)
     LIMIT 1)
),
(
  (SELECT c.id FROM contracts c
     WHERE c.name='Globex Standard' AND c.client_id=(SELECT id FROM clients WHERE name='Globex' LIMIT 1)
     LIMIT 1),
  (SELECT s.id FROM sites s
     WHERE s.name='Globex API' AND s.client_id=(SELECT id FROM clients WHERE name='Globex' LIMIT 1)
     LIMIT 1)
);

-- Initech Legacy → Initech Portal
INSERT INTO contract_sites (contract_id, site_id) VALUES
(
  (SELECT c.id FROM contracts c
     WHERE c.name='Initech Legacy' AND c.client_id=(SELECT id FROM clients WHERE name='Initech' LIMIT 1)
     LIMIT 1),
  (SELECT s.id FROM sites s
     WHERE s.name='Initech Portal' AND s.client_id=(SELECT id FROM clients WHERE name='Initech' LIMIT 1)
     LIMIT 1)
);
