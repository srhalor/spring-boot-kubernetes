package com.fmd.email_processor.service;

import com.fmd.email_processor.entity.OrderRequest;

/**
 * Service to manage fetching emails and persisting their processing state.
 */
public interface EmailProcessingService {

    void fetchAndPersistNewEmails(OrderRequest orderRequest);
}