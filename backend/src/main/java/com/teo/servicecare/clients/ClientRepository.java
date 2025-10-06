package com.teo.servicecare.clients;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

public interface ClientRepository
    extends JpaRepository<Client, Long>, JpaSpecificationExecutor<Client> {

  Optional<Client> findByName(String name);

  boolean existsByName(String name);

  boolean existsByNameAndIdNot(String name, Long id);

}