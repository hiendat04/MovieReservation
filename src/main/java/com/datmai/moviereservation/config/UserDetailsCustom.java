package com.datmai.moviereservation.config;

import java.util.*;
import java.util.stream.Collectors;

import com.datmai.moviereservation.domain.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.datmai.moviereservation.service.UserService;

@Component("userDetailService")
@RequiredArgsConstructor
@Slf4j(topic = "USER-DETAIL-CUSTOM")
public class UserDetailsCustom implements UserDetailsService {
    private final UserService userService;

    // Need this function for the user authentication process in the
    // DaoAuthenticationProvider, because this is where we authenticate user fetched
    // from Database
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.datmai.moviereservation.domain.User user = this.userService.fetchUserByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username/ password is not valid");
        }
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        log.info("User {} with authority {}", username, authorities);

        return new User(
                user.getEmail(),
                user.getPassword(),
                authorities);
    }
}
