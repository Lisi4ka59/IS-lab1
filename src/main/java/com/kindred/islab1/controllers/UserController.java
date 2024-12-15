package com.kindred.islab1.controllers;

import com.kindred.islab1.authentication.RequestStatus;
import com.kindred.islab1.authentication.dto.UpdateUserRequest;
import com.kindred.islab1.authentication.mapper.UserMapper;
import com.kindred.islab1.authentication.service.UserService;
import com.kindred.islab1.entities.AdminRequest;
import com.kindred.islab1.entities.User;
import com.kindred.islab1.repositories.AdminRequestRepository;
import com.kindred.islab1.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("api/users")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AdminRequestRepository adminRequestRepository;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/profile/{username}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable String username) {
        Map<String, Object> response = new HashMap<>();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with username: " + username + " not found"));
        response.put("user", UserMapper.INSTANCE.toUserResponse(user));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> response = new HashMap<>();
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with username: " + userDetails.getUsername() + " not found"));
        response.put("user", UserMapper.INSTANCE.toLoginResponse(user));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/profile/delete")
    public ResponseEntity<Map<String, Object>> deleteProfile(@AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> response = new HashMap<>();
        userRepository.delete(userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with username: " + userDetails.getUsername() + " not found")));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/profile/update")
    public ResponseEntity<Map<String, Object>> updateProfile(@RequestBody UpdateUserRequest updateUserRequest, @AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> response = new HashMap<>();
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with username: " + userDetails.getUsername() + " not found"));
        user.setName(updateUserRequest.getName());
        user.setSurname(updateUserRequest.getSurname());
        user.setPhoneNumber(updateUserRequest.getPhoneNumber());
        user.setAboutUser(updateUserRequest.getAboutUser());
        userService.save(user);
        response.put("user", user);
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

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/admin-requests")
    public ResponseEntity<Map<String, Object>> createAdminRequest(@RequestBody String description, @AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> response = new HashMap<>();
        if (adminRequestRepository.existsAdminRequestByUsername(userDetails.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Admin request already exists");
        }
        AdminRequest request = new AdminRequest();
        request.setDescription(description);
        request.setStatus(RequestStatus.SEND);
        request.setUserId(userRepository.findByUsername(userDetails.getUsername()).orElseThrow().getId());
        request.setUsername(userDetails.getUsername());
        response.put("adminRequest", adminRequestRepository.save(request));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/admin-request")
    public ResponseEntity<Map<String, Object>> getAdminRequest(@AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> response = new HashMap<>();
        Optional<AdminRequest> adminRequest = adminRequestRepository.findAdminRequestByUsernameAndStatus(userDetails.getUsername(), RequestStatus.SEND);
        if (adminRequest.isPresent()) {
            response.put("adminRequest", adminRequest);
        } else {
            response.put("adminRequest", adminRequestRepository.findAdminRequestByUsernameAndStatusOrderByIdDesc(userDetails.getUsername(), RequestStatus.DENIED).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin request does not exists")));
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin-requests")
    public ResponseEntity<Map<String, Object>> getAllAdminRequest() {
        Map<String, Object> response = new HashMap<>();
        response.put("adminRequests", adminRequestRepository.findAllByStatus(RequestStatus.SEND).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin requests doesn't exist")));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
