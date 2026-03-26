package com.learningcenter.temporal;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemporalConfig {

    public static final String TASK_QUEUE = "session-payment-task-queue";

    private WorkflowServiceStubs service;
    private WorkflowClient client;
    private WorkerFactory factory;

    @Bean
    public WorkflowServiceStubs workflowServiceStubs() {
        this.service = WorkflowServiceStubs.newLocalServiceStubs();
        return this.service;
    }

    @Bean
    public WorkflowClient workflowClient(WorkflowServiceStubs service) {
        this.client = WorkflowClient.newInstance(service);
        return this.client;
    }

    @PostConstruct
    public void startWorker() {
        if (service == null) {
            service = WorkflowServiceStubs.newLocalServiceStubs();
        }
        if (client == null) {
            client = WorkflowClient.newInstance(service);
        }

        factory = WorkerFactory.newInstance(client);
        Worker worker = factory.newWorker(TASK_QUEUE);

        worker.registerWorkflowImplementationTypes(SessionPaymentWorkflowImpl.class);
        worker.registerActivitiesImplementations(new FakePaymentActivitiesImpl());

        factory.start();
    }

    @PreDestroy
    public void shutdown() {
        if (factory != null) {
            factory.shutdown();
        }
        if (service != null) {
            service.shutdown();
        }
    }
}