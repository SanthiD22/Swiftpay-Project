package com.project.identity_service.service;

import com.netflix.discovery.converters.Auto;
import com.project.identity_service.dto.CustomUserDetails;
import com.project.identity_service.model.UserCredential;
import com.project.identity_service.repository.UserCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserCredentialRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username){
        Optional<UserCredential> userCredential = repository.findByName(username);
        return userCredential.map(CustomUserDetails::new)
                .orElseThrow(()-> new UsernameNotFoundException("User not found: "+username));
    }
}
