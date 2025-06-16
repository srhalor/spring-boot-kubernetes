package com.fmd.email_processor.service;

import com.fmd.email_processor.entity.OrderRequest;

/**
 * Service interface for processing emails related to order requests.
 * <p>
 * This service is responsible for fetching new emails and persisting them
 * in the system, ensuring that each email is associated with an order request.
 * </p>
 *
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
public interface EmailProcessingService {

    /**
     * Fetches new emails related to order requests and persists them in the system.
     * <p>
     * This method retrieves emails that have not yet been processed and associates
     * them with the corresponding order request. It ensures that each email is
     * stored only once to avoid duplication.
     * </p>
     *
     * @param orderRequest the order request for which new emails are to be fetched
     */
    void fetchAndPersistNewEmails(OrderRequest orderRequest);
}
