package com.fmd.email_processor.service.impl;

import com.fmd.email_processor.dto.EmailMessage;
import com.fmd.email_processor.entity.OrderRequest;
import com.fmd.email_processor.entity.ProcessedEmailEntity;
import com.fmd.email_processor.repository.ProcessedEmailRepository;
import com.fmd.email_processor.service.EmailProcessingService;
import com.fmd.email_processor.service.EmailServerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Implementation of {@link EmailProcessingService} binding email server logic to database persistence.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailProcessingServiceImpl implements EmailProcessingService {

    private final EmailServerService emailServerService;
    private final ProcessedEmailRepository processedEmailRepository;

    /**
     * Fetches unprocessed emails matching the provided filters, associates them with an OrderRequest,
     * persists them as processed, and returns the new emails.
     */
    @Override
    @Transactional
    public void fetchAndPersistNewEmails(OrderRequest orderRequest) {
        Long orderRequestId = orderRequest.getId();
        Instant createdAt = orderRequest.getCreatedAt();
        List<EmailMessage> allEmails = emailServerService.fetchEmails(String.valueOf(orderRequestId), createdAt);

        if (null != allEmails && !allEmails.isEmpty()) {
            log.info("Fetched {} emails for OrderRequest ID: {}", allEmails.size(), orderRequest.getId());

            // Filter unprocessed emails and persist them
            allEmails.forEach(email -> {
                if (isUnprocessedForOrderRequest(email, orderRequestId)) {
                    persistProcessedEmail(email, orderRequestId);
                    emailServerService.markEmailAsProcessed(email.messageId());
                } else {
                    log.info("Email with messageId={} already processed for OrderRequest ID: {}", email.messageId(), orderRequest.getId());
                }
            });
        }
        log.info("No new emails fetched for OrderRequest ID: {}", orderRequest.getId());

    }

    /**
     * Checks if the email has not already been processed for the given order request.
     *
     * @param email          the email message
     * @param orderRequestId the associated order request ID
     * @return true if the email is unprocessed for this order request, false otherwise
     */
    private boolean isUnprocessedForOrderRequest(EmailMessage email, Long orderRequestId) {
        return email.messageId() != null && !processedEmailRepository.existsByMessageIdAndOrderRequestId(email.messageId(), orderRequestId);
    }

    /**
     * Persists a processed email entity for the given order request.
     *
     * @param email          the email message
     * @param orderRequestId the associated order request ID
     */
    private void persistProcessedEmail(EmailMessage email, Long orderRequestId) {
        try {
            ProcessedEmailEntity entity = new ProcessedEmailEntity();
            entity.setMessageId(email.messageId());
            entity.setOrderRequestId(orderRequestId);
            processedEmailRepository.save(entity);
        } catch (Exception ex) {
            log.error("Failed to persist processed state for messageId={} and orderRequestId={}", email.messageId(), orderRequestId, ex);
        }
    }
}