package com.microcommerce.surgeride_api.ride.service;

import com.microcommerce.surgeride_api.ride.dto.RideRequestDto;
import com.microcommerce.surgeride_api.ride.entity.Ride;
import com.microcommerce.surgeride_api.ride.enums.RideStatus;
import com.microcommerce.surgeride_api.ride.repository.RideRepository;
import com.microcommerce.surgeride_api.user.entity.User;
import com.microcommerce.surgeride_api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RideService {
    private final SurgeZoneService surgeZoneService;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;
    private final DriverLocationService driverLocationService;
    private final RideRepository rideRepository;
    private final UserRepository userRepository;
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
        List<String> nearbyDrivers = driverLocationService.getNearbyDrivers(
                requestDto.getStartLatitude(),
                requestDto.getStartLongitude(),
                5.0);

        if (nearbyDrivers.isEmpty()) {
            return "Bölgenizde uygun araç bulunamadı";
        }
        String selectedDriverId = nearbyDrivers.get(0);
        RLock rlock = redissonClient.getLock("driver:lock:" + selectedDriverId);

        try{
            User rider = userRepository.findById(requestDto.getRiderId()).orElseThrow(() -> new RuntimeException("Yolcu Bulunamadı!"));
            User driver = userRepository.findById(Long.parseLong(selectedDriverId)).orElseThrow(() -> new RuntimeException("Sürücü bulunamadı!"));

            double basePriceDouble = 60.0 + (requestDto.getDistanceInKm() * 20.0);
            double endPriceDouble = basePriceDouble + liveMultiplier;
            String startLoc = requestDto.getStartLatitude() + "," + requestDto.getStartLongitude();
            String endLoc = requestDto.getEndLatitude() + "," + requestDto.getEndLongitude();

            Ride newRide = Ride.builder()
                    .rider(rider)
                    .driver(driver)
                    .startLocation(startLoc)
                    .endLocation(endLoc)
                    .basePrice(BigDecimal.valueOf(basePriceDouble))
                    .surgeMultiplier(BigDecimal.valueOf(liveMultiplier))
                    .endPrice(BigDecimal.valueOf(endPriceDouble))
                    .status(RideStatus.ACCEPTED)
                    .build();
            rideRepository.save(newRide);
            boolean isAcquired = rlock.tryLock(0,10, TimeUnit.SECONDS);

            if(!isAcquired){
                return "Araç başka bir yolcuyla eşleşti lütfen tekrar deneyiniz";
            }
            stringRedisTemplate.opsForGeo().remove("driver_locations" , selectedDriverId);
            return "Tebrikler! Sürücü : "+selectedDriverId+"sizinle eşleşti.Araç yola çıktı";
        }catch (InterruptedException exception){
            Thread.currentThread().interrupt();
            throw new RuntimeException("Sistem kesintiye uğradı");
        }finally {
            if(rlock.isHeldByCurrentThread()){
                rlock.unlock();
            }
        }

    }

}
