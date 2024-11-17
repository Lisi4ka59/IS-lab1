package com.kindred.islab1.authentication.service;


import com.kindred.islab1.authentication.dto.LoginRequest;
import com.kindred.islab1.entities.User;
import com.kindred.islab1.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public void encodePasswordAndSaveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public User validateUser(LoginRequest loginInfo) {
        User user;
        if (loginInfo.getLogin().contains("@")) {
            user = userRepository.findByEmail(loginInfo.getLogin());
        } else {
            user = userRepository.findByUsername(loginInfo.getLogin());
        }
        if (user != null && passwordEncoder.matches(loginInfo.getPassword(), user.getPassword())){
            return user;
        }
        return null;
    }

}