package com.kindred.islab1.authentication;



import com.kindred.islab1.entities.User;
import com.kindred.islab1.repositories.RoleRepository;
import com.kindred.islab1.repositories.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.kindred.islab1.Constants.ACTIVATION_LINK;
import static com.kindred.islab1.Constants.EMAIL_DOMAIN;


@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
public class AuthController {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;


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

    @PostMapping("/login/username")
    public ResponseEntity<String> loginByUsername(@RequestBody LoginRequest loginRequest) {
        User user = userService.validateUsername(loginRequest);
        if (user == null) {
            return new ResponseEntity<>("Bad login or password", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(jwtTokenProvider.generateToken(user.getUsername()), HttpStatus.OK);
    }

    @PostMapping("/login/email")
    public ResponseEntity<String> loginByEmail(@RequestBody LoginRequest loginRequest) {
        User user = userService.validateEmail(loginRequest);
        if (user == null) {
            return new ResponseEntity<>("Bad email or password", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(jwtTokenProvider.generateToken(user.getUsername()), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User userRegistrationInfo) {
        if (userRegistrationInfo.getUsername() == null || userRegistrationInfo.getEmail() == null || userRegistrationInfo.getUsername().isBlank() || userRegistrationInfo.getEmail().isBlank()) {
            return new ResponseEntity<>("Email and Username are required", HttpStatus.BAD_REQUEST);
        }
        if (!userRegistrationInfo.getEmail().substring(userRegistrationInfo.getEmail().lastIndexOf("@") + 1).equals(EMAIL_DOMAIN)) {
            return new ResponseEntity<>("Email is not valid", HttpStatus.BAD_REQUEST);
        }
        if (userService.findUserWithRolesByEmail(userRegistrationInfo.getEmail()) != null) {
            return new ResponseEntity<>("Email already taken", HttpStatus.CONFLICT);
        }
        if (userService.findUserWithRolesByUsername(userRegistrationInfo.getUsername()) != null) {
            return new ResponseEntity<>("Username already taken", HttpStatus.CONFLICT);
        }
        userService.encodePasswordAndSaveUser(userRegistrationInfo);
        return sendActivation(userRegistrationInfo.getEmail());
    }

    @GetMapping("/send-activation")
    public ResponseEntity<String> sendActivation(@RequestParam("email") String email) {
        User user = userService.findUserWithRolesByEmail(email);
        String activationToken = jwtTokenProvider.generateActivateToken(email);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        try {
            emailService.sendActivationEmail(email, ACTIVATION_LINK + activationToken);
        } catch (MessagingException e) {
            return new ResponseEntity<>(String.format("Error sending activation email: %s",  e), HttpStatus.SERVICE_UNAVAILABLE);
        }
        return new ResponseEntity<>("Activation sent successfully", HttpStatus.OK);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateAccount(@RequestParam("token") String token) {
        if (!jwtTokenProvider.validateActivationToken(token)) {
            return new ResponseEntity<>("Invalid activation token or expired token", HttpStatus.UNAUTHORIZED);
        }
        User user = userService.findUserWithRolesByEmail(jwtTokenProvider.getEmailFromActivationToken(token));
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        if (!user.getRoles().isEmpty()) {
            return new ResponseEntity<>("User is already activated", HttpStatus.CONFLICT);
        }
        user.addRoles(roleRepository.findByName("USER"));
        userRepository.save(user);
        return new ResponseEntity<>("User activated successfully", HttpStatus.OK);
    }

    @GetMapping("/add-administrator-role")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<String> addAdministratorRole(@RequestParam("user") String userName) {
        User user = userService.findUserWithRolesByUsername(userName);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        if (user.getRoles().contains(roleRepository.findByName("ADMIN"))) {
            return new ResponseEntity<>("User is already admin", HttpStatus.CONFLICT);
        }
        user.addRoles(roleRepository.findByName("ADMIN"));
        userRepository.save(user);
        return new ResponseEntity<>(String.format("User %s is now ADMIN", userName), HttpStatus.OK);
    }

    @GetMapping("/remove-administrator-role")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<String> removeAdministratorRole(@RequestParam("user") String userName) {
        User user = userService.findUserWithRolesByUsername(userName);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        user.removeRoles(roleRepository.findByName("ADMIN"));
        userRepository.save(user);
        return new ResponseEntity<>(String.format("User %s is not now ADMIN", userName), HttpStatus.OK);
    }

    @GetMapping("/delete-user")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<String> deleteUser(@RequestParam("user") String userName) {
        User user = userService.findUserWithRolesByUsername(userName);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        userRepository.delete(user);
        return new ResponseEntity<>(String.format("User %s deleted", userName), HttpStatus.OK);
    }

}
