package com.microcommerce.surgeride_api.ride.controller;

import com.microcommerce.surgeride_api.ride.dto.RideRequestDto;
import com.microcommerce.surgeride_api.ride.entity.Ride;
import com.microcommerce.surgeride_api.ride.service.RideService;
import com.microcommerce.surgeride_api.ride.service.SurgePricingService;
import jakarta.persistence.Entity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rides")
@RequiredArgsConstructor
public class RideController {
    private final SurgePricingService surgePricingService;
    private final RideService rideService;
    @GetMapping("/estimate")
    public ResponseEntity<String> getPriceEstimate(
            @RequestParam double startLat,
            @RequestParam double startLon,
            @RequestParam double distanceInKm
    ){
        surgePricingService.recordDemand(999L,startLat,startLon);
        double estimatedPrice = surgePricingService.estimatePrice(startLat,startLon,distanceInKm);
        return ResponseEntity.ok("Tahmini yolculuk tutarı : "+estimatedPrice+"TL");
    }

    @PostMapping("/request")
    public ResponseEntity<String> requestRide(@Valid @RequestBody RideRequestDto requestDto){
        String responseMessage = rideService.requestRide(requestDto);
        return ResponseEntity.ok(responseMessage);
    }
    @PostMapping("/complete/{rideId}")
    public ResponseEntity<String> completeRide(@PathVariable Long rideId){
        String response = rideService.completeRide(rideId);
        return ResponseEntity.ok(response);
    }
}
