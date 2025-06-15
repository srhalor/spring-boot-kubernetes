package com.fmd.email_processor.dto.mapper;

import com.fmd.email_processor.dto.EmailMessage;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * Utility class to map a Jakarta Mail Message to an EmailMessage DTO.
 * Handles null checks and exceptions gracefully.
 *
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@UtilityClass
public class MessageMapper {

    /**
     * Maps a Jakarta Mail Message to an EmailMessage DTO.
     *
     * @param message the Jakarta Mail Message to map
     * @return an EmailMessage DTO containing the mapped data, or null if mapping fails
     */
    public static EmailMessage mapTo(Message message) {
        if (message == null) return null;
        try {
            return EmailMessage.builder()
                    .messageId(getHeader(message))
                    .subject(message.getSubject())
                    .from(message.getFrom() != null ? message.getFrom()[0].toString() : null)
                    .receivedAt(getReceivedAt(message))
                    .rawContent(Objects.toString(message.getContent(), null))
                    .build();
        } catch (MessagingException e) {
            log.error("Failed to map Message to EmailMessage", e);
            return null;
        } catch (Exception e) {
            log.error("Unexpected error while mapping Message to EmailMessage", e);
            return null;
        }
    }

    /**
     * Utility to extract the received date from a Message.
     *
     * @param message the message
     * @return the received date as milliseconds since epoch, or null if not available
     */
    private static Long getReceivedAt(Message message) throws MessagingException {
        if (message.getReceivedDate() != null) {
            return message.getReceivedDate().toInstant().toEpochMilli();
        }
        return null;
    }

    /**
     * Extracts the Message-ID header from a Jakarta Mail Message.
     *
     * @param message the message to extract the header from
     * @return the Message-ID header value, or null if not present
     * @throws MessagingException if an error occurs while accessing the message headers
     */
    private static String getHeader(Message message) throws MessagingException {
        log.debug("Extracting Message-ID header from message: {}", message.getSubject());
        String[] values = message.getHeader("Message-ID");
        if (null != values && values.length > 0) {
            return values[0];
        }
        return null;
    }
}
