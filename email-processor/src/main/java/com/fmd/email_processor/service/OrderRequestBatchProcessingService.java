package com.fmd.email_processor.service;

/**
 * Service that manages batch fetching and parallel processing of OrderRequests.
 */
public interface OrderRequestBatchProcessingService {
    /**
     * Fetches the next batch of order requests and processes them in parallel.
     */
    void fetchNextBatchAndProcess();
}