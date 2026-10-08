package com.group.cover_hub.security;

import com.group.cover_hub.model.dto.LoginRequest;
import com.group.cover_hub.model.dto.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public String login(@RequestBody @Valid LoginRequest request){
        return authService.login(request.getEmail(), request.getPassword());
    }

    @PostMapping("/register")
    public String register(@RequestBody @Valid RegisterRequest request){
        authService.register(request);
        return "Пользователь успешно зарегистрирован.";
    }
}
