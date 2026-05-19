package com.microcommerce.surgeride_api.ride.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

@RequiredArgsConstructor
public class SurgePricingService {
    private final StringRedisTemplate stringRedisTemplate;
    private final SurgeZoneService surgeZoneService;

    public void recordDemand(Long riderId,double latitude,double longitude) {
        String zoneId = surgeZoneService.getZoneId(latitude,longitude);
        String redisKey = "surge:demand:"+ zoneId;

        stringRedisTemplate.opsForZSet().add(
                redisKey,
                String.valueOf(riderId),
                System.currentTimeMillis()
        );
    }

    public void recordSupply(Long driverId,double latitude,double longitude) {
        String zoneId = surgeZoneService.getZoneId(latitude,longitude);
        String redisKey = "surge:supply:"+ zoneId;
        stringRedisTemplate.opsForZSet().add(
                redisKey,
                String.valueOf(driverId),
                System.currentTimeMillis()
        );
    }

    public double calculateSurgeMultiplier(String zoneId){
        Double fiveMinutesAgo = System.currentTimeMillis()-300000;
    }
}
