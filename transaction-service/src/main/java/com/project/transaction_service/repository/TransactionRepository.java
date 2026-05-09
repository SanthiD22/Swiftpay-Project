package com.project.transaction_service.repository;

import com.project.transaction_service.model.TransactionLog;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<TransactionLog, String> {

    List<TransactionLog> findBySenderIdOrReceiverId(Integer senderId, Integer receiverId);
}
