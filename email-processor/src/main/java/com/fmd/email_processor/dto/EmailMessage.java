package com.fmd.email_processor.dto;

import lombok.Builder;

/**
 * Represents an email message fetched from the external server.
 *
 * @param messageId  unique message identifier (Message-ID header)
 * @param subject    email subject
 * @param from       sender address
 * @param receivedAt received timestamp (epoch ms)
 * @param rawContent raw message content
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
@Builder
public record EmailMessage(
        String messageId,
        String subject,
        String from,
        Long receivedAt,
        String rawContent
) {
}
