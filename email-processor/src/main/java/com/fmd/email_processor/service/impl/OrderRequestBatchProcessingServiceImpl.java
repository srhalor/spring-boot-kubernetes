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
import java.util.Objects;
import java.util.concurrent.*;

/**
 * Service to fetch the next batch of order requests and process them in parallel chunks,
 * immediately querying for the next chunk as soon as the current one is complete.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderRequestBatchProcessingServiceImpl implements OrderRequestBatchProcessingService {

    private final OrderRequestService orderRequestService;
    private final EmailProcessingService emailProcessingService;

    // Thread pool for parallel processing of order requests.
    private final ExecutorService processingPool = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors(),
            new CustomizableThreadFactory("order-req-processing-")
    );

    /**
     * Continuously fetches and processes batches of order requests in parallel.
     * Stops when there are no more records to process.
     */
    @Override
    public void fetchNextBatchAndProcess() {
        while (true) {
            List<OrderRequest> nextBatch = orderRequestService.fetchNextBatch();

            if (nextBatch == null || nextBatch.isEmpty()) {
                log.info("No order requests to process. Exiting batch processing loop.");
                break;
            }

            // Submit each order request for parallel processing
            List<Future<Object>> futures = nextBatch.stream()
                    .map(orderRequest -> processingPool.submit(() -> {
                        processOrderRequest(orderRequest);
                        return null;
                    }))
                    .toList();

            // Wait for the entire batch to finish before fetching the next
            for (Future<Object> future : futures) {
                try {
                    future.get();
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
     * Processes a single order request using EmailProcessingService and updates its status.
     *
     * @param orderRequest the order request to process
     */
    private void processOrderRequest(OrderRequest orderRequest) {
        Objects.requireNonNull(orderRequest, "OrderRequest must not be null");
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
     * Clean up processing pool on bean destruction.
     */
    @PreDestroy
    public void shutdownProcessingPool() {
        processingPool.shutdown();
        try {
            if (!processingPool.awaitTermination(10, TimeUnit.SECONDS)) {
                processingPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            processingPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}