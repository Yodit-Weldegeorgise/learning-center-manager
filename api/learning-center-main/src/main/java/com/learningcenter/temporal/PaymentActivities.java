package com.learningcenter.temporal;

import com.learningcenter.dto.CreatePaymentRecordRequest;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface PaymentActivities {

    @ActivityMethod
    String authorizePayment(CreatePaymentRecordRequest request);

    @ActivityMethod
    String capturePayment(String providerReference);

    @ActivityMethod
    String refundPayment(String providerReference, String reason);
}