package com.fmd.email_processor.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents an order request in the system.
 * <p>
 * This entity is used to track order requests, their status, and processing details.
 * </p>
 *
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "order_requests")
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(nullable = false, length = 40)
    private String status;

    @Column(nullable = false)
    private boolean processed;

    @Column(nullable = false)
    private int retryCount;

    private String failureReason;
}
