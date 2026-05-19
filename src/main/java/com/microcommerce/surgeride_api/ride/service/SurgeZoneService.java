package com.microcommerce.surgeride_api.ride.service;

import ch.hsr.geohash.GeoHash;

public class SurgeZoneService {
    public String getZoneId(double latitude,double longitude){
        GeoHash geoHash = GeoHash.withCharacterPrecision(latitude,longitude,5);
        return geoHash.toBase32();
    }
}
