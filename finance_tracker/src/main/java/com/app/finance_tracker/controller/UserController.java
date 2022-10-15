package com.app.finance_tracker.controller;

import com.app.finance_tracker.model.Exeptionls.BadRequestException;
import com.app.finance_tracker.model.dto.UserRegistrationDTO;
import com.app.finance_tracker.model.dto.UserWithoutPasswordDTO;
import com.app.finance_tracker.model.entities.User;
import com.app.finance_tracker.model.repository.UserRepository;
import com.app.finance_tracker.model.utility.validation.UserValidation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class UserController extends MasterControllerForExceptionHandlers {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserValidation userValidation;

    @PostMapping("/register")
    public User registerUser(@RequestBody UserRegistrationDTO userDTO) {
        if(!userValidation.validateEmail(userDTO.getEmail())){
            throw new BadRequestException("Invalid email");
        }
        if(!userValidation.validateUsername(userDTO.getUsername())){
            throw new BadRequestException("Invalid username");
        }
        if(!userValidation.validatePassword(userDTO.getPassword())){
            throw new BadRequestException("Invalid password");
        }
        if(!userDTO.getConfirmPassword().equals(userDTO.getPassword())){
            throw new BadRequestException("Passwords do not match");
        }
        if(!userValidation.validateFirstName(userDTO.getFirstName())){
            throw new BadRequestException("Invalid first name");
        }
        if(!userValidation.validateLastName(userDTO.getLastName())){
            throw new BadRequestException("Invalid last name");
        }
        userRepository.findByEmail(userDTO.getEmail()).ifPresent(user -> {
            throw new BadRequestException("Email already exists");
        });
        userRepository.findByUsername(userDTO.getUsername()).ifPresent(user -> {
            throw new BadRequestException("Username already exists");
        });
        User user = modelMapper.map(userDTO, User.class);
        userRepository.save(user);
        return user;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserWithoutPasswordDTO>> getUser() {
        List<User> users = userRepository.findAll();
         List<UserWithoutPasswordDTO> userWithoutPasswordDTOS = users.stream()
                .map(user -> modelMapper.map(user, UserWithoutPasswordDTO.class))
                .toList();
        return ResponseEntity.ok(userWithoutPasswordDTOS);
    }

//    @Scheduled(cron = "0 0 9 * * 1")
//    @Scheduled(fixedRate = 5000)
//    public void sendEmail() {
//        // send email to user who hasn't logged in for 5 days
//        System.out.println("Sending email...");
//    }
}
