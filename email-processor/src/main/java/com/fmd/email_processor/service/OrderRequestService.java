package com.fmd.email_processor.service;

import com.fmd.email_processor.entity.OrderRequest;

import java.util.List;

/**
 * Service for fetching and updating order requests in batch processing.
 */
public interface OrderRequestService {

    /**
     * Fetches the next batch of order requests to be processed.
     *
     * @return list of {@link OrderRequest}; never null, may be empty.
     */
    List<OrderRequest> fetchNextBatch();

    /**
     * Marks a given order request as successfully processed.
     *
     * @param orderRequestId the ID of the order request
     */
    void markProcessed(Long orderRequestId);

    /**
     * Marks a given order request as failed and records the reason.
     *
     * @param orderRequestId the ID of the order request
     * @param remarks        reason for failure or diagnostic information
     */
    void markFailed(Long orderRequestId, String remarks);
}