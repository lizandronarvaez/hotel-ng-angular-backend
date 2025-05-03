package com.hotel_ng.app.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hotel_ng.app.repository.AdminRepository;
import com.hotel_ng.app.service.interfaces.AdminDetailsService;

@RequiredArgsConstructor
@Service
public class AdminDetailsServiceImpl implements AdminDetailsService {

    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        final String MESSAGE_ERROR_USER = String.format("El usuario %s no existe", username);
        return adminRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(MESSAGE_ERROR_USER));
    }
    
}
