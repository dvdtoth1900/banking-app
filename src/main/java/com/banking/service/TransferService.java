package com.banking.service;

import com.banking.model.Account;
import com.banking.model.Transaction;
import com.banking.repository.AccountRepository;
import com.banking.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransferService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Transaction executeTransfer(String sourceIban, String destinationIban, double amount, String description) {

        if (amount <= 0) {
            throw new IllegalArgumentException("Suma de transferat trebuie să fie mai mare decât 0!");
        }
        if (sourceIban.equals(destinationIban)) {
            throw new IllegalArgumentException("Nu poți transfera bani în același cont!");
        }

        Account sourceAccount = accountRepository.findByIbanForUpdate(sourceIban)
                .orElseThrow(() -> new RuntimeException("Contul sursă nu a fost găsit!"));

        Account destinationAccount = accountRepository.findByIbanForUpdate(destinationIban)
                .orElseThrow(() -> new RuntimeException("Contul destinație nu a fost găsit!"));

        if (sourceAccount.getBalance() < amount) {
            throw new RuntimeException("Fonduri insuficiente în contul sursă!");
        }


        sourceAccount.subtractFunds(amount);
        destinationAccount.addFunds(amount);

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);


        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));


        Transaction transaction = new Transaction();
        transaction.setDate(currentDate);
        transaction.setDescription(description);
        transaction.setAmount(amount);
        transaction.setSourceIban(sourceIban);
        transaction.setDestinationIban(destinationIban);

        return transactionRepository.save(transaction);
    }
}