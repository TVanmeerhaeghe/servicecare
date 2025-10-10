ALTER TABLE tickets

  ADD COLUMN sla_timezone VARCHAR(64) NULL,
  ADD COLUMN sla_support_days VARCHAR(32) NULL,
  ADD COLUMN sla_support_hours_start TIME NULL,
  ADD COLUMN sla_support_hours_end TIME NULL,
  ADD COLUMN sla_measure_window VARCHAR(32) NULL,
  ADD COLUMN sla_pause_on_waiting TINYINT(1) NULL,

  ADD COLUMN sla_resp_crit_hours INT NULL,
  ADD COLUMN sla_resp_high_hours INT NULL,
  ADD COLUMN sla_resp_medium_hours INT NULL,
  ADD COLUMN sla_resp_low_hours INT NULL,

  ADD COLUMN sla_reso_crit_hours INT NULL,
  ADD COLUMN sla_reso_high_hours INT NULL,
  ADD COLUMN sla_reso_medium_hours INT NULL,
  ADD COLUMN sla_reso_low_hours INT NULL;