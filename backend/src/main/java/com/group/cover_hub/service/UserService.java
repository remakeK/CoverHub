package com.group.cover_hub.service;

import com.group.cover_hub.model.dto.response.UserResponse;
import com.group.cover_hub.model.entity.User;
import com.group.cover_hub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public UserResponse getUserById(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return UserResponse.builder()
                .id(user.getId())
                .photoLink(user.getPhotoLink())
                .name(user.getName())
                .description(user.getDescription())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
