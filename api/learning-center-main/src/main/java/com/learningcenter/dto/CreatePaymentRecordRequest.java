package com.learningcenter.dto;

import java.math.BigDecimal;

public record CreatePaymentRecordRequest(
        Long sessionId,
        Long parentId,
        BigDecimal amount,
        String currency,
        String paymentMethod,
        String failureMode
) {}