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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
    private final RabbitTemplate rabbitTemplate;
    private final RideRepository rideRepository;

    public String requestRide(RideRequestDto requestDto){
        String zoneId = surgeZoneService.getZoneId(requestDto.getStartLatitude(), requestDto.getStartLongitude());
        String multiplierStr = stringRedisTemplate.opsForValue().get("surge:multiplier:" + zoneId);

        double liveMultiplier = 1.0;

        if(multiplierStr != null){
            liveMultiplier = Double.parseDouble(multiplierStr);
        }
        double differenceBetweenMultipliers = Math.abs(liveMultiplier - requestDto.getAcceptedMultiplier());

        if(differenceBetweenMultipliers > 0.01){
            throw new RuntimeException("Bölgenizdeki fiyatlar güncellendi! Lütfen tekrar deneyiniz");
        }
        rabbitTemplate.convertAndSend(
                com.microcommerce.surgeride_api.Common.Config.RabbitMQConfig.EXCHANGE_NAME,
                "ride_request.new",
                requestDto
        );

        return "Talebiniz alındı, en uygun araç arka planda aranıyor. Lütfen bekleyin...";
        }
    public String completeRide(Long rideId){
       Ride ride = rideRepository.findById(Math.toIntExact(rideId))
               .orElseThrow(() -> new RuntimeException("Yolculuk bulunamadı"));

       if (ride.getStatus() != RideStatus.ACCEPTED){
           throw new RuntimeException("Geçersiz işlem! Devam eden yolculuklar tamamlanabilir mevcut durum : "+ride.getStatus());
       }
       ride.setStatus(RideStatus.COMPLETED);
       rideRepository.save(ride);
       return "Yolculuk tamamlandı Fatura : "+ride.getEndPrice()+"Tl";
    }
}


