package com.fmd.email_processor.service.impl;

import com.fmd.email_processor.dto.EmailMessage;
import com.fmd.email_processor.dto.EmailServerConnection;
import com.fmd.email_processor.dto.EmailServerProperties;
import com.fmd.email_processor.dto.mapper.MessageMapper;
import com.fmd.email_processor.service.EmailServerService;
import com.fmd.email_processor.util.EmailServerConnectionUtil;
import jakarta.mail.Flags.Flag;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.search.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

/**
 * Implementation of the EmailServerService interface.
 * <p>
 * This service handles interactions with the email server, including fetching emails,
 * marking them as processed, and deleting them based on specified criteria.
 * </p>
 *
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServerServiceImpl implements EmailServerService {

    private final EmailServerProperties emailServerProperties;

    /**
     * Fetches emails from the mail server based on the specified criteria.
     * <p>
     * This method retrieves emails that match the given subject pattern and were received
     * after the specified timestamp.
     * </p>
     *
     * @param subjectLike        a string pattern to match against email subjects
     * @param emailReceivedAfter an Instant representing the earliest time an email can be received
     * @return a list of EmailMessage objects that match the criteria
     */
    @Override
    public List<EmailMessage> fetchEmails(String subjectLike, Instant emailReceivedAfter) {
        SearchTerm searchTerm = buildSearchTerm(subjectLike, emailReceivedAfter);

        // If no search criteria are provided, do not process and return empty result.
        if (searchTerm == null) {
            log.warn("No search criteria provided for fetching emails. Aborting fetch.");
            return List.of();
        }

        try (EmailServerConnection connection = EmailServerConnectionUtil.openConnection(emailServerProperties, false)) {
            log.debug("Fetching emails with criteria: subjectLike={}, emailReceivedAfter={}", subjectLike, emailReceivedAfter);
            Folder folder = connection.folder();
            Message[] messages = folder.search(searchTerm);

            // Stream processing instead of explicit for-loop
            return Arrays.stream(messages)
                    .map(MessageMapper::mapTo)
                    .filter(Objects::nonNull)
                    .toList();
        } catch (Exception e) {
            log.error("Failed to fetch emails from server", e);
            return List.of();
        }
    }

    /**
     * Marks the email identified by the given messageId as processed by setting the SEEN flag.
     *
     * @param messageId the unique identifier of the email message to be marked as processed
     */
    @Override
    public void markEmailAsProcessed(String messageId) {
        updateFlag(messageId, Flag.SEEN);
    }

    /**
     * Deletes the email identified by the given messageId by setting the DELETED flag.
     *
     * @param messageId the unique identifier of the email message to be deleted
     */
    @Override
    public void deleteEmail(String messageId) {
        updateFlag(messageId, Flag.DELETED);
    }

    /**
     * Updates the specified flag on the email message identified by the given messageId.
     *
     * @param messageId the unique identifier of the email message
     * @param flag      the flag to be set on the email message
     */
    private void updateFlag(String messageId, Flag flag) {
        if (messageId == null || messageId.isBlank()) {
            log.warn("Message ID is null or blank. Cannot update flag.");
            return;
        }
        try (EmailServerConnection connection = EmailServerConnectionUtil.openConnection(emailServerProperties, true)) {
            log.debug("Updating flag {} on email with messageId={}", flag, messageId);
            Folder folder = connection.folder();
            SearchTerm searchTerm = new HeaderTerm("Message-ID", messageId);
            Message[] messages = folder.search(searchTerm);

            for (Message message : messages) {
                message.setFlag(flag, true);
            }
        } catch (Exception e) {
            log.error("Failed to update flag {} on messageId {}", flag, messageId, e);
        }
    }

    /**
     * Builds a SearchTerm based on the provided subject pattern and email received timestamp.
     *
     * @param subjectLike        a string pattern to match against email subjects
     * @param emailReceivedAfter an Instant representing the earliest time an email can be received
     * @return a SearchTerm that can be used to filter emails, or null if no criteria are provided
     */
    private static SearchTerm buildSearchTerm(String subjectLike, Instant emailReceivedAfter) {
        if (subjectLike == null && emailReceivedAfter == null) {
            log.warn("No search criteria provided. Returning null SearchTerm.");
            return null;
        }
        log.debug("Building SearchTerm with subjectLike={} and emailReceivedAfter={}", subjectLike, emailReceivedAfter);
        List<SearchTerm> terms = new ArrayList<>();
        if (subjectLike != null) {
            terms.add(new SubjectTerm(subjectLike));
        }
        if (emailReceivedAfter != null) {
            terms.add(new ReceivedDateTerm(ComparisonTerm.GE, Date.from(emailReceivedAfter)));
        }
        if (terms.size() == 1) {
            return terms.getFirst();
        }
        // If multiple terms are present, combine them using AndTerm
        return new AndTerm(terms.toArray(new SearchTerm[0]));
    }
}
