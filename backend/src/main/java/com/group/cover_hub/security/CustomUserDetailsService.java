package com.group.cover_hub.security;

import com.group.cover_hub.repository.UserRepository;
import com.group.cover_hub.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username){
        User user = userRepository.findByEmail(username).orElseThrow(() ->
                new UsernameNotFoundException("Пользователь не найден"));
        return new CustomUserDetails(user);
    }
}
