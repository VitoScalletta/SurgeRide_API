package com.microcommerce.surgeride_api.user.entity;

import com.microcommerce.surgeride_api.ride.entity.Ride;
import com.microcommerce.surgeride_api.user.enums.StatusType;
import com.microcommerce.surgeride_api.user.enums.UserType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Builder
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false,unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusType status;

    @OneToMany(mappedBy = "rider",fetch = FetchType.LAZY)
    List<Ride> requestedRides;

    @OneToMany(mappedBy = "driver",fetch = FetchType.LAZY)
    List<Ride> drivenRides;
}
