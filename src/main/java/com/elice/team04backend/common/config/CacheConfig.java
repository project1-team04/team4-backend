package com.elice.team04backend.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CacheConfig {
    @Value("${app.page-size}")
    private int pageSize;

    public int getPageSize() {
        return pageSize;
    }
}
