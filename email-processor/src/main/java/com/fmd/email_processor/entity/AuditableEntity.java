package com.fmd.email_processor.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.time.Instant;

/**
 * Base class for entities that require auditing information.
 * Provides fields for creation and update timestamps.
 * <p>
 * This class should be extended by any entity that requires auditing.
 * </p>
 *
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
@MappedSuperclass
@Getter
@Setter
public abstract class AuditableEntity implements Serializable {

    @Column(name = "created_at", updatable = false, nullable = false)
    @CreatedDate
    private Instant createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private Instant updatedAt;

    /**
     * Default constructor.
     * <p>
     * Protected to prevent instantiation outside of subclasses.
     * </p>
     */
    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * Updates the updatedAt field to the current timestamp.
     * <p>
     * This method is called before the entity is updated in the database.
     * </p>
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
