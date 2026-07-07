package com.fintech.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        String type,
        BigDecimal amount,
        String description,
        LocalDateTime createdAt
) {}