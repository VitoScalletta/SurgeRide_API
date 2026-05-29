package com.microcommerce.surgeride_api;

import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ChaosTest {

    public static void main(String[] args) throws InterruptedException {
        int totalRequests = 500;
        ExecutorService executor = Executors.newFixedThreadPool(50);
        RestTemplate restTemplate = new RestTemplate();

        System.out.println("🚀 TEST BAŞLIYOR: Aynı bölgede 500 Yolcu aynı anda araç arıyor...");

        for (int i = 0; i < totalRequests; i++) {
            executor.execute(() -> {
                try {
                    String url = "http://localhost:8080/api/rides/estimate?startLat=41.0082&startLon=28.9784&distanceInKm=5";
                    restTemplate.getForObject(url, String.class);
                } catch (Exception e) {
                    System.out.println("İstek başarısız: " + e.getMessage());
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        System.out.println("🏁 TEST BİTTİ! Lütfen Grafana ve IntelliJ Loglarına bak!");
    }
}