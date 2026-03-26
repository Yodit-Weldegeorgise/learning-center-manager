package com.learningcenter.temporal;

import com.learningcenter.dto.CreatePaymentRecordRequest;
import com.learningcenter.dto.FailureMode;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FakePaymentActivitiesImpl implements PaymentActivities {

    private static final Set<Long> FAILED_ONCE_SESSIONS = ConcurrentHashMap.newKeySet();

    @Override
    public String authorizePayment(CreatePaymentRecordRequest request) {
        FailureMode failureMode = FailureMode.valueOf(request.failureMode().toUpperCase());

        if (failureMode == FailureMode.ALWAYS_FAIL) {
            throw new RuntimeException("Simulated payment provider failure");
        }

        if (failureMode == FailureMode.FAIL_ONCE
                && !FAILED_ONCE_SESSIONS.contains(request.sessionId())) {
            FAILED_ONCE_SESSIONS.add(request.sessionId());
            throw new RuntimeException("Simulated temporary authorization failure");
        }

        return "auth-session-" + request.sessionId();
    }

    @Override
    public String capturePayment(String providerReference) {
        return "captured-" + providerReference;
    }

    @Override
    public String refundPayment(String providerReference, String reason) {
        return "refunded-" + providerReference;
    }
}