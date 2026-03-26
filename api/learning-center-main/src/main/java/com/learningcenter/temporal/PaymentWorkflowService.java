package com.learningcenter.temporal;

import com.learningcenter.dto.CreatePaymentRecordRequest;
import com.learningcenter.dto.PaymentStatus;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import org.springframework.stereotype.Service;

@Service
public class PaymentWorkflowService {

    private final WorkflowClient workflowClient;

    public PaymentWorkflowService(WorkflowClient workflowClient) {
        this.workflowClient = workflowClient;
    }

    public String startPayment(CreatePaymentRecordRequest request) {
        String workflowId = "payment-session-" + request.sessionId();

        SessionPaymentWorkflow workflow =
                workflowClient.newWorkflowStub(
                        SessionPaymentWorkflow.class,
                        WorkflowOptions.newBuilder()
                                .setTaskQueue(TemporalConfig.TASK_QUEUE)
                                .setWorkflowId(workflowId)
                                .build()
                );

        WorkflowClient.start(workflow::start, request);
        return workflowId;
    }

    public PaymentStatus getStatus(Long sessionId) {
        String workflowId = "payment-session-" + sessionId;

        SessionPaymentWorkflow workflow =
                workflowClient.newWorkflowStub(SessionPaymentWorkflow.class, workflowId);

        return workflow.getStatus();
    }

    public void markAttended(Long sessionId) {
        String workflowId = "payment-session-" + sessionId;

        SessionPaymentWorkflow workflow =
                workflowClient.newWorkflowStub(SessionPaymentWorkflow.class, workflowId);

        workflow.markAttended();
    }

    public void cancelSession(Long sessionId, String reason) {
        String workflowId = "payment-session-" + sessionId;

        SessionPaymentWorkflow workflow =
                workflowClient.newWorkflowStub(SessionPaymentWorkflow.class, workflowId);

        workflow.cancelSession(reason);
    }
}