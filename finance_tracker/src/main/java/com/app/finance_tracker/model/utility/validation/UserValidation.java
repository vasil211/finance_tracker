package com.app.finance_tracker.model.utility.validation;

import com.app.finance_tracker.model.Exeptionls.BadRequestException;
import com.app.finance_tracker.model.Exeptionls.InvalidArgumentsException;
import com.app.finance_tracker.model.dto.userDTO.UserRegistrationDTO;
import com.app.finance_tracker.model.entities.User;
import com.app.finance_tracker.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserValidation {

    @Autowired
    private UserRepository userRepository;

    public boolean validateEmail(String email) {
        return email.matches("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
    }

    public boolean validateUsername(String username) {
        return username.matches("^(?=[a-zA-Z0-9._]{8,20}$)(?!.*[_.]{2})[^_.].*[^_.]$");
    }

    public boolean validatePassword(String password) {
        return password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!.])(?=\\S+$).{8,20}$");
    }

    public boolean validateFirstName(String firstName) {
        return firstName.matches("^[A-Z][a-z]{2,20}$");
    }

    public boolean validateLastName(String lastName) {
        return lastName.matches("^[A-Z][a-z]{2,20}$");
    }

    public void validateUSerForRegistration(UserRegistrationDTO userDTO) {
        validateCredentials(userDTO);
        userRepository.findByEmail(userDTO.getEmail()).ifPresent(user -> {
            throw new InvalidArgumentsException("Email already exists");
        });
        userRepository.findByUsername(userDTO.getUsername()).ifPresent(user -> {
            throw new InvalidArgumentsException("Username already exists");
        });
    }

    public User validateUserForUpdateToTheProfile(UserRegistrationDTO userDTO) {
        Optional<User> optUser = userRepository.findById(userDTO.getId());
        if (optUser.isEmpty()) {
            throw new BadRequestException("User does not exist");
        }
        validateCredentials(userDTO);
        User user = optUser.get();
        if (!user.getEmail().equals(userDTO.getEmail())) {
            userRepository.findByEmail(userDTO.getEmail()).ifPresent(user1 -> {
                if (user1.getId() != userDTO.getId()) {
                    throw new InvalidArgumentsException("Email already exists");
                }
            });
        }
        if (!user.getUsername().equals(userDTO.getUsername())) {
            userRepository.findByUsername(userDTO.getUsername()).ifPresent(user1 -> {
                if (user1.getId() != userDTO.getId()) {
                    throw new InvalidArgumentsException("Username already exists");
                }
            });
        }
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        return user;
    }

    public void validateCredentials(UserRegistrationDTO userDTO) {
        if (!validateEmail(userDTO.getEmail())) {
            throw new InvalidArgumentsException("Invalid email");
        }
        if (!validateUsername(userDTO.getUsername())) {
            throw new InvalidArgumentsException("Invalid username");
        }
        if (!validatePassword(userDTO.getPassword())) {
            throw new InvalidArgumentsException("Invalid password");
        }
        if (!userDTO.getConfirmPassword().equals(userDTO.getPassword())) {
            throw new InvalidArgumentsException("Passwords do not match");
        }
        if (!validateFirstName(userDTO.getFirstName())) {
            throw new InvalidArgumentsException("Invalid first name");
        }
        if (!validateLastName(userDTO.getLastName())) {
            throw new InvalidArgumentsException("Invalid last name");
        }

    }
}
