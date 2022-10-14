package com.app.finance_tracker.controller;

import com.app.finance_tracker.model.entities.User;
import com.app.finance_tracker.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        // validate credentials
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        return user;
    }

    @GetMapping("/users")
    public User getUser() {
        User user = new User();
        user.setFirstName("tosho");
        user.setLastName("toshov");
        user.setLastLogin(LocalDateTime.now());
        return user;
    }

}
