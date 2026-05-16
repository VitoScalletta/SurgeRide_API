package com.microcommerce.surgeride_api.ride.repository;

import com.microcommerce.surgeride_api.ride.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RideRepository extends JpaRepository<Ride,Integer> {

}
