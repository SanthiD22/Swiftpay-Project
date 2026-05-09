package com.project.transaction_service.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "transactions")
@Data
@Builder
public class TransactionLog {
    @Id
    private String id; // Mongo uses String IDs (ObjectIds) by default
    private String transactionId; // From Wallet Service
    private Integer senderId;
    private Integer receiverId;
    private BigDecimal amount;
    private String status;
    private LocalDateTime timeStamp;

}

