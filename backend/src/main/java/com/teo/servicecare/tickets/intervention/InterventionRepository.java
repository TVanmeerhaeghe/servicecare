package com.teo.servicecare.tickets.intervention;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InterventionRepository
        extends JpaRepository<Intervention, Long>, JpaSpecificationExecutor<Intervention> {
}
