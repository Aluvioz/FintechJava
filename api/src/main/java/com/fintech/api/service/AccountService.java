package com.fintech.api.service;

import com.fintech.api.dto.AccountResponse;
import com.fintech.api.dto.TransferRequest;
import com.fintech.api.exception.BusinessException;
import com.fintech.api.model.*;
import com.fintech.api.repository.AccountRepository;
import com.fintech.api.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountResponse getBalance(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException("Conta não encontrada"));
        return new AccountResponse(account.getId(), account.getAccountNumber(), account.getBalance());
    }

    @Transactional
    public void transfer(TransferRequest request) {
        if (request.originAccountId().equals(request.destinationAccountId())) {
            throw new BusinessException("Não é possível transferir para a mesma conta");
        }

        Account origin = accountRepository.findWithLockById(request.originAccountId())
                .orElseThrow(() -> new BusinessException("Conta de origem não encontrada"));

        Account destination = accountRepository.findWithLockById(request.destinationAccountId())
                .orElseThrow(() -> new BusinessException("Conta de destino não encontrada"));

        if (origin.getBalance().compareTo(request.amount()) < 0) {
            throw new BusinessException("Saldo insuficiente");
        }

        origin.setBalance(origin.getBalance().subtract(request.amount()));
        destination.setBalance(destination.getBalance().add(request.amount()));

        accountRepository.save(origin);
        accountRepository.save(destination);

        transactionRepository.save(Transaction.builder()
                .account(origin).type(TransactionType.TRANSFER_OUT)
                .amount(request.amount()).description("Transferência enviada").build());

        transactionRepository.save(Transaction.builder()
                .account(destination).type(TransactionType.TRANSFER_IN)
                .amount(request.amount()).description("Transferência recebida").build());
    }
}