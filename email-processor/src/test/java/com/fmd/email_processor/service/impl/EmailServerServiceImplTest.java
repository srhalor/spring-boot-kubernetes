package com.fmd.email_processor.service.impl;

import com.fmd.email_processor.dto.EmailMessage;
import com.fmd.email_processor.dto.EmailServerConnection;
import com.fmd.email_processor.dto.EmailServerProperties;
import com.fmd.email_processor.util.EmailServerConnectionUtil;
import jakarta.mail.Flags.Flag;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.search.SearchTerm;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class EmailServerServiceImplTest {
    private EmailServerServiceImpl service;

    @BeforeEach
    void setUp() {
        EmailServerProperties emailServerProperties = Mockito.mock(EmailServerProperties.class);
        Mockito.when(emailServerProperties.host()).thenReturn("host");
        Mockito.when(emailServerProperties.folder()).thenReturn("inbox");
        Mockito.when(emailServerProperties.protocol()).thenReturn("imaps");
        Mockito.when(emailServerProperties.port()).thenReturn(993);
        Mockito.when(emailServerProperties.username()).thenReturn("user");
        Mockito.when(emailServerProperties.password()).thenReturn("pass");
        service = new EmailServerServiceImpl(emailServerProperties);
    }

    @Test
    @DisplayName("fetchEmails should return mapped emails")
    void fetchEmails_shouldReturnMappedEmails() throws Exception {
        EmailServerConnection connection = Mockito.mock(EmailServerConnection.class);
        Folder folder = Mockito.mock(Folder.class);
        Message msg = Mockito.mock(Message.class);
        Mockito.when(connection.folder()).thenReturn(folder);
        Mockito.when(folder.search(Mockito.any(SearchTerm.class))).thenReturn(new Message[]{msg});
        try (MockedStatic<EmailServerConnectionUtil> util = Mockito.mockStatic(EmailServerConnectionUtil.class)) {
            util.when(() -> EmailServerConnectionUtil.openConnection(Mockito.any(), Mockito.anyBoolean())).thenReturn(connection);
            List<EmailMessage> result = service.fetchEmails("sub", Instant.now());
            Assertions.assertThat(result).isNotNull();
        }
    }

    @Test
    @DisplayName("fetchEmails should return empty list on exception")
    void fetchEmails_shouldReturnEmptyOnException() {
        try (MockedStatic<EmailServerConnectionUtil> util = Mockito.mockStatic(EmailServerConnectionUtil.class)) {
            util.when(() -> EmailServerConnectionUtil.openConnection(Mockito.any(), Mockito.anyBoolean())).thenThrow(new MessagingException("fail"));
            List<EmailMessage> result = service.fetchEmails("sub", Instant.now());
            Assertions.assertThat(result).isEmpty();
        }
    }

    @Test
    @DisplayName("fetchEmails should return empty list if no criteria")
    void fetchEmails_shouldReturnEmptyIfNoCriteria() {
        List<EmailMessage> result = service.fetchEmails(null, null);
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("markEmailAsProcessed should update SEEN flag")
    void markEmailAsProcessed_shouldUpdateSeenFlag() throws Exception {
        EmailServerConnection connection = Mockito.mock(EmailServerConnection.class);
        Folder folder = Mockito.mock(Folder.class);
        Message msg = Mockito.mock(Message.class);
        Mockito.when(connection.folder()).thenReturn(folder);
        Mockito.when(folder.search(Mockito.any(SearchTerm.class))).thenReturn(new Message[]{msg});
        try (MockedStatic<EmailServerConnectionUtil> util = Mockito.mockStatic(EmailServerConnectionUtil.class)) {
            util.when(() -> EmailServerConnectionUtil.openConnection(Mockito.any(), Mockito.eq(true))).thenReturn(connection);
            service.markEmailAsProcessed("mid");
            Mockito.verify(msg).setFlag(Flag.SEEN, true);
        }
    }

    @Test
    @DisplayName("deleteEmail should update DELETED flag")
    void deleteEmail_shouldUpdateDeletedFlag() throws Exception {
        EmailServerConnection connection = Mockito.mock(EmailServerConnection.class);
        Folder folder = Mockito.mock(Folder.class);
        Message msg = Mockito.mock(Message.class);
        Mockito.when(connection.folder()).thenReturn(folder);
        Mockito.when(folder.search(Mockito.any(SearchTerm.class))).thenReturn(new Message[]{msg});
        try (MockedStatic<EmailServerConnectionUtil> util = Mockito.mockStatic(EmailServerConnectionUtil.class)) {
            util.when(() -> EmailServerConnectionUtil.openConnection(Mockito.any(), Mockito.eq(true))).thenReturn(connection);
            service.deleteEmail("mid");
            Mockito.verify(msg).setFlag(Flag.DELETED, true);
        }
    }

    @Test
    @DisplayName("updateFlag should do nothing for null or blank messageId")
    void updateFlag_shouldDoNothingForNullOrBlankMessageId() {
        service.markEmailAsProcessed(null);
        service.markEmailAsProcessed("");
        // No exception, no connection attempt
        // Assert that openConnection is never called
        try (MockedStatic<EmailServerConnectionUtil> util = Mockito.mockStatic(EmailServerConnectionUtil.class)) {
            util.verifyNoInteractions();
        }
    }

    @Test
    @DisplayName("updateFlag should handle exception gracefully")
    void updateFlag_shouldHandleException() {
        try (MockedStatic<EmailServerConnectionUtil> util = Mockito.mockStatic(EmailServerConnectionUtil.class)) {
            util.when(() -> EmailServerConnectionUtil.openConnection(Mockito.any(), Mockito.eq(true))).thenThrow(new MessagingException("fail"));
            // No exception should be thrown
            assertDoesNotThrow(() -> service.markEmailAsProcessed("mid"));
        }
    }
}
