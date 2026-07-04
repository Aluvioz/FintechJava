package com.fintech.api.service;

import com.fintech.api.dto.TransferRequest;
import com.fintech.api.exception.BusinessException;
import com.fintech.api.model.Account;
import com.fintech.api.repository.AccountRepository;
import com.fintech.api.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void deveLancarExcecaoQuandoSaldoInsuficiente() {
        UUID originId = UUID.randomUUID();
        UUID destId = UUID.randomUUID();

        Account origin = Account.builder().id(originId).balance(BigDecimal.TEN).build();
        Account destination = Account.builder().id(destId).balance(BigDecimal.ZERO).build();

        when(accountRepository.findWithLockById(originId)).thenReturn(Optional.of(origin));
        when(accountRepository.findWithLockById(destId)).thenReturn(Optional.of(destination));

        TransferRequest request = new TransferRequest(originId, destId, BigDecimal.valueOf(100));

        assertThrows(BusinessException.class, () -> accountService.transfer(request));
    }
}