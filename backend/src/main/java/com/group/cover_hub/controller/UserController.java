package com.group.cover_hub.controller;

import com.group.cover_hub.model.dto.response.UserResponse;
import com.group.cover_hub.model.entity.User;
import com.group.cover_hub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/all")
    //@PreAuthorize("hasAnyRole('ADMIN')")
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    //@PreAuthorize("hasAnyRole("USER")")
    public UserResponse getUserById(@PathVariable Long id){
        return userService.getUserById(id);
    }
}
