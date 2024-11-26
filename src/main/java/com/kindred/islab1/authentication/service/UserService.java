package com.kindred.islab1.authentication.service;


import com.kindred.islab1.authentication.dto.LoginRequest;
import com.kindred.islab1.entities.User;
import com.kindred.islab1.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void save(User user) {
        userRepository.save(user);
    }

    public void encodePasswordAndSaveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        save(user);
    }

    public User validateUser(LoginRequest loginInfo) {
        Optional<User> optionalUser = loginInfo.getLogin().contains("@")
                ? userRepository.findByEmail(loginInfo.getLogin())
                : userRepository.findByUsername(loginInfo.getLogin());
        User user = optionalUser.orElseThrow(() -> new BadCredentialsException("Invalid login or password"));
        if (!passwordEncoder.matches(loginInfo.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid login or password");
        }
        return user;
    }

    public User updateUserInfo(Long userId, String aboutUser) {
        if (aboutUser == null || aboutUser.isBlank()) {
            throw new IllegalArgumentException("Information can't be empty");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID" + userId + "not found"));
        user.setAboutUser(aboutUser);
        return userRepository.save(user);
    }

}