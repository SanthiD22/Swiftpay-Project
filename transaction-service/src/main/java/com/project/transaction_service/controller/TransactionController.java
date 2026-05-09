package com.project.transaction_service.controller;

import com.netflix.discovery.converters.Auto;
import com.project.transaction_service.model.TransactionLog;
import com.project.transaction_service.repository.TransactionRepository;
import com.project.transaction_service.service.TransactionConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionRepository repository;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionLog>> getHistory(@PathVariable Integer userId){
        return ResponseEntity.ok(repository.findBySenderIdOrReceiverId(userId, userId));
    }

}
