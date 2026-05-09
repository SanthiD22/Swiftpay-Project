package com.project.wallet_service.service;

import com.project.wallet_service.client.IdentityClient;
import com.project.wallet_service.dto.TransferRequest;
import com.project.wallet_service.model.UserWallet;
import com.project.wallet_service.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletManager {

    private final WalletRepository repository;
    private final IdentityClient identityClient;

    @Transactional
    public void processTransfer(TransferRequest request){
        if(request.getAmount().compareTo(BigDecimal.ZERO) <=0){
            throw new RuntimeException("Transfer amount should be positive");
        }
        // 1. Logic: Verify both Sender and Receiver exist in Identity Service
        // We do this BEFORE touching the database to save resources
        validateUser(request.getSenderId(), "Sender");
        validateUser(request.getReceiverId(), "Receiver");

        // 2. Database Locking: Fetch wallets with Pessimistic Write locks
        // This prevents other transactions from changing these balances simultaneously
        UserWallet sender = repository.findByUserIdForUpdate(request.getSenderId())
                .orElseThrow( () -> new RuntimeException("Sender wallet record missing"));

        UserWallet receiver = repository.findByUserIdForUpdate(request.getReceiverId())
                .orElseThrow( ()-> new RuntimeException("Receiver wallet record missing"));

        if(sender.getBalance().compareTo(request.getAmount()) < 0){
            throw new RuntimeException("Insufficient funds in sender's wallet");
        }

        sender.setBalance(sender.getBalance().subtract(request.getAmount()));
        receiver.setBalance(receiver.getBalance().add(request.getAmount()));

        repository.save(sender);
        repository.save(receiver);

    }

    private void validateUser(Integer userId, String role){
        ResponseEntity<Boolean> response = identityClient.checkUserExists(userId);
        Boolean exists = response.getBody();
        if(Boolean.FALSE.equals(exists)){
            throw new RuntimeException(role + "with ID "+ userId + " does not exist in the identity system");
        }
    }
}
