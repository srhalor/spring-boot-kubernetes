package com.fmd.email_processor.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a processed email entity in the system.
 * <p>
 * This entity is used to track emails that have been processed, including their
 * associated OrderRequest ID and unique message identifier.
 * </p>
 *
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "processed_email")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessedEmailEntity extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String messageId;

    /**
     * The ID of the OrderRequest associated with this processed email.
     */
    @Column(nullable = false)
    private Long orderRequestId;
}