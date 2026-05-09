package com.project.wallet_service.controller;

import com.project.wallet_service.dto.DepositRequest;
import com.project.wallet_service.dto.TransferRequest;
import com.project.wallet_service.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService service;

    @GetMapping("/{userId}/balance")
    public ResponseEntity<Map<String,BigDecimal>> getBalance(@PathVariable Integer userId){
        BigDecimal balance = service.getBalance(userId);
        Map<String, BigDecimal> response = new HashMap<>();
        response.put("User Balance",balance);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(@RequestBody DepositRequest request) {
        service.addMoney(request);
        return ResponseEntity.ok("Deposit Successful. New balance updated");
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransferRequest request){
        service.transferMoney(request);
        return ResponseEntity.ok("Transfer completed successfully");
    }
}
