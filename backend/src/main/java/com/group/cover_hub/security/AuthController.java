package com.group.cover_hub.security;

import com.group.cover_hub.model.dto.request.LoginRequest;
import com.group.cover_hub.model.dto.request.RegisterRequest;
import com.group.cover_hub.security.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @RequestBody @Valid LoginRequest request
    ) {
        return authService.login(
                request.getEmail(),
                request.getPassword()
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(
            @RequestBody Map<String, String> body
    ) {
        return authService.refresh(body);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestBody Map<String, String> body
    ) {
        authService.logout(body);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody @Valid RegisterRequest request
    ) {
        authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Пользователь успешно зарегистрирован.");
    }
}