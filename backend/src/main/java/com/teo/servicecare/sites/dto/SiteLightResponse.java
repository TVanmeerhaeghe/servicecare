package com.teo.servicecare.sites.dto;

import com.teo.servicecare.sites.Site;

public record SiteLightResponse(
        Long id,
        String name,
        String url) {
    public static SiteLightResponse from(Site s) {
        return new SiteLightResponse(s.getId(), s.getName(), s.getUrl());
    }
}