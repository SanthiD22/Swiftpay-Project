package com.project.identity_service.controller;

import com.project.identity_service.dto.AuthRequest;
import com.project.identity_service.dto.UserCredentialDto;
import com.project.identity_service.dto.UserResponseDto;
import com.project.identity_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> addNewUser(@RequestBody UserCredentialDto userCredentialDto){
        UserResponseDto response = authService.saveUser(userCredentialDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/token")
    public ResponseEntity<Map<String,String>> getToken(@RequestBody AuthRequest authRequest){
        try{
           Authentication authenticate = authenticationManager.authenticate(
                   new UsernamePasswordAuthenticationToken(authRequest.getUsername(),authRequest.getPassword())
           );
           if(authenticate.isAuthenticated()){
               String token = authService.generateToken(authRequest.getUsername());
               Map<String,String> response = new HashMap<>();
               response.put("token",token);
               return ResponseEntity.ok(response);
           }
           else {
               return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
           }
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestParam("token") String token){
        try{
            authService.validateToken(token);
            return ResponseEntity.ok("Token is valid");
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/validate-user/{userId}")
    public ResponseEntity<Boolean> validateUser(@PathVariable Integer userId){
        return ResponseEntity.ok(authService.checkUserExists(userId));
    }
}
