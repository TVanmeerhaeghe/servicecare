package com.teo.servicecare.sites;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SiteRepository extends JpaRepository<Site, Long>, JpaSpecificationExecutor<Site> {
        List<Site> findAllByClient_IdOrderByNameAsc(Long clientId);
}
