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
 * Default implementation of {@link OrderRequestService}.
 * <p>
 * Note: Do not log requestId directly—set it in MDC for structured logging.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderRequestServiceImpl implements OrderRequestService {

    private final OrderRequestRepository orderRequestRepository;
    private final BatchJobProperties properties;

    @Override
    @Transactional(readOnly = true)
    public List<OrderRequest> fetchNextBatch() {
        return orderRequestRepository.fetchNextBatch(properties.maxRetry(), properties.chunkSize());
    }

    @Override
    @Transactional
    public void markProcessed(Long requestId) {
        updateOrderRequest(requestId, "Completed", true, 0, null);
    }

    @Override
    @Transactional
    public void markFailed(Long requestId, String failureReason) {
        updateOrderRequest(requestId, "Error", false, 1, failureReason);
        log.warn("OrderRequest marked as failed with failureReason='{}'", failureReason);
    }

    /**
     * Common method to update status, processed flag, and retry count for an OrderRequest.
     *
     * @param requestId     the request ID
     * @param newStatus     new status value
     * @param processedFlag true if processed, false otherwise
     * @param increment     0 to keep retry count as is, 1 to increment retry count by 1
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