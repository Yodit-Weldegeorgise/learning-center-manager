package com.learningcenter.dto;

public enum PaymentStatus {
    STARTED,
    AUTHORIZATION_IN_PROGRESS,
    AUTHORIZED,
    CAPTURED,
    REFUNDED,
    CANCELLED,
    FAILED
}