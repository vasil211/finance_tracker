package com.app.finance_tracker.controller;

import com.app.finance_tracker.model.Exeptionls.BadRequestException;
import com.app.finance_tracker.model.dto.UserLoginDTO;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        BadRequestException exception = validateCredentials(userDTO);
        if (exception != null) {
            throw exception;
        }
        userRepository.findByEmail(userDTO.getEmail()).ifPresent(user -> {
            throw new BadRequestException("Email already exists");
        });
        userRepository.findByUsername(userDTO.getUsername()).ifPresent(user -> {
            throw new BadRequestException("Username already exists");
        });
        User user = modelMapper.map(userDTO, User.class);
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok(modelMapper.map(user, UserWithoutPasswordDTO.class));
    }

    @PostMapping("/login")
    public ResponseEntity<UserWithoutPasswordDTO> loginUser(@RequestBody UserLoginDTO userDTO) {
        User user = userRepository.findByUsername(userDTO.getUsername()).orElseThrow(() -> new BadRequestException("Invalid username"));
        if (!encoder.matches(userDTO.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid password");
        }
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        UserWithoutPasswordDTO userWithoutPasswordDTO = modelMapper.map(user, UserWithoutPasswordDTO.class);
        return ResponseEntity.ok(userWithoutPasswordDTO);
    }

    @PutMapping("/updateProfile")
    public ResponseEntity<UserWithoutPasswordDTO> updateUser(@RequestBody UserRegistrationDTO userDTO) {
        Optional<User> optUser = userRepository.findById(userDTO.getId());
        if (optUser.isEmpty()) {
            throw new BadRequestException("User does not exist");
        }
        User user = optUser.get();
        BadRequestException exception = validateCredentials(userDTO);
        if (exception != null) {
            throw exception;
        }
        if (!user.getEmail().equals(userDTO.getEmail())) {
            userRepository.findByEmail(userDTO.getEmail()).ifPresent(user1 -> {
                throw new BadRequestException("Email already exists");
            });
        }
        if (!user.getUsername().equals(userDTO.getUsername())) {
            userRepository.findByUsername(userDTO.getUsername()).ifPresent(user1 -> {
                throw new BadRequestException("Username already exists");
            });
        }
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(encoder.encode(userDTO.getPassword()));
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        userRepository.save(user);
        return ResponseEntity.ok(modelMapper.map(user, UserWithoutPasswordDTO.class));
    }

    private BadRequestException validateCredentials(UserRegistrationDTO userDTO) {
        if (!userValidation.validateEmail(userDTO.getEmail())) {
            return new BadRequestException("Invalid email");
        }
        if (!userValidation.validateUsername(userDTO.getUsername())) {
            return new BadRequestException("Invalid username");
        }
        if (!userValidation.validatePassword(userDTO.getPassword())) {
            return new BadRequestException("Invalid password");
        }
        if (!userDTO.getConfirmPassword().equals(userDTO.getPassword())) {
            return new BadRequestException("Passwords do not match");
        }
        if (!userValidation.validateFirstName(userDTO.getFirstName())) {
            return new BadRequestException("Invalid first name");
        }
        if (!userValidation.validateLastName(userDTO.getLastName())) {
            return new BadRequestException("Invalid last name");
        }
        return null;
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
