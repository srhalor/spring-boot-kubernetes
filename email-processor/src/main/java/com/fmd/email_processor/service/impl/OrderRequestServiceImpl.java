package com.fmd.email_processor.service.impl;

import com.fmd.email_processor.dto.BatchJobProperties;
import com.fmd.email_processor.entity.OrderRequest;
import com.fmd.email_processor.repository.OrderRequestRepository;
import com.fmd.email_processor.service.OrderRequestService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of the OrderRequestService interface.
 * <p>
 * This service handles operations related to OrderRequest entities, including fetching batches,
 * marking requests as processed or failed, and updating their status in the database.
 * </p>
 *
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderRequestServiceImpl implements OrderRequestService {

    private final OrderRequestRepository orderRequestRepository;
    private final BatchJobProperties properties;

    /**
     * Fetches the next batch of OrderRequests from the database.
     * This method retrieves a list of OrderRequests that are ready for processing,
     */
    @Override
    @Transactional(readOnly = true)
    public List<OrderRequest> fetchNextBatch() {
        return orderRequestRepository.fetchNextBatch(properties.maxRetry(), properties.chunkSize());
    }

    /**
     * Marks an OrderRequest as processed by updating its status and flags in the database.
     *
     * @param requestId the ID of the OrderRequest to mark as processed
     */
    @Override
    @Transactional
    public void markProcessed(Long requestId) {
        updateOrderRequest(requestId, "Completed", true, 0, null);
    }

    /**
     * Marks an OrderRequest as failed by updating its status, flags, and failure reason in the database.
     *
     * @param requestId     the ID of the OrderRequest to mark as failed
     * @param failureReason the reason for the failure
     */
    @Override
    @Transactional
    public void markFailed(Long requestId, String failureReason) {
        updateOrderRequest(requestId, "Error", false, 1, failureReason);
        log.warn("OrderRequest marked as failed with failureReason='{}'", failureReason);
    }

    /**
     * Updates the status of an OrderRequest in the database.
     *
     * @param requestId     the ID of the OrderRequest to update
     * @param newStatus     the new status to set (e.g., "Completed", "Error")
     * @param processedFlag flag indicating if the request was processed successfully
     * @param increment     the amount to increment the retry count
     * @param failureReason reason for failure, if applicable
     */
    private void updateOrderRequest(Long requestId, String newStatus, boolean processedFlag, int increment, String failureReason) {
        OrderRequest request = orderRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("OrderRequest not found: " + requestId));
        int newRetryCount = request.getRetryCount() + increment;
        if (newRetryCount > properties.maxRetry()) {
            log.error("Max retry count exceeded for OrderRequest: {}", requestId);
            throw new IllegalStateException("Max retry count exceeded for OrderRequest: " + requestId);
        }
        int updated = orderRequestRepository.updateOrderRequest(
                requestId, newStatus, processedFlag, newRetryCount, failureReason
        );
        if (updated != 1) {
            log.error("Failed to update OrderRequest");
            throw new EntityNotFoundException("OrderRequest not found: " + requestId);
        }
    }
}
