package com.fmd.email_processor.repository;

import com.fmd.email_processor.entity.OrderRequest;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Repository abstraction for order requests. Replace with JPA as needed.
 */
@Repository
public interface OrderRequestRepository extends JpaRepository<OrderRequest, Long> {
    /**
     * Fetches the next batch of order requests to process.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
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
     * Atomically updates the status, processed flag, and retry count for a given OrderRequest.
     *
     * @param id         the OrderRequest ID
     * @param status     new status
     * @param processed  new processed flag
     * @param retryCount new retry count
     * @return rows updated (should be 1 if success)
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