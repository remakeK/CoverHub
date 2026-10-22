package com.group.cover_hub.controller;

import com.group.cover_hub.model.entity.User;
import com.group.cover_hub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/all")
    //@PreAuthorize("hasAnyRole('USER')")
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }


}
