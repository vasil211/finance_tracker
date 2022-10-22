package com.app.finance_tracker.controller;

import com.app.finance_tracker.model.Exeptionls.UnauthorizedException;
import com.app.finance_tracker.model.dto.userDTO.UserLoginDTO;
import com.app.finance_tracker.model.dto.userDTO.UserRegistrationDTO;
import com.app.finance_tracker.model.dto.userDTO.UserWithoutPasswordDTO;
import com.app.finance_tracker.model.entities.User;
import com.app.finance_tracker.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController extends AbstractController {

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
    public ResponseEntity<UserWithoutPasswordDTO> loginUser(@RequestBody UserLoginDTO userDTO, HttpServletRequest request) {
        User user = userService.loginUser(userDTO);
        logUser(request, user.getId());
        return ResponseEntity.ok(modelMapper.map(user, UserWithoutPasswordDTO.class));
    }

    @PostMapping("/users/logout")
    public ResponseEntity<String> logoutUser(HttpServletRequest request) {
        request.getSession().invalidate();
        return ResponseEntity.ok("Logout successful");
    }

    @PutMapping("/users")
    public ResponseEntity<UserWithoutPasswordDTO> updateUser(@RequestBody UserRegistrationDTO userDTO, HttpServletRequest session) {
        checkIfLogged(session);
        if (userDTO.getId() != (int) session.getAttribute(USER_ID)) {
            throw new UnauthorizedException("You are not authorized to update this user");
        }
        User user = userService.updateUserProfile(userDTO);
        return ResponseEntity.ok(modelMapper.map(user, UserWithoutPasswordDTO.class));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserWithoutPasswordDTO>> getAllUsers(HttpServletRequest request) {
        checkIfLogged(request);
        // todo do we need to return all users?
        List<UserWithoutPasswordDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    // todo: return user by username
    // todo: return user by first and last name

//    @Scheduled(cron = "0 0 9 * * 1")
//    @Scheduled(fixedRate = 5000)
//    public void sendEmail() {
//        // send email to user who hasn't logged in for 5 days
//        System.out.println("Sending email...");
//    }
}
