package com.microcommerce.surgeride_api.ride.service;

import com.microcommerce.surgeride_api.Common.Config.RabbitMQConfig;
import com.microcommerce.surgeride_api.ride.dto.RideRequestDto;
import com.microcommerce.surgeride_api.ride.entity.Ride;
import com.microcommerce.surgeride_api.ride.enums.RideStatus;
import com.microcommerce.surgeride_api.ride.repository.RideRepository;
import com.microcommerce.surgeride_api.user.entity.User;
import com.microcommerce.surgeride_api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RideRequestConsumer {
    private final DriverLocationService driverLocationService;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;
    private final UserRepository userRepository;
    private final RideRepository rideRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void processRideRequest(RideRequestDto  rideRequestDto) {
        List<String> nearbyDrivers = driverLocationService.getNearbyDrivers(
                rideRequestDto.getStartLatitude(),
                rideRequestDto.getStartLongitude(),
                5.0
        );

        if (nearbyDrivers.isEmpty()) {
            log.warn("No nearby drivers found");
            return;
        }

        String selectedDriverId = nearbyDrivers.get(0);
        RLock rlock = redissonClient.getLock("driver:lock:" + selectedDriverId);

        try{
            User rider = userRepository.findById(rideRequestDto.getRiderId()).orElseThrow(() -> new RuntimeException("Rider not found"));
            User driver = userRepository.findById(Long.parseLong(selectedDriverId)).orElseThrow(() -> new RuntimeException("Driver not found"));

            double basePriceDouble = 60.0 + (rideRequestDto.getDistanceInKm()*20.0);
            double endPriceDouble = basePriceDouble * rideRequestDto.getAcceptedMultiplier();
            String startLoc = rideRequestDto.getStartLatitude()+","+rideRequestDto.getStartLongitude();
            String endLoc = rideRequestDto.getEndLatitude()+","+rideRequestDto.getEndLongitude();

            Ride newRide = Ride.builder()
                    .rider(rider)
                    .driver(driver)
                    .startLocation(startLoc)
                    .endLocation(endLoc)
                    .basePrice(BigDecimal.valueOf(basePriceDouble))
                    .surgeMultiplier(BigDecimal.valueOf(rideRequestDto.getAcceptedMultiplier()))
                    .endPrice(BigDecimal.valueOf(endPriceDouble))
                    .status(RideStatus.ACCEPTED)
                    .build();
            rideRepository.save(newRide);

            boolean isAcquired = rlock.tryLock(0, 10, TimeUnit.SECONDS);

            if(!isAcquired){
                log.warn("Sürücü başka bir yolcuyla eşleşti Yolcu ID : {}", rideRequestDto.getRiderId());
                return;
            }
            stringRedisTemplate.opsForGeo().remove("driver_locations", selectedDriverId);
            log.info("Tebrikler! Sürücü : {} yolcu : {} ile eşleşti Araç yola çıktı",selectedDriverId,rideRequestDto.getRiderId());
            String channel = "/topic/ride-updates/"+rideRequestDto.getRiderId();
            String message = "Aracınız yola çıktı Sürücü ID: "+ selectedDriverId;
            simpMessagingTemplate.convertAndSend(channel, message);
        }catch (Exception exception){
            Thread.currentThread().interrupt();
            log.error("Kuyruktaki mesaj işlenirken KRİTİK HATA oluştu. Mesaj çöpe atılıyor. Hata: {}", exception.getMessage(), exception);
        }finally {
            if (rlock.isHeldByCurrentThread()) {
                rlock.unlock();
            }
        }
    }
}
