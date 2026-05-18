package com.microcommerce.surgeride_api.ride.service;

import com.microcommerce.surgeride_api.ride.dto.LocationUpdateRequest;
import org.springframework.data.redis.connection.RedisGeoCommands;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class DriverLocationService {
    private final StringRedisTemplate stringRedisTemplate;

    public void updateLocation(LocationUpdateRequest request){
        Point location = new Point(request.getLongitude(), request.getLatitude());
        String driverId = String.valueOf(request.getDriverId());
        stringRedisTemplate.opsForGeo().add(
                "driver_locations",
                location,
                driverId
        );
        stringRedisTemplate.opsForZSet().add("driver_last_Seen",driverId,System.currentTimeMillis());
        log.info("Driver {} location updated", driverId);
    }

    public List<String> getNearbyDrivers(double latitude, double longitude,double radiusInKm){
        Point riderLocation = new Point(longitude, latitude);
        Distance distance = new Distance(radiusInKm, Metrics.KILOMETERS);
        Circle searchArea = new Circle(riderLocation, distance);
        GeoResults<RedisGeoCommands.GeoLocation<String>> results =
                stringRedisTemplate.opsForGeo().radius("driver_locations",searchArea);

        if (results == null){
            return new ArrayList<>();
        }

        return results.getContent().stream()
                .map(geoResult -> geoResult.getContent().getName()).toList();
    }

    @Scheduled(fixedRate = 60000)
    public void removeInactiveDrivers(){
        long oneMinuteAgo = System.currentTimeMillis() - 60000;

        Set<String> inactiveDrivers = stringRedisTemplate.opsForZSet()
                .rangeByScore("driver_last_Seen",0,oneMinuteAgo);

        if(inactiveDrivers != null && !inactiveDrivers.isEmpty()){
            for(String driverId : inactiveDrivers){
                stringRedisTemplate.opsForGeo().remove("driver_last_seen",driverId);
                stringRedisTemplate.opsForZSet().remove("driver_last_Seen",driverId);
                log.info("Uzun süredir aktif olmayan sürücüler silindi Sürücü id : "+ driverId);
            }
        }
    }

}
