package com.fmd.email_processor.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for batch job processing in the Email Processor service.
 * <p>
 * This class defines properties related to batch job execution, such as interval,
 * chunk size, and maximum retry attempts.
 * </p>
 *
 * @param intervalMs the interval in milliseconds between batch job executions
 * @param chunkSize  the number of records to process in each batch
 * @param maxRetry   the maximum number of retry attempts for failed jobs
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
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
            // Default chunk size is set to 25 if not specified
            chunkSize = 25;
        }
        if (maxRetry == null) {
            // Default maximum retry attempts is set to 5 if not specified
            maxRetry = 5;
        }
        if (intervalMs == null) {
            // Default interval is set to 60 seconds (60,000 milliseconds) if not specified
            intervalMs = 60_000L;
        }
    }
}
