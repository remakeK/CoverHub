package com.group.cover_hub.model.service;

import com.group.cover_hub.model.entity.User;
import com.group.cover_hub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @
    public List<User> getUsers(){
        return userRepository.findAll();
    }
}
