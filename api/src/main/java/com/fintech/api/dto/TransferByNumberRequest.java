package com.fintech.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record TransferByNumberRequest(
        @NotNull UUID originAccountId,
        @NotBlank String destinationAccountNumber,
        @NotNull @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
        BigDecimal amount
) {}