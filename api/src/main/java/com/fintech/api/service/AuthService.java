package com.fintech.api.service;

import com.fintech.api.dto.AuthResponse;
import com.fintech.api.dto.LoginRequest;
import com.fintech.api.dto.UserRegisterRequest;
import com.fintech.api.exception.BusinessException;
import com.fintech.api.model.Account;
import com.fintech.api.model.User;
import com.fintech.api.repository.AccountRepository;
import com.fintech.api.repository.UserRepository;
import com.fintech.api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Este email já está cadastrado");
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        userRepository.save(user);

        Account account = Account.builder()
                .user(user)
                .accountNumber(generateAccountNumber())
                .balance(BigDecimal.ZERO)
                .build();

        accountRepository.save(account);

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (BadCredentialsException ex) {
            throw new BusinessException("Email ou senha inválidos");
        }

        String token = jwtService.generateToken(request.email());
        return new AuthResponse(token);
    }

    private String generateAccountNumber() {
        return String.valueOf(System.currentTimeMillis()).substring(3)
                + UUID.randomUUID().toString().substring(0, 4);
    }
}