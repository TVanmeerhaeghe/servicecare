package com.teo.servicecare.contracts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ContractRepository
    extends JpaRepository<Contract, Long>, JpaSpecificationExecutor<Contract> {}
