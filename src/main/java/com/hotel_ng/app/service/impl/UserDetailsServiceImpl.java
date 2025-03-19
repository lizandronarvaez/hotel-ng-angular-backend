package com.hotel_ng.app.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.hotel_ng.app.repository.UserRepository;
import com.hotel_ng.app.service.interfaces.IUserDetailsService;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements IUserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        String messageUserNotFound = String.format("El usuario %s no existe", username);

        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(messageUserNotFound));
    }

}
