package com.microcommerce.surgeride_api.user.repository;

import com.microcommerce.surgeride_api.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {
}
