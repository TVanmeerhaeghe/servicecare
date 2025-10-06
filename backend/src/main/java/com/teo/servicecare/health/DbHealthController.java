package com.teo.servicecare.health;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class DbHealthController {
  private final JdbcTemplate jdbc;

  public DbHealthController(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @GetMapping("/api/db/ping")
  public Map<String, Object> dbPing() {
    Integer v = jdbc.queryForObject("select 1", Integer.class);
    return Map.of("db", v);
  }
}
