package com.group.cover_hub.security.service;

import com.group.cover_hub.model.dto.RegisterRequest;
import com.group.cover_hub.model.entity.RefreshSession;
import com.group.cover_hub.model.entity.User;
import com.group.cover_hub.model.enums.UserRole;
import com.group.cover_hub.repository.RefreshSessionRepository;
import com.group.cover_hub.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshSessionRepository refreshSessionRepository;

    @Transactional
    public ResponseEntity<Map<String, String>> login(String email, String password){
        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Неверный email или пароль"
            );
        }

        String username = authentication.getName();
        User user = userRepository.findByEmail(username).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "User not found"
                )
        );
        String accessToken = jwtService.generateAccessToken(username);
        String refreshToken = jwtService.generateRefreshToken(username);
        RefreshSession session = new RefreshSession();
        session.setUser(user);
        session.setTokenHash(jwtService.hashToken(refreshToken));
        session.setCreatedAt(LocalDateTime.now());
        session.setExpiresAt(LocalDateTime.now().plusDays(7));
        session.setRevoked(false);

        refreshSessionRepository.save(session);


        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        ));
    }


    @Transactional
    public void logout(Map<String, String> tokens) {
        String refreshToken = tokens.get("refreshToken");

        if (refreshToken == null || !jwtService.isValidRefreshToken(refreshToken)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid refresh token"
            );
        }

        String tokenHash = jwtService.hashToken(refreshToken);
        RefreshSession session = refreshSessionRepository.findByTokenHash(tokenHash)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Invalid refresh token"
                        )
                );

        if (session.isRevoked()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Session already revoked"
            );
        }

        String username = jwtService.extractUsernameFromRefreshToken(refreshToken);

        if (!session.getUser().getEmail().equals(username)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid refresh token"
            );
        }

        session.setRevoked(true);
        refreshSessionRepository.save(session);
    }


    @Transactional
    public ResponseEntity<Map<String, String>> refresh(Map<String, String> tokens) {
        String refreshToken = tokens.get("refreshToken");

        if (refreshToken == null || !jwtService.isValidRefreshToken(refreshToken)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid refresh token"
            );
        }

        String username = jwtService.extractUsernameFromRefreshToken(refreshToken);
        if (userRepository.findByEmail(username).isEmpty()){
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "User not found"
                    );
        }
        String tokenHash = jwtService.hashToken(refreshToken);
        RefreshSession session = refreshSessionRepository.findByTokenHash(tokenHash)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Invalid refresh token"
                        ));
        if (session.isRevoked() || !session.getExpiresAt().isAfter(LocalDateTime.now())){
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Refresh session expired or revoked"
            );
        }
        if (!session.getUser().getEmail().equals(username)){
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid refresh token"
            );
        }

        String newAccessToken = jwtService.generateAccessToken(username);
        String newRefreshToken = jwtService.generateRefreshToken(username);
        session.setTokenHash(jwtService.hashToken(newRefreshToken));
        session.setExpiresAt(LocalDateTime.now().plusDays(7));
        refreshSessionRepository.save(session);

        return ResponseEntity.ok(Map.of(
                "accessToken", newAccessToken,
                "refreshToken", newRefreshToken
        ));
    }

    public void register(RegisterRequest request){
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Пользователь с такой почтой уже зарегистрирован."
            );
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.ROLE_USER);
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.saveAndFlush(user);

        System.out.println(
                "REGISTER SAVED: id=" + savedUser.getId()
                        + ", email=" + savedUser.getEmail()
        );
    }

}
