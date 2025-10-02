package com.teo.servicecare.sites;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SiteRepository
    extends JpaRepository<Site, Long>, JpaSpecificationExecutor<Site> {}
