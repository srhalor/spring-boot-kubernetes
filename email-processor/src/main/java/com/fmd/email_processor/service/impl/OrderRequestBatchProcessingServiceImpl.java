package com.fmd.email_processor.service.impl;

import com.fmd.email_processor.entity.OrderRequest;
import com.fmd.email_processor.service.EmailProcessingService;
import com.fmd.email_processor.service.OrderRequestBatchProcessingService;
import com.fmd.email_processor.service.OrderRequestService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;

/**
 * Implementation of the OrderRequestBatchProcessingService interface.
 * <p>
 * This service fetches and processes batches of order requests in parallel,
 * ensuring efficient handling of email processing tasks.
 * </p>
 *
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderRequestBatchProcessingServiceImpl implements OrderRequestBatchProcessingService {

    private final OrderRequestService orderRequestService;
    private final EmailProcessingService emailProcessingService;

    /**
     * Thread pool for processing order requests in parallel.
     * Uses a fixed thread pool based on the number of available processors.
     */
    private final ExecutorService processingPool = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors(),
            new CustomizableThreadFactory("order-req-processing-")
    );

    /**
     * Fetches the next batch of order requests and processes them in parallel.
     * <p>
     * This method continuously fetches batches of order requests and submits them
     * for processing until no more requests are available.
     * </p>
     */
    @Override
    public void fetchNextBatchAndProcess() {
        while (true) {
            List<OrderRequest> nextBatch = orderRequestService.fetchNextBatch();

            if (nextBatch == null || nextBatch.isEmpty()) {
                log.info("No order requests to process. Exiting batch processing loop.");
                break;
            }

            log.info("Processing batch of {} order requests", nextBatch.size());
            // Submit each order request for parallel processing
            List<Future<Object>> futures = nextBatch.stream()
                    .map(orderRequest -> processingPool.submit(() -> {
                        processOrderRequest(orderRequest);
                        return null;
                    }))
                    .toList();

            log.info("Submitted {} order requests for processing", futures.size());
            // Wait for the entire batch to finish before fetching the next
            for (Future<Object> future : futures) {
                try {
                    // Wait for each future to complete
                    future.get();
                    log.info("Order request batch processed successfully");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("Order request batch processing interrupted", e);
                    return;
                } catch (ExecutionException e) {
                    log.error("Error processing order request", e.getCause());
                }
            }
        }
    }

    /**
     * Processes a single OrderRequest by fetching and persisting new emails.
     *
     * @param orderRequest the order request to process
     */
    private void processOrderRequest(OrderRequest orderRequest) {
        if (orderRequest == null) {
            log.warn("Received null OrderRequest, skipping processing");
            return;
        }
        log.info("Processing OrderRequest ID: {}", orderRequest.getId());
        try {
            emailProcessingService.fetchAndPersistNewEmails(orderRequest);
            orderRequestService.markProcessed(orderRequest.getId());
            log.info("OrderRequest {} processed successfully", orderRequest.getId());
        } catch (Exception ex) {
            orderRequestService.markFailed(orderRequest.getId(), ex.getMessage());
            log.error("Failed to process OrderRequest {}: {}", orderRequest.getId(), ex.getMessage(), ex);
        }
    }

    /**
     * Shuts down the processing pool gracefully.
     * <p>
     * This method ensures that all tasks are completed before shutting down the pool.
     * </p>
     */
    @PreDestroy
    public void shutdownProcessingPool() {
        log.info("Shutting down processing pool...");
        processingPool.shutdown();
        try {
            if (!processingPool.awaitTermination(10, TimeUnit.SECONDS)) {
                processingPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("Processing pool shutdown interrupted. Forcing shutdown now.", e);
            processingPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
