package com.learningcenter.dto;

public record PaymentRecordResponse(
        String workflowId,
        String status,
        String message
) {}