package com.microcommerce.surgeride_api.ride.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationUpdateRequest {
    Long driverId;
    Double latitude;
    Double longitude;
}
