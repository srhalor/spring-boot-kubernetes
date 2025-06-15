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
 * Utility class for managing email server connections.
 * <p>
 * This class provides methods to open a connection to an email server,
 * allowing for interaction with mail folders and messages.
 * </p>
 *
 * @author Shailesh Halor
 * @version 1.0
 * @since 1.0
 */
@UtilityClass
public final class EmailServerConnectionUtil {

    /**
     * Opens a connection to the email server using the provided properties.
     *
     * @param props     the properties containing email server configuration
     * @param readWrite whether to open the folder in read-write mode
     * @return an EmailServerConnection object containing the store and folder
     * @throws MessagingException if there is an error connecting to the server or opening the folder
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
