package com.microcommerce.surgeride_api.ride.service;

import com.microcommerce.surgeride_api.ride.dto.LocationUpdateRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class DriverLocationService {
    private final StringRedisTemplate stringRedisTemplate;

    public void updateLocation(LocationUpdateRequest request){
        Point location = new Point(request.getLongitude(), request.getLatitude());
        String driverId = String.valueOf(request.getDriverId());
        stringRedisTemplate.opsForGeo().add(
                "driver_location",
                location,
                driverId
        );
        stringRedisTemplate.opsForZSet().add("driver_last_Seen",driverId,System.currentTimeMillis());
        log.info("Driver {} location updated", driverId);
    }


}
