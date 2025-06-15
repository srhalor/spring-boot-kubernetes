package com.fmd.email_processor.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for the email server used in batch jobs.
 * <p>
 * This class holds the properties required to connect to an email server,
 * including host, username, password, folder, port, and protocol.
 * </p>
 *
 * @param host     the email server host (e.g., imap.example.com)
 * @param username the username for the email account
 * @param password the password for the email account
 * @param folder   the folder to monitor (e.g., INBOX)
 * @param port     the port number for the email server (default is 993 for IMAP)
 * @param protocol the protocol to use (default is "imaps")
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
@Validated
@ConfigurationProperties(prefix = "batch.job.email")
public record EmailServerProperties(
        @NotBlank
        String host,

        @NotBlank
        String username,

        @NotBlank
        String password,

        @NotBlank
        String folder,

        @NotNull
        @Min(1)
        @Max(65535)
        Integer port,

        @NotBlank
        String protocol
) {
    /**
     * Default constructor for Spring Boot configuration properties.
     * Initializes with default values if not specified in application properties.
     */
    public EmailServerProperties {
        if (port == null) {
            port = 993; // Default IMAP port
        }
        if (protocol == null || protocol.isBlank()) {
            protocol = "imaps"; // Default protocol
        }
    }
}
