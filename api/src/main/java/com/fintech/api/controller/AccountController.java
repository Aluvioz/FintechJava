package com.fintech.api.controller;

import com.fintech.api.dto.AccountResponse;
import com.fintech.api.dto.TransferRequest;
import com.fintech.api.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{id}/balance")
    public ResponseEntity<AccountResponse> getBalance(@PathVariable UUID id) {
        return ResponseEntity.ok(accountService.getBalance(id));
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@RequestBody @Valid TransferRequest request) {
        accountService.transfer(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
public ResponseEntity<AccountResponse> getMyAccount(Authentication authentication) {
    String email = authentication.getName();
    return ResponseEntity.ok(accountService.getMyAccount(email));
}
}