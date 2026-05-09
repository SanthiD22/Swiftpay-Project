package com.project.wallet_service.client;

import jakarta.ws.rs.Path;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "identity-service")
public interface IdentityClient {

    @GetMapping("/auth/validate-user/{userId}")
    ResponseEntity<Boolean> checkUserExists(@PathVariable("userId") Integer userId);
}
