package com.fmd.email_processor.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
            log.warn("Chunk size is not specified, using default value of 25.");
            chunkSize = 25;
        }
        if (maxRetry == null) {
            log.warn("Max retry is not specified, using default value of 5.");
            maxRetry = 5;
        }
        if (intervalMs == null) {
            log.warn("Interval is not specified, using default value of 60 seconds.");
            intervalMs = 60_000L;
        }
    }
}
