package com.elice.team04backend.service;

public interface CacheService {
    void evictCache(String cacheName, String key);
}
