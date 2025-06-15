package com.fmd.email_processor.util;

import com.fmd.email_processor.dto.EmailServerConnection;
import com.fmd.email_processor.dto.EmailServerProperties;
import jakarta.mail.Folder;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;
import lombok.experimental.UtilityClass;

import java.util.Properties;

/**
 * Utility for creating email server connections.
 */
@UtilityClass
public final class EmailServerConnectionUtil {

    /**
     * Opens a new mail Store and Folder for the given properties.
     * Caller should use try-with-resources on the returned EmailServerConnection.
     */
    public static EmailServerConnection openConnection(EmailServerProperties props, boolean readWrite) throws MessagingException {
        Properties mailProps = new Properties();
        mailProps.put("mail.store.protocol", props.protocol());
        mailProps.put("mail.imaps.port", String.valueOf(props.port()));

        Session session = Session.getInstance(mailProps);

        Store store = session.getStore();
        store.connect(props.host(), props.port(), props.username(), props.password());
        Folder folder = store.getFolder(props.folder());
        folder.open(readWrite ? Folder.READ_WRITE : Folder.READ_ONLY);

        return EmailServerConnection.builder()
                .store(store)
                .folder(folder)
                .build();
    }
}