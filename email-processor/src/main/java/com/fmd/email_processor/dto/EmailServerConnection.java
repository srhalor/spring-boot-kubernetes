package com.fmd.email_processor.dto;

import jakarta.mail.Folder;
import jakarta.mail.MessagingException;
import jakarta.mail.Store;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Shailesh Halor
 */
@Slf4j
@Builder
public record EmailServerConnection(Store store, Folder folder) implements AutoCloseable {

    @Override
    public void close() {
        try {
            if (folder != null && folder.isOpen()) {
                folder.close(true);
            }
        } catch (MessagingException e) {
            log.warn("Failed to close folder: {}", e.getMessage());
        }
        try {
            if (store != null && store.isConnected()) {
                store.close();
            }
        } catch (MessagingException e) {
            log.warn("Failed to close store: {}", e.getMessage());
        }
    }
}
