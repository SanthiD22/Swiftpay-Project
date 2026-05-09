package com.project.identity_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedEvent {

    private Integer userId;
    private String email; // userful when notification needs to send, then other service listen to the same event, without modifying the identity-service
}
