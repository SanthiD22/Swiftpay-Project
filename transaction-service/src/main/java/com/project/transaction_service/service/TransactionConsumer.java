package com.project.transaction_service.service;

import com.project.transaction_service.dto.TransactionEvent;
import com.project.transaction_service.model.TransactionLog;
import com.project.transaction_service.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.requests.TransactionResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class TransactionConsumer {

    @Autowired
    private TransactionRepository repository;

    @KafkaListener(topics = "transaction-events", groupId = "transaction-group")
    public void consume(TransactionEvent event) {
        log.info("Logging transaction: {}", event.getTransactionId());

        TransactionLog logEntry = TransactionLog.builder()
                .transactionId(event.getTransactionId())
                .senderId(event.getSenderId())
                .amount(event.getAmount())
                .receiverId(event.getReceiverId())
                .status(event.getStatus())
                .timeStamp(LocalDateTime.now())
                .build();

        repository.save(logEntry);
    }
}
