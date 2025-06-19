package com.fmd.email_processor.repository;

import com.fmd.email_processor.entity.OrderRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Repository interface for managing OrderRequest entities.
 * <p>
 * This interface provides methods to fetch and update order requests in the database.
 * It supports batch processing with optimistic locking to handle concurrent updates.
 * </p>
 *
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
@Repository
public interface OrderRequestRepository extends JpaRepository<OrderRequest, Long> {

    /**
     * Fetches the next batch of unprocessed OrderRequests that have not exceeded the maximum retry count.
     * <p>
     * This method uses pessimistic locking to ensure that the fetched records are locked for processing,
     * preventing other transactions from modifying them until the current transaction is completed.
     * </p>
     *
     * @param maxRetry  maximum retry count for processing
     * @param chunkSize number of records to fetch in this batch
     * @return list of OrderRequest entities ready for processing
     */
    @Query(
            value = """
                        SELECT *
                        FROM order_requests
                        WHERE processed = false
                          AND retry_count < :maxRetry
                        ORDER BY created_date ASC
                        LIMIT :chunkSize
                        FOR UPDATE SKIP LOCKED
                    """,
            nativeQuery = true
    )
    List<OrderRequest> fetchNextBatch(
            @Param("maxRetry") int maxRetry,
            @Param("chunkSize") int chunkSize
    );

    /**
     * Updates the status and processing details of an OrderRequest.
     * <p>
     * This method is used to mark an order request as processed or to update its retry count and failure reason.
     * </p>
     *
     * @param id            ID of the OrderRequest to update
     * @param status        new status of the OrderRequest
     * @param processed     whether the OrderRequest has been processed
     * @param retryCount    number of retries attempted for this OrderRequest
     * @param failureReason reason for failure, if applicable
     * @return number of rows affected by the update
     */
    @Modifying
    @Transactional
    @Query("""
                UPDATE OrderRequest o SET
                  o.status = :status,
                  o.processed = :processed,
                  o.retryCount = :retryCount,
                  o.failureReason = :failureReason
                WHERE o.id = :id
            """)
    int updateOrderRequest(
            @Param("id") Long id,
            @Param("status") String status,
            @Param("processed") boolean processed,
            @Param("retryCount") int retryCount,
            @Param("failureReason") String failureReason
    );
}
