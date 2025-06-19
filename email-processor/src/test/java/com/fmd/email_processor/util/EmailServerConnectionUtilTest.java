package com.fmd.email_processor.util;

import com.fmd.email_processor.dto.EmailServerConnection;
import com.fmd.email_processor.dto.EmailServerProperties;
import jakarta.mail.Folder;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServerConnectionUtilTest {

    @Mock
    private Store store;
    @Mock
    private Folder folder;
    @Mock
    private Session session;
    private EmailServerProperties props;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        props = mock(EmailServerProperties.class);
        when(props.host()).thenReturn("test.host");
        when(props.protocol()).thenReturn("imaps");
        when(props.port()).thenReturn(993);
        when(props.username()).thenReturn("user");
        when(props.password()).thenReturn("pass");
        when(props.folder()).thenReturn("INBOX");
    }

    @Test
    void openConnection_success() throws Exception {
        try (var sessionMock = Mockito.mockStatic(Session.class)) {
            sessionMock.when(() -> Session.getInstance(any(Properties.class))).thenReturn(session);
            when(session.getStore()).thenReturn(store);
            doNothing().when(store).connect(anyString(), anyInt(), anyString(), anyString());
            when(store.getFolder(anyString())).thenReturn(folder);
            doNothing().when(folder).open(anyInt());

            EmailServerConnection connection = EmailServerConnectionUtil.openConnection(props, true);
            assertNotNull(connection);
            assertEquals(store, connection.store());
            assertEquals(folder, connection.folder());
        }
    }

    @Test
    void openConnection_messagingException() throws Exception {
        try (var sessionMock = Mockito.mockStatic(Session.class)) {
            sessionMock.when(() -> Session.getInstance(any(Properties.class))).thenReturn(session);
            when(session.getStore()).thenReturn(store);
            doThrow(new MessagingException("Connection failed")).when(store).connect(anyString(), anyInt(), anyString(), anyString());

            assertThrows(MessagingException.class, () -> EmailServerConnectionUtil.openConnection(props, true));
        }
    }
}
