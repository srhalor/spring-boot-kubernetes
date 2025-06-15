package com.fmd.email_processor.service;

import com.fmd.email_processor.dto.EmailMessage;

import java.time.Instant;
import java.util.List;

/**
 * Service for fetching emails from the server.
 */
public interface EmailServerService {

    /**
     * Fetches emails matching the given search criteria.
     *
     * @param subjectLike  subject pattern to match (can be null for no filtering)
     * @param emailReceivedAfter optional: only fetch emails received after this timestamp (can be null for no filtering)
     * @return a list of matching email messages (never null)
     */
    List<EmailMessage> fetchEmails(String subjectLike, Instant emailReceivedAfter);

    /**
     * Marks the email as processed/handled on the mail server.
     *
     * @param messageId unique identifier of the email message
     */
    void markEmailAsProcessed(String messageId);

    /**
     * Deletes the specified email from the mail server.
     *
     * @param messageId unique identifier of the email message
     */
    void deleteEmail(String messageId);
}