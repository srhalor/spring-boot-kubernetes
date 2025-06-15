package com.fmd.email_processor.dto.mapper;

import com.fmd.email_processor.dto.EmailMessage;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author Shailesh Halor
 */
@Slf4j
@UtilityClass
public class MessageMapper {
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

    private static Long getReceivedAt(Message message) throws MessagingException {
        if (message.getReceivedDate() != null) {
            return message.getReceivedDate().toInstant().toEpochMilli();
        }
        return null;
    }

    /**
     * Utility to extract a single header value from a Message.
     *
     * @param message the message
     * @return the value, or null if not found or error
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
