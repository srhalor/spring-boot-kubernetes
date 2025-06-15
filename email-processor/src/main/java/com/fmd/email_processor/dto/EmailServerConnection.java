package com.fmd.email_processor.dto;

import jakarta.mail.Folder;
import jakarta.mail.MessagingException;
import jakarta.mail.Store;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents a connection to an email server, encapsulating the store and folder.
 * Implements AutoCloseable to ensure resources are released properly.
 * <p>
 * This class is used to manage the connection to an email server and the specific folder
 * being accessed, providing a clean way to close connections when done.
 * </p>
 *
 * @param store  the email store connection
 * @param folder the folder being accessed
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@Builder
public record EmailServerConnection(Store store, Folder folder) implements AutoCloseable {

    /**
     * Closes the email folder and store connections.
     * <p>
     * This method ensures that both the folder and store are closed properly,
     * handling any exceptions that may occur during the closing process.
     * </p>
     */
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
