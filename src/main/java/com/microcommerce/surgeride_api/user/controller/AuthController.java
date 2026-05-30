package com.microcommerce.surgeride_api.user.controller;

import com.microcommerce.surgeride_api.Common.security.JwtService;
import com.microcommerce.surgeride_api.user.dto.LoginRequestDto;
import com.microcommerce.surgeride_api.user.dto.RegisterRequestDto;
import com.microcommerce.surgeride_api.user.entity.User;
import com.microcommerce.surgeride_api.user.enums.StatusType;
import com.microcommerce.surgeride_api.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        User newUser = User.builder()
                .name(requestDto.getName())
                .password(requestDto.getPassword())
                .email(requestDto.getEmail())
                .userType(requestDto.getUserType())
                .status(StatusType.AVAILABLE)
                .build();

        userRepository.save(newUser);
        return ResponseEntity.ok("Kayıt başarılı.");
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isPresent() && userOptional.get().getPassword().equals(request.getPassword())) {

            String token = jwtService.generateJwtToken(request.getEmail());

            return ResponseEntity.ok(token);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Hatalı e-posta veya şifre!");
    }
}
