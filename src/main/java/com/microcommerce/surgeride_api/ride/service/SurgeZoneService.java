package com.microcommerce.surgeride_api.ride.service;

import ch.hsr.geohash.GeoHash;
import org.springframework.stereotype.Service;

@Service
public class SurgeZoneService {
    public String getZoneId(double latitude,double longitude){
        GeoHash geoHash = GeoHash.withCharacterPrecision(latitude,longitude,5);
        return geoHash.toBase32();
    }
}
