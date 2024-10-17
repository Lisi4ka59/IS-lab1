package com.kindred.islab1.authentication;


import com.kindred.islab1.entities.User;
import com.kindred.islab1.repositories.RoleRepository;
import com.kindred.islab1.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;


    public void encodePasswordAndSaveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }


    public User validateUsername(LoginRequest loginInfo) {
        User user = findUserWithRolesByUsername(loginInfo.getData());
        if (user != null && passwordEncoder.matches(loginInfo.getPassword(), user.getPassword())){
            return user;
        }
        return null;
    }

    public User validateEmail(LoginRequest loginInfo) {
        User user = findUserWithRolesByEmail(loginInfo.getData());
        if (user != null && passwordEncoder.matches(loginInfo.getPassword(), user.getPassword())){
            return user;
        }
        return null;
    }

    public User findUserWithRolesByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return null;
        }
        user.setRoles(roleRepository.findByUsers(Set.of(user)));
        return user;
    }

    public User findUserWithRolesByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return null;
        }
        user.setRoles(roleRepository.findByUsers(Set.of(user)));
        return user;
    }
}