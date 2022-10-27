package com.app.finance_tracker.service;

import com.app.finance_tracker.model.exceptions.InvalidArgumentsException;
import com.app.finance_tracker.model.dto.userDTO.UserLoginDTO;
import com.app.finance_tracker.model.dto.userDTO.UserRegistrationDTO;
import com.app.finance_tracker.model.dto.userDTO.UserWithoutPasswordDTO;
import com.app.finance_tracker.model.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService extends AbstractService {

    @Autowired
    private PasswordEncoder encoder;

    public User loginUser(UserLoginDTO userDTO) {
        if(!userValidation.validateUsername(userDTO.getUsername())){
            throw new InvalidArgumentsException("Invalid credentials");
        }
        User user = userRepository.findByUsername(userDTO.getUsername())
                .orElseThrow(() -> new InvalidArgumentsException("Invalid credentials"));
        if(!encoder.matches(userDTO.getPassword(), user.getPassword())){
            throw new InvalidArgumentsException("Invalid credentials");
        }
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        return user;
    }

    public User registerUser(UserRegistrationDTO userDTO) {
        userValidation.validateUSerForRegistration(userDTO);
        User user = modelMapper.map(userDTO, User.class);
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
        return user;
    }

    public User updateUserProfile(UserRegistrationDTO userDTO) {
        userValidation.validateUserForUpdateToTheProfile(userDTO);
        User user = modelMapper.map(userDTO, User.class);
        user.setPassword(encoder.encode(userDTO.getPassword()));
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        userRepository.save(user);
        return user;
    }

    public List<UserWithoutPasswordDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> modelMapper.map(user, UserWithoutPasswordDTO.class))
                .toList();
    }

    public void sendEmails() {
        List<User> users = userRepository.findAllWhereLastLoginIsBefore(LocalDateTime.now().minusDays(5));
        String subject = "Finance Tracker";
        String text = "You haven't logged in for 5 days. Please log in to your account.";
        users.forEach(user -> emailService.sendSimpleMessage(user.getEmail(), subject, text));
    }
}
