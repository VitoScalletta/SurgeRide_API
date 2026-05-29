package com.microcommerce.surgeride_api.ride.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RideRequestDto {
    @NotNull(message = "Yolcu Id boş bırakılamaz!")
    Long riderId;
    Long driverId;

    @NotNull(message = "Başlangıç enlemi zorunludur!")
    @DecimalMin(value = "-90.0",message = "Geçersiz enlem!")
    @DecimalMax(value = "90.0",message = "Geçersiz enlem!")
    double startLatitude;

    @NotNull(message = "Başlangıç boylam zorunludur")
    @DecimalMin(value = "-180.0",message = "Geçersiz boylam!")
    @DecimalMax(value = "180.0",message = "Geçersiz boylam")
    double startLongitude;

    double endLatitude;
    double endLongitude;
    @NotNull(message = "Mesafe boş bırakılamaz")
    @DecimalMin(value = "1.0",message = "mesafe en az 1km olmalıdır")
    double distanceInKm;
    @NotNull(message = "Kabul edilen çarpan boş olamaz")
    @DecimalMin(value = "1.0",message = "Dinamik fiyat çarpanı 1.0'ın altında olamaz!")
    double acceptedMultiplier;
}
