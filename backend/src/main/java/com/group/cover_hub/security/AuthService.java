package com.group.cover_hub.security;

import com.group.cover_hub.model.dto.RegisterRequest;
import com.group.cover_hub.model.entity.User;
import com.group.cover_hub.model.enums.UserRole;
import com.group.cover_hub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String login(String email, String password){
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, password));
        return jwtService.generateToken(authentication.getName());
    }

    public void register(RegisterRequest request){
        User user = new User();
        user.setName(request.getName());
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new RuntimeException("Пользователь с такой почтой уже зарегистрирован.");
        }
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.ROLE_USER);
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

}
