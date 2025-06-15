package com.fmd.email_processor.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "batch.job")
public record BatchJobProperties(
        @Min(60_000)
        @Max(300_000)
        Long intervalMs,

        @Min(1)
        @Max(1000)
        Integer chunkSize,

        @Min(1)
        @Max(25)
        Integer maxRetry
) {
        /**
         * Default constructor for Spring Boot configuration properties.
         * Initializes with default values if not specified in application properties.
         */
        public BatchJobProperties {
                if (chunkSize == null) {
                        chunkSize = 100; // Default chunk size
                }
                if (maxRetry == null) {
                        maxRetry = 5; // Default max retry count
                }
                if (intervalMs == null) {
                        intervalMs = 60_000L; // Default to 1 minute if out of bounds
                }
        }
}

