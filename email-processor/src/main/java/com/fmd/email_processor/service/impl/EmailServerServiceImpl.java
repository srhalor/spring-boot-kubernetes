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
 * Default implementation of {@link EmailServerService}.
 * <p>
 * Connects to an IMAP email server using {@link EmailServerProperties} and manages resources via try-with-resources.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServerServiceImpl implements EmailServerService {

    private final EmailServerProperties emailServerProperties;

    @Override
    public List<EmailMessage> fetchEmails(String subjectLike, Instant emailReceivedAfter) {
        SearchTerm searchTerm = buildSearchTerm(subjectLike, emailReceivedAfter);

        // If no search criteria are provided, do not process and return empty result.
        if (searchTerm == null) {
            log.warn("No search criteria provided for fetching emails. Aborting fetch.");
            return List.of();
        }

        try (EmailServerConnection connection = EmailServerConnectionUtil.openConnection(emailServerProperties, false)) {
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

    @Override
    public void markEmailAsProcessed(String messageId) {
        updateFlag(messageId, Flag.SEEN);
    }

    @Override
    public void deleteEmail(String messageId) {
        updateFlag(messageId, Flag.DELETED);
    }

    /**
     * Utility to update message flags.
     */
    private void updateFlag(String messageId, Flag flag) {
        try (EmailServerConnection connection = EmailServerConnectionUtil.openConnection(emailServerProperties, true)) {
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
     * Builds a composite SearchTerm for IMAP search based on provided filters.
     *
     * @param subjectLike  pattern for subject or null
     * @return SearchTerm or null if no filters applied
     */
    private static SearchTerm buildSearchTerm(String subjectLike, Instant emailReceivedAfter) {
        List<SearchTerm> terms = new ArrayList<>();
        if (subjectLike != null) terms.add(new SubjectTerm(subjectLike));
        if (emailReceivedAfter != null)
            terms.add(new ReceivedDateTerm(ComparisonTerm.GE, Date.from(emailReceivedAfter)));

        if (terms.isEmpty()) return null;
        if (terms.size() == 1) return terms.getFirst();
        return new AndTerm(terms.toArray(new SearchTerm[0]));
    }
}