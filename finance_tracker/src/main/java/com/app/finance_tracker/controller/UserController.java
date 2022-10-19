package com.app.finance_tracker.controller;

import com.app.finance_tracker.model.Exeptionls.InvalidArgumentsException;
import com.app.finance_tracker.model.dto.userDTO.UserLoginDTO;
import com.app.finance_tracker.model.dto.userDTO.UserRegistrationDTO;
import com.app.finance_tracker.model.dto.userDTO.UserWithoutPasswordDTO;
import com.app.finance_tracker.model.entities.User;
import com.app.finance_tracker.model.repository.UserRepository;
import com.app.finance_tracker.model.utility.service.UserService;
import com.app.finance_tracker.model.utility.validation.UserValidation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class UserController extends MasterControllerForExceptionHandlers {

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserWithoutPasswordDTO> registerUser(@RequestBody UserRegistrationDTO userDTO) {
       User user = userService.registerUser(userDTO);
       return ResponseEntity.ok(modelMapper.map(user, UserWithoutPasswordDTO.class));
    }

    @PostMapping("/login")
    public ResponseEntity<UserWithoutPasswordDTO> loginUser(@RequestBody UserLoginDTO userDTO) {
        User user = userService.loginUser(userDTO);
        // todo add session
        return ResponseEntity.ok(modelMapper.map(user, UserWithoutPasswordDTO.class));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser() {
        // todo invalidate session
        return ResponseEntity.ok("Logout successful");
    }


    @PutMapping("/updateProfile")
    public ResponseEntity<UserWithoutPasswordDTO> updateUser(@RequestBody UserRegistrationDTO userDTO) {
        // todo check if user is logged in
        User user = userService.updateUserProfile(userDTO);
        return ResponseEntity.ok(modelMapper.map(user, UserWithoutPasswordDTO.class));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserWithoutPasswordDTO>> getAllUsers() {
        // todo check if user is logged in

        // todo do we need to return all users?
        List<UserWithoutPasswordDTO> users = userService.getAllUsers();

        return ResponseEntity.ok(users);
    }

//    @Scheduled(cron = "0 0 9 * * 1")
//    @Scheduled(fixedRate = 5000)
//    public void sendEmail() {
//        // send email to user who hasn't logged in for 5 days
//        System.out.println("Sending email...");
//    }
}
