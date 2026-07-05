package com.fintech.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        String accountNumber,
        String userName,
        String userEmail,
        BigDecimal balance
) {}