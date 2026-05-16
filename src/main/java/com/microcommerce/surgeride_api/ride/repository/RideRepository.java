package com.microcommerce.surgeride_api.ride.repository;

import com.microcommerce.surgeride_api.ride.entity.Ride;
import com.microcommerce.surgeride_api.ride.enums.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RideRepository extends JpaRepository<Ride,Integer> {
    boolean existsByRiderIdAndStatusIn(Long riderId, List<RideStatus> statuses);
}
