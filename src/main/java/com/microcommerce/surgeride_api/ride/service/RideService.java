package com.microcommerce.surgeride_api.ride.service;

import com.microcommerce.surgeride_api.ride.dto.RideRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RideService {
    private final SurgeZoneService surgeZoneService;
    private final StringRedisTemplate stringRedisTemplate;
    public String requestRide(RideRequestDto requestDto){
        String zoneId = surgeZoneService.getZoneId(requestDto.getStartLatitude(), requestDto.getStartLongitude());
        String multiplierStr = stringRedisTemplate.opsForValue().get("surge:multiplier:" + zoneId);

        double liveMultiplier = 1.0;

        if(multiplierStr != null){
            liveMultiplier = Double.parseDouble(multiplierStr);
        }
        double differenceBetweenMultipliers = Math.abs(liveMultiplier - requestDto.getAcceptedMultiplier());
        if (differenceBetweenMultipliers > 0.01) {
            throw new RuntimeException("Bölgenizdeki fiyatlar güncellendi! Lütfen tekrar deneyiniz");
        }
        return "Araç aranıyor";
    }

}
