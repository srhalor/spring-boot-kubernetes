package com.fmd.email_processor.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Stores processed email Message-IDs and associates them with an order request.
 */
@Entity
@Table(name = "processed_email")
@Getter
@Setter
@NoArgsConstructor
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