package com.teo.servicecare.clients;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
  boolean existsByName(String name);
}
