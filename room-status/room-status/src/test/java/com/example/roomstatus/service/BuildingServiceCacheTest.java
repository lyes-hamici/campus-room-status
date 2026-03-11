package com.example.roomstatus.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKey;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BuildingServiceCacheTest {

    @Autowired
    private BuildingService buildingService;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void shouldPopulateBuildingsCache() {
        buildingService.getBuildings();

        Cache cache = cacheManager.getCache("buildings");

        assertThat(cache).isNotNull();
        assertThat(cache.get(SimpleKey.EMPTY)).isNotNull();
    }
}