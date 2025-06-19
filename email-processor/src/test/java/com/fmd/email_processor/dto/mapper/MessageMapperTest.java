package com.fmd.email_processor.dto.mapper;

import com.fmd.email_processor.dto.EmailMessage;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Date;

class MessageMapperTest {

    @Test
    @DisplayName("should map Message to EmailMessage correctly")
    void mapTo_shouldMapFields() throws Exception {
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getSubject()).thenReturn("Test Subject");
        Mockito.when(message.getFrom()).thenReturn(new InternetAddress[]{new InternetAddress("sender@example.com")});
        Mockito.when(message.getReceivedDate()).thenReturn(new Date(123456789L));
        Mockito.when(message.getHeader("Message-ID")).thenReturn(new String[]{"<msgid@domain>"});
        Mockito.when(message.getContent()).thenReturn("Raw content");

        EmailMessage result = MessageMapper.mapTo(message);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.messageId()).isEqualTo("<msgid@domain>");
        Assertions.assertThat(result.subject()).isEqualTo("Test Subject");
        Assertions.assertThat(result.from()).isEqualTo("sender@example.com");
        Assertions.assertThat(result.receivedAt()).isEqualTo(123456789L);
        Assertions.assertThat(result.rawContent()).isEqualTo("Raw content");
    }

    @Test
    @DisplayName("should return null for null message")
    void mapTo_shouldReturnNullForNull() {
        Assertions.assertThat(MessageMapper.mapTo(null)).isNull();
    }

    @Test
    @DisplayName("should handle MessagingException gracefully")
    void mapTo_shouldHandleMessagingException() throws Exception {
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getSubject()).thenThrow(new MessagingException("error"));
        Assertions.assertThat(MessageMapper.mapTo(message)).isNull();
    }

    @Test
    @DisplayName("should handle null from address")
    void mapTo_shouldHandleNullFrom() throws Exception {
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getSubject()).thenReturn("No From");
        Mockito.when(message.getFrom()).thenReturn(null);
        Mockito.when(message.getReceivedDate()).thenReturn(new Date(123456789L));
        Mockito.when(message.getHeader("Message-ID")).thenReturn(new String[]{"<msgid@domain>"});
        Mockito.when(message.getContent()).thenReturn("Raw content");
        EmailMessage result = MessageMapper.mapTo(message);
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.from()).isNull();
    }

    @Test
    @DisplayName("should handle null received date")
    void mapTo_shouldHandleNullReceivedDate() throws Exception {
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getSubject()).thenReturn("No Date");
        Mockito.when(message.getFrom()).thenReturn(new InternetAddress[]{new InternetAddress("sender@example.com")});
        Mockito.when(message.getReceivedDate()).thenReturn(null);
        Mockito.when(message.getHeader("Message-ID")).thenReturn(new String[]{"<msgid@domain>"});
        Mockito.when(message.getContent()).thenReturn("Raw content");
        EmailMessage result = MessageMapper.mapTo(message);
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.receivedAt()).isNull();
    }

    @Test
    @DisplayName("should handle missing Message-ID header")
    void mapTo_shouldHandleMissingMessageId() throws Exception {
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getSubject()).thenReturn("No MessageId");
        Mockito.when(message.getFrom()).thenReturn(new InternetAddress[]{new InternetAddress("sender@example.com")});
        Mockito.when(message.getReceivedDate()).thenReturn(new Date(123456789L));
        Mockito.when(message.getHeader("Message-ID")).thenReturn(null);
        Mockito.when(message.getContent()).thenReturn("Raw content");
        EmailMessage result = MessageMapper.mapTo(message);
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.messageId()).isNull();
    }
}
