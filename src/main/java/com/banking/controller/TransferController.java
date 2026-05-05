package com.banking.controller;

import com.banking.model.Transaction;
import com.banking.service.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfer")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<?> makeTransfer(
            @RequestParam String sourceIban,
            @RequestParam String destinationIban,
            @RequestParam double amount,
            @RequestParam String description) {
        try {
            Transaction result = transferService.executeTransfer(sourceIban, destinationIban, amount, description);

            return ResponseEntity.ok("Transfer realizat cu succes! Suma: " + result.getAmount());
        } catch (Exception e) {
            // Dacă apare o eroare (ex: fonduri insuficiente), returnăm un mesaj clar
            return ResponseEntity.badRequest().body("Eroare la transfer: " + e.getMessage());
        }
    }
}