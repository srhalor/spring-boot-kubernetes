package com.fmd.email_processor.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@MappedSuperclass
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class AuditableEntity implements Serializable {

    @Column(name = "created_date", updatable = false, nullable = false)
    @CreatedDate
    private Instant createdAt;

    @Column(name = "updated_date")
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
        log.debug("Setting createdAt and updatedAt timestamps for entity: {}", this.getClass().getSimpleName());
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
        log.debug("Updating updatedAt timestamp for entity: {}", this.getClass().getSimpleName());
        this.updatedAt = Instant.now();
    }
}
