package com.fmd.email_processor.repository;

import com.fmd.email_processor.entity.ProcessedEmailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for managing processed email records.
 * Stores Message-IDs and associates them with order requests.
 */
public interface ProcessedEmailRepository extends JpaRepository<ProcessedEmailEntity, Long> {
    boolean existsByMessageIdAndOrderRequestId(String messageId, Long orderRequestId);
}