package com.fmd.email_processor.service;

import com.fmd.email_processor.entity.OrderRequest;

import java.util.List;

/**
 * Service interface for managing order requests in the email processing system.
 * <p>
 * This service provides methods to fetch, mark, and update the status of order requests.
 * </p>
 *
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
public interface OrderRequestService {

    /**
     * Fetches the next batch of order requests that are pending processing.
     * <p>
     * This method retrieves a list of order requests that are ready to be processed,
     * typically those that have not been marked as processed or failed.
     * </p>
     *
     * @return a list of order requests ready for processing
     */
    List<OrderRequest> fetchNextBatch();

    /**
     * Marks a given order request as processed.
     * <p>
     * This method updates the status of the order request to indicate that it has been successfully processed.
     * </p>
     *
     * @param orderRequestId the ID of the order request to be marked as processed
     */
    void markProcessed(Long orderRequestId);

    /**
     * Marks a given order request as failed.
     * <p>
     * This method updates the status of the order request to indicate that it has failed processing,
     * along with a reason for the failure.
     * </p>
     *
     * @param orderRequestId the ID of the order request to be marked as failed
     * @param remarks        additional information about the failure
     */
    void markFailed(Long orderRequestId, String remarks);
}
