package com.datmai.moviereservation.config;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.datmai.moviereservation.service.UserService;

@Component("userDetailService") // Override Bean
public class UserDetailsCustom implements UserDetailsService {
    private final UserService userService;

    public UserDetailsCustom(UserService userService) {
        this.userService = userService;
    }

    // Need this function for the user authentication process in the
    // DaoAuthenticationProvider, because this is where we authenticate user fetched
    // from Database
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.datmai.moviereservation.domain.User user = this.userService.fetchUserByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username/ password is not valid");
        }

        return new User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

}
