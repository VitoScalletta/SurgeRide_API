package com.microcommerce.surgeride_api.ride.controller;

import com.microcommerce.surgeride_api.ride.service.SurgePricingService;
import jakarta.persistence.Entity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rides")
@RequiredArgsConstructor
public class RideController {
    private final SurgePricingService surgePricingService;

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
}
