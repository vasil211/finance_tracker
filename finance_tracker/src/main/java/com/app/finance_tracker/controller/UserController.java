package com.app.finance_tracker.controller;

import com.app.finance_tracker.model.Exeptionls.InvalidArgumentsException;
import com.app.finance_tracker.model.dto.userDTO.UserLoginDTO;
import com.app.finance_tracker.model.dto.userDTO.UserRegistrationDTO;
import com.app.finance_tracker.model.dto.userDTO.UserWithoutPasswordDTO;
import com.app.finance_tracker.model.entities.User;
import com.app.finance_tracker.model.repository.UserRepository;
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
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserValidation userValidation;
    @Autowired
    private PasswordEncoder encoder;

    @PostMapping("/register")
    public ResponseEntity<UserWithoutPasswordDTO> registerUser(@RequestBody UserRegistrationDTO userDTO) {
        userValidation.validateUSerForRegistration(userDTO);
        User user = modelMapper.map(userDTO, User.class);
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok(modelMapper.map(user, UserWithoutPasswordDTO.class));
    }


    @PostMapping("/login")
    public ResponseEntity<UserWithoutPasswordDTO> loginUser(@RequestBody UserLoginDTO userDTO) {
        User user = userRepository.findByUsername(userDTO.getUsername())
                .orElseThrow(() -> new InvalidArgumentsException("Invalid username"));
        if (!encoder.matches(userDTO.getPassword(), user.getPassword())) {
            throw new InvalidArgumentsException("Invalid password");
        }
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        UserWithoutPasswordDTO userWithoutPasswordDTO = modelMapper.map(user, UserWithoutPasswordDTO.class);
        return ResponseEntity.ok(userWithoutPasswordDTO);
    }

    @PutMapping("/updateProfile")
    public ResponseEntity<UserWithoutPasswordDTO> updateUser(@RequestBody UserRegistrationDTO userDTO) {
        User user = userValidation.validateUserForUpdateToTheProfile(userDTO);
        user.setPassword(encoder.encode(userDTO.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok(modelMapper.map(user, UserWithoutPasswordDTO.class));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserWithoutPasswordDTO>> getAllUsers() {
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
