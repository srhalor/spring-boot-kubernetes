package com.fmd.email_processor.service;

import com.fmd.email_processor.dto.EmailMessage;

import java.time.Instant;
import java.util.List;

/**
 * Service interface for interacting with the email server.
 * <p>
 * This service provides methods to fetch, mark, and delete emails on the mail server.
 * </p>
 *
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
public interface EmailServerService {

    /**
     * Fetches emails from the mail server based on the specified criteria.
     * <p>
     * This method retrieves emails that match the given subject pattern and were received
     * after the specified timestamp.
     * </p>
     *
     * @param subjectLike          a string pattern to match against email subjects
     * @param emailReceivedAfter   an Instant representing the earliest time an email can be received
     * @return a list of EmailMessage objects that match the criteria
     */
    List<EmailMessage> fetchEmails(String subjectLike, Instant emailReceivedAfter);

    /**
     * Marks the specified email as processed on the mail server.
     * <p>
     * This method updates the status of the email to indicate that it has been processed,
     * preventing it from being fetched again in future operations.
     * </p>
     *
     * @param messageId unique identifier of the email message to be marked as processed
     */
    void markEmailAsProcessed(String messageId);

    /**
     * Deletes the specified email from the mail server.
     * <p>
     * This method removes the email from the server, ensuring it is no longer accessible
     * or retrievable in future operations.
     * </p>
     *
     * @param messageId unique identifier of the email message to be deleted
     */
    @SuppressWarnings("unused")
    void deleteEmail(String messageId);
}
