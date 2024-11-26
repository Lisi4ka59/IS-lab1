package com.kindred.islab1.authentication;



import com.kindred.islab1.authentication.dto.LoginRequest;
import com.kindred.islab1.authentication.service.AuthService;
import com.kindred.islab1.entities.User;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminEndpoint() {
        return "This is an admin endpoint";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public String userEndpoint() {
        return "This is a user endpoint";
    }

    @GetMapping("/owner")
    @PreAuthorize("hasRole('OWNER')")
    public String ownerEndpoint() {
        return "This is an owner endpoint";
    }

    @GetMapping("/secure")
    public String secureEndpoint() {
        return "This is a secure endpoint";
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginByUsername(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody User userRegistrationInfo) throws MessagingException {
        return authService.register(userRegistrationInfo);
    }

    @GetMapping("/send-activation")
    public ResponseEntity<Map<String, String>> sendActivation(@RequestParam("email") String email) throws MessagingException {
        return authService.sendActivation(email);
    }

    @GetMapping("/activate")
    public ResponseEntity<Map<String, String>> activateAccount(@RequestParam("token") String token) {
        return authService.activateAccount(token);
    }

    @GetMapping("/add-administrator-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> addAdministratorRole(@RequestParam("user") String username) {
        return authService.addAdministratorRole(username);
    }

    @GetMapping("/remove-administrator-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> removeAdministratorRole(@RequestParam("user") String username) {
        return authService.removeAdministratorRole(username);
    }

    @GetMapping("/delete-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteUser(@RequestParam("user") String username) {
        return authService.deleteUser(username);
    }
}
