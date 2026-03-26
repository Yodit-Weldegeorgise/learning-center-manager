package com.learningcenter.temporal;

import com.learningcenter.dto.CreatePaymentRecordRequest;
import com.learningcenter.dto.PaymentRecordResponse;
import com.learningcenter.dto.PaymentStatus;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;

public class SessionPaymentWorkflowImpl implements SessionPaymentWorkflow {

    private boolean attended;
    private boolean cancelled;
    private String cancelReason;
    private PaymentStatus status = PaymentStatus.STARTED;
    private String providerReference;

    private final PaymentActivities activities =
            Workflow.newActivityStub(
                    PaymentActivities.class,
                    ActivityOptions.newBuilder()
                            .setStartToCloseTimeout(Duration.ofSeconds(10))
                            .setRetryOptions(
                                    RetryOptions.newBuilder()
                                            .setInitialInterval(Duration.ofSeconds(2))
                                            .setMaximumInterval(Duration.ofSeconds(10))
                                            .setMaximumAttempts(3)
                                            .build()
                            )
                            .build()
            );

    @Override
    public PaymentRecordResponse start(CreatePaymentRecordRequest request) {
        try {
            status = PaymentStatus.AUTHORIZATION_IN_PROGRESS;

            providerReference = activities.authorizePayment(request);

            status = PaymentStatus.AUTHORIZED;

            Workflow.await(() -> attended || cancelled);

            if (cancelled) {
                activities.refundPayment(
                        providerReference,
                        cancelReason == null ? "Cancelled by user" : cancelReason
                );
                status = PaymentStatus.REFUNDED;

                return new PaymentRecordResponse(
                        Workflow.getInfo().getWorkflowId(),
                        status.name(),
                        "Payment refunded"
                );
            }

            activities.capturePayment(providerReference);
            status = PaymentStatus.CAPTURED;

            return new PaymentRecordResponse(
                    Workflow.getInfo().getWorkflowId(),
                    status.name(),
                    "Payment captured"
            );
        } catch (Exception e) {
            status = PaymentStatus.FAILED;

            return new PaymentRecordResponse(
                    Workflow.getInfo().getWorkflowId(),
                    status.name(),
                    e.getMessage()
            );
        }
    }

    @Override
    public void markAttended() {
        this.attended = true;
    }

    @Override
    public void cancelSession(String reason) {
        this.cancelled = true;
        this.cancelReason = reason;
    }

    @Override
    public PaymentStatus getStatus() {
        return status;
    }
}