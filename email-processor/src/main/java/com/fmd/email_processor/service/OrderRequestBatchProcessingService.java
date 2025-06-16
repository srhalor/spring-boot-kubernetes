package com.fmd.email_processor.service;

/**
 * Service interface for batch processing of order requests in the email processing system.
 * <p>
 * This service is responsible for fetching the next batch of order requests and processing them
 * in parallel, ensuring efficient handling of multiple requests.
 * </p>
 *
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
public interface OrderRequestBatchProcessingService {

    /**
     * Initiates the process of fetching the next batch of order requests and processing them.
     * <p>
     * This method is designed to be called periodically to ensure that new order requests
     * are processed in a timely manner, leveraging parallel processing for efficiency.
     * </p>
     */
    void fetchNextBatchAndProcess();
}
