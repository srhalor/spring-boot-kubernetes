package com.fmd.email_processor.web;

import com.fmd.email_processor.dto.BatchJobProperties;
import com.fmd.email_processor.service.OrderRequestBatchProcessingService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;

/** * Controller for managing the Order Request Batch Job.
 * Provides endpoints to start, stop, and check the status of the batch job.
 * Automatically starts the job on application startup.
 *
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/batch-job")
@RequiredArgsConstructor
public class OrderRequestBatchJobController {

    private final OrderRequestBatchProcessingService batchProcessingService;
    private final BatchJobProperties batchJobProperties;

    // Dedicated scheduler for job interval execution.
    private final TaskScheduler taskScheduler = createScheduler();

    private ScheduledFuture<?> scheduledFuture;

    /**
     * Starts the batch job if it is not already running.
     * If already running, returns info message.
     */
    @PostMapping("/start")
    public synchronized String startJob() {
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            return "Batch job is already running.";
        }
        scheduledFuture = taskScheduler.scheduleAtFixedRate(
                batchProcessingService::fetchNextBatchAndProcess,
                Duration.ofMillis(batchJobProperties.intervalMs())
        );
        log.info("Order request batch job started with interval {} ms.", batchJobProperties.intervalMs());
        return "Batch job started.";
    }

    /**
     * Stops the batch job if it is currently running.
     * If not running, returns info message.
     */
    @PostMapping("/stop")
    public synchronized String stopJob() {
        if (scheduledFuture == null || scheduledFuture.isCancelled()) {
            return "Batch job is not running.";
        }
        scheduledFuture.cancel(false);
        log.info("Order request batch job stopped.");
        return "Batch job stopped.";
    }

    /**
     * Checks the status of the batch job.
     * Returns running status if job is active, otherwise returns stopped status.
     */
    @GetMapping("/status")
    public String jobStatus() {
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            return "Batch job is running.";
        }
        return "Batch job is stopped.";
    }

    /**
     * Automatically starts the batch job when the application context is initialized.
     * This ensures that the job runs without manual intervention after deployment.
     */
    @PostConstruct
    public void autoStartJob() {
        log.info("Auto-starting batch job at application startup.");
        startJob();
    }

    /**
     * Shuts down the scheduler when the application context is destroyed.
     * This ensures that all scheduled tasks are properly cleaned up.
     */
    @PreDestroy
    public void shutdownScheduler() {
        if (taskScheduler instanceof ThreadPoolTaskScheduler scheduler) {
            scheduler.shutdown();
        }
    }

    /**
     * Creates a ThreadPoolTaskScheduler for scheduling the batch job.
     * Configured with a single thread and a custom thread name prefix.
     *
     * @return the configured TaskScheduler instance
     */
    private static TaskScheduler createScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("batch-job-scheduler-");
        scheduler.setPoolSize(1); // Single thread for job scheduling
        scheduler.initialize();
        return scheduler;
    }
}
