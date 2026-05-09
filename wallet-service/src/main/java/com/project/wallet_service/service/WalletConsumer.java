package com.project.wallet_service.service;

import com.project.wallet_service.dto.UserCreatedEvent;
import com.project.wallet_service.model.UserWallet;
import com.project.wallet_service.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class WalletConsumer {

    private final WalletRepository repository;

    @KafkaListener(topics = "user-registration-topic", groupId = "wallet-group")
    public void consumeUserCreatedEvent(UserCreatedEvent event){
        log.info("Received UserCreatedEvent for userId : {}", event.getUserId());

        // Idempotency check: Ensure we don't create duplicate wallets
        if(repository.findByUserId(event.getUserId()).isPresent()){
            log.warn("wallet already exists for userId: {}",event.getUserId());
            return;
        }

        UserWallet wallet = new UserWallet();
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setUserId(event.getUserId());
        wallet.setCurrency("INR");

        repository.save(wallet);
    }

}
