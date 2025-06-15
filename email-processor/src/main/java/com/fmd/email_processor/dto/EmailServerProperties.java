package com.fmd.email_processor.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

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
