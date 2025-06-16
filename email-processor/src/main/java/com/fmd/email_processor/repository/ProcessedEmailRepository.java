package com.fmd.email_processor.repository;

import com.fmd.email_processor.entity.ProcessedEmailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing processed email entities.
 * <p>
 * This interface provides methods to check if an email has already been processed
 * based on its message ID and associated OrderRequest ID.
 * </p>
 *
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
public interface ProcessedEmailRepository extends JpaRepository<ProcessedEmailEntity, Long> {

    /**
     * Checks if an email with the given message ID and associated OrderRequest ID
     * already exists in the repository.
     *
     * @param messageId      the unique identifier of the email message
     * @param orderRequestId the ID of the associated OrderRequest
     * @return true if the email has already been processed, false otherwise
     */
    boolean existsByMessageIdAndOrderRequestId(String messageId, Long orderRequestId);
}
