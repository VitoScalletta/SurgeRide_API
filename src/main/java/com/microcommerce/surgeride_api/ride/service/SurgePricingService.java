package com.microcommerce.surgeride_api.ride.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
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
        String demandKey = "surge:demand:" + zoneId;
        String supplyKey = "surge:supply:" + zoneId;

        long now = System.currentTimeMillis();
        long fiveMinutesAgo = now - 300000;

        stringRedisTemplate.opsForZSet().removeRangeByScore(demandKey,0,fiveMinutesAgo);
        stringRedisTemplate.opsForZSet().removeRangeByScore(supplyKey,0,fiveMinutesAgo);
        Long demandCount = stringRedisTemplate.opsForZSet().zCard(demandKey);
        Long supplyCount = stringRedisTemplate.opsForZSet().zCard(supplyKey);
        if(demandCount == null || supplyCount == null){
            return 0L;
        }
        if (demandCount == 0) {
            return 1L;
        }
        if (supplyCount == 0) {
            return 2L;
        }
        double ratio = demandCount / supplyCount;

        if (ratio <= 1){
            return 1.0;
        }
        else if (ratio < 2){
            return 1.2;
        }
        else if (ratio < 5){
            return 1.5;
        }
        else{
            return 2.0;
        }
    }
}
