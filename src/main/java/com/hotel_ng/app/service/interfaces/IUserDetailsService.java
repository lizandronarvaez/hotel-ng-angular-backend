package com.hotel_ng.app.service.interfaces;

import org.springframework.security.core.userdetails.*;

public interface IUserDetailsService extends UserDetailsService {

    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
   
}
