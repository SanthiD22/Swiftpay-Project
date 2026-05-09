package com.project.wallet_service.service;

import com.project.wallet_service.client.IdentityClient;
import com.project.wallet_service.dto.DepositRequest;
import com.project.wallet_service.dto.TransactionEvent;
import com.project.wallet_service.dto.TransferRequest;
import com.project.wallet_service.model.UserWallet;
import com.project.wallet_service.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final WalletRepository repository;
    private final WalletManager walletManager;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public BigDecimal getBalance(Integer userId){
        return repository.findByUserId(userId)
                .map(UserWallet::getBalance)
                .orElseThrow(() -> new RuntimeException(("Wallet not found for user: "+ userId)));
    }

    @Transactional
    public void addMoney(DepositRequest request){
        if(request.getAmount().compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        UserWallet userWallet = repository.findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        userWallet.setBalance(userWallet.getBalance().add(request.getAmount()));
        repository.save(userWallet);
    }


    public void transferMoney(TransferRequest request){

        boolean dbSuccess = false;
        try{
            walletManager.processTransfer(request);
            dbSuccess = true;
        }
        catch(Exception e){
            sendEvent(request, "FAILED");
            throw e;
        }

        if(dbSuccess){
            try{
                sendEvent(request,"SUCCESS");
            } catch (Exception e) {
                log.error("Money moved, but Kafka failed to log SUCCESS");
            }
        }

    }


    private void sendEvent(TransferRequest request, String status){

        TransactionEvent event = TransactionEvent.builder()
                .transactionId(UUID.randomUUID().toString())
                .senderId(request.getSenderId())
                .receiverId(request.getReceiverId())
                .amount(request.getAmount())
                .status(status)
                .build();

        kafkaTemplate.send("transaction-events",event);

    }

}
