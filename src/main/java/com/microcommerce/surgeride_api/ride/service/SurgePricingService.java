package com.microcommerce.surgeride_api.ride.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
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

        stringRedisTemplate.opsForSet().add("surge:active_zones",zoneId);
    }

    public void recordSupply(Long driverId,double latitude,double longitude) {
        String zoneId = surgeZoneService.getZoneId(latitude,longitude);
        String redisKey = "surge:supply:"+ zoneId;
        stringRedisTemplate.opsForZSet().add(
                redisKey,
                String.valueOf(driverId),
                System.currentTimeMillis()
        );
        stringRedisTemplate.opsForSet().add("surge:active_zones",zoneId);
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
        if(demandCount == null){
            demandCount = 0L;
        }
        if(supplyCount == null){
            supplyCount = 0L;
        }
        if (demandCount == 0) {
            return 1.0;
        }
        if (supplyCount == 0) {
            return 2.0;
        }
        double ratio = (double) demandCount / supplyCount;

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

    @Scheduled(fixedRate = 10000)
    public void updateAllZoneSurge(){
        Set<String> activeZones = stringRedisTemplate.opsForSet().members("surge:active_zones");

        if(activeZones == null|| activeZones.isEmpty()){
            return;
        }
        for (String zoneId : activeZones) {
            double multiplier = calculateSurgeMultiplier(zoneId);
            String multiplierKey = "surge:multiplier:" + zoneId;
            stringRedisTemplate.opsForValue().set(multiplierKey,String.valueOf(multiplier));

            log.info("Bölge {} için dinamik fiyat çarpanı güncellendi: {}x",zoneId,multiplier);
        }
    }

    public double estimatePrice(double starlat, double starlon, double distanceInKm) {
        String zoneId = surgeZoneService.getZoneId(starlat,starlon);
        String multiplierStr = stringRedisTemplate.opsForValue().get("surge:multiplier:" + zoneId);

        double multiplier = 1.0;
        if (multiplierStr != null) {
            multiplier = Double.parseDouble(multiplierStr);
        }

        double basePrice = 60.0 +(distanceInKm*20.0);
        return basePrice * multiplier;
    }
}
