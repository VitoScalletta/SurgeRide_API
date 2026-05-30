package com.microcommerce.surgeride_api.user.dto;

import com.microcommerce.surgeride_api.user.enums.UserType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDto {
    private String name;
    private String email;
    private String password;
    private UserType userType;
}
