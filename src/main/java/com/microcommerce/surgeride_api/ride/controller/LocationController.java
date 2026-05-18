package com.microcommerce.surgeride_api.ride.controller;

import com.microcommerce.surgeride_api.ride.dto.LocationUpdateRequest;
import com.microcommerce.surgeride_api.ride.service.DriverLocationService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {
    private final DriverLocationService driverLocationService;

    @PostMapping("/driver")
    public ResponseEntity<String> updateLocation(@RequestBody LocationUpdateRequest request) {
        driverLocationService.updateLocation(request);
        return ResponseEntity.ok("Driver location updated");
    }
}
