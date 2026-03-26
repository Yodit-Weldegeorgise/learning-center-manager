package com.learningcenter.temporal;

import com.learningcenter.dto.CreatePaymentRecordRequest;
import com.learningcenter.dto.PaymentRecordResponse;
import com.learningcenter.dto.PaymentStatus;
import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface SessionPaymentWorkflow {

    @WorkflowMethod
    PaymentRecordResponse start(CreatePaymentRecordRequest request);

    @SignalMethod
    void markAttended();

    @SignalMethod
    void cancelSession(String reason);

    @QueryMethod
    PaymentStatus getStatus();
}