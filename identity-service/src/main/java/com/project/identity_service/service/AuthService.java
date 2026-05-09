package com.project.identity_service.service;

import com.project.identity_service.dto.UserCreatedEvent;
import com.project.identity_service.dto.UserCredentialDto;
import com.project.identity_service.dto.UserResponseDto;
import com.project.identity_service.model.UserCredential;
import com.project.identity_service.repository.UserCredentialRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserCredentialRepository repository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;

    @Transactional
    public UserResponseDto saveUser(UserCredentialDto credentialDto){
        UserCredential userCredential = UserCredential.builder()
                .name(credentialDto.getUsername())
                .email(credentialDto.getEmail())
                .password(passwordEncoder.encode(credentialDto.getPassword()))
                .build();
        UserCredential saved = repository.save(userCredential);

        UserCreatedEvent event = new UserCreatedEvent(saved.getId(), saved.getEmail());
        try{
            kafkaTemplate.send("user-registration-topic", event);
            log.info("sent UserCreatedEvent for ID: {}",saved.getId());
        }
        catch(Exception e){
            log.error("Failed to send kafka message", e);
        }

        return UserResponseDto.builder()
                .id(saved.getId())
                .email(saved.getEmail())
                .userName(saved.getName())
                .build();
    }

    public String generateToken(String userName){
        return jwtService.generateToken(userName);
    }

    public void validateToken(String token){
        jwtService.validateToken(token);
    }

    public boolean checkUserExists(Integer userId){
        return repository.existsById(userId);
    }

}
