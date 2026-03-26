package com.learningcenter.controller;

import com.learningcenter.dto.CreatePaymentRecordRequest;
import com.learningcenter.dto.PaymentRecordResponse;
import com.learningcenter.dto.PaymentStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.learningcenter.temporal.PaymentWorkflowService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentWorkflowService paymentWorkflowService;

    public PaymentController(PaymentWorkflowService paymentWorkflowService) {
        this.paymentWorkflowService = paymentWorkflowService;
    }

    @PostMapping("/start")
    public ResponseEntity<PaymentRecordResponse> start(@RequestBody CreatePaymentRecordRequest request) {
        String workflowId = paymentWorkflowService.startPayment(request);

        return ResponseEntity.ok(
                new PaymentRecordResponse(
                        workflowId,
                        PaymentStatus.STARTED.name(),
                        "Payment workflow started"
                )
        );
    }

    @GetMapping("/status/{sessionId}")
    public ResponseEntity<PaymentRecordResponse> status(@PathVariable Long sessionId) {
        PaymentStatus status = paymentWorkflowService.getStatus(sessionId);

        return ResponseEntity.ok(
                new PaymentRecordResponse(
                        "payment-session-" + sessionId,
                        status.name(),
                        "Payment status fetched successfully"
                )
        );
    }

    @PostMapping("/attended/{sessionId}")
    public ResponseEntity<PaymentRecordResponse> attended(@PathVariable Long sessionId) {
        paymentWorkflowService.markAttended(sessionId);

        return ResponseEntity.ok(
                new PaymentRecordResponse(
                        "payment-session-" + sessionId,
                        "SIGNAL_SENT",
                        "Attendance signal sent"
                )
        );
    }

    @PostMapping("/cancel/{sessionId}")
    public ResponseEntity<PaymentRecordResponse> cancel(
            @PathVariable Long sessionId,
            @RequestParam(defaultValue = "Cancelled by user") String reason
    ) {
        paymentWorkflowService.cancelSession(sessionId, reason);

        return ResponseEntity.ok(
                new PaymentRecordResponse(
                        "payment-session-" + sessionId,
                        "SIGNAL_SENT",
                        "Cancel signal sent"
                )
        );
    }
}