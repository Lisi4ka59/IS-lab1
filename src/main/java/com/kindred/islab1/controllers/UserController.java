package com.kindred.islab1.controllers;



import com.kindred.islab1.authentication.mapper.UserMapper;
import com.kindred.islab1.authentication.service.UserService;
import com.kindred.islab1.entities.User;
import com.kindred.islab1.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("api/users")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/profile/{username}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable String username) {
        Map<String, Object> response = new HashMap<>();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with username: " + username + " not found"));
        response.put("user", UserMapper.INSTANCE.toUserResponse(user));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{userId}/info")
    public ResponseEntity<Map<String, Object>> updateUserInfo(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String aboutUser = request.get("aboutUser");

        User changedUser = userService.updateUserInfo(userId, aboutUser);
        response.put("message", "Information about user updated successfully");
        response.put("user", changedUser);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}
