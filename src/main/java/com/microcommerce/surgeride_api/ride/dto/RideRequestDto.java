package com.microcommerce.surgeride_api.ride.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RideRequestDto {
    Long riderId;
    double startLatitude;
    double startLongitude;
    double endLatitude;
    double endLongitude;
    double distanceInKm;
    double acceptedMultiplier;
}
