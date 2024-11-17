package com.kindred.islab1.authentication;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.kindred.islab1.repositories.UserRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetail implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.kindred.islab1.entities.User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        Set<GrantedAuthority> grantedAuthorities = user.getRole().stream()
                .map(role -> new SimpleGrantedAuthority(String.format("ROLE_%s", role.getName())))
                .collect(Collectors.toSet());
        return new User(user.getUsername(), user.getPassword(), true, true, true, true, grantedAuthorities);
    }
}
