package com.fmd.email_processor.service.impl;

import com.fmd.email_processor.dto.EmailMessage;
import com.fmd.email_processor.entity.OrderRequest;
import com.fmd.email_processor.entity.ProcessedEmailEntity;
import com.fmd.email_processor.repository.ProcessedEmailRepository;
import com.fmd.email_processor.service.EmailServerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.List;

class EmailProcessingServiceImplTest {
    @Mock
    private EmailServerService emailServerService;
    @Mock
    private ProcessedEmailRepository processedEmailRepository;
    @InjectMocks
    private EmailProcessingServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("should fetch and persist new emails")
    void fetchAndPersistNewEmails_shouldPersist() {
        OrderRequest order = OrderRequest.builder().id(1L).createdAt(Instant.now()).build();
        EmailMessage email = EmailMessage.builder().messageId("mid").build();
        Mockito.when(emailServerService.fetchEmails(Mockito.anyString(), Mockito.any())).thenReturn(List.of(email));
        Mockito.when(processedEmailRepository.existsByMessageIdAndOrderRequestId("mid", 1L)).thenReturn(false);
        Mockito.when(processedEmailRepository.save(Mockito.any(ProcessedEmailEntity.class))).thenReturn(null);
        service.fetchAndPersistNewEmails(order);
        Mockito.verify(processedEmailRepository).save(Mockito.any(ProcessedEmailEntity.class));
        Mockito.verify(emailServerService).markEmailAsProcessed("mid");
    }

    @Test
    @DisplayName("should not persist already processed emails")
    void fetchAndPersistNewEmails_shouldSkipProcessed() {
        OrderRequest order = OrderRequest.builder().id(1L).createdAt(Instant.now()).build();
        EmailMessage email = EmailMessage.builder().messageId("mid").build();
        Mockito.when(emailServerService.fetchEmails(Mockito.anyString(), Mockito.any())).thenReturn(List.of(email));
        Mockito.when(processedEmailRepository.existsByMessageIdAndOrderRequestId("mid", 1L)).thenReturn(true);
        service.fetchAndPersistNewEmails(order);
        Mockito.verify(processedEmailRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(emailServerService, Mockito.never()).markEmailAsProcessed(Mockito.any());
    }

    @Test
    @DisplayName("should handle null orderRequest gracefully")
    void fetchAndPersistNewEmails_shouldHandleNullOrderRequest() {
        service.fetchAndPersistNewEmails(null);
        Mockito.verify(emailServerService, Mockito.never()).fetchEmails(Mockito.anyString(), Mockito.any());
    }

    @Test
    @DisplayName("should handle null/empty emails from server")
    void fetchAndPersistNewEmails_shouldHandleNoEmails() {
        OrderRequest order = OrderRequest.builder().id(1L).createdAt(Instant.now()).build();
        Mockito.when(emailServerService.fetchEmails(Mockito.anyString(), Mockito.any())).thenReturn(List.of());
        service.fetchAndPersistNewEmails(order);
        Mockito.verify(processedEmailRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("should handle exception during persistence")
    void fetchAndPersistNewEmails_shouldHandlePersistenceException() {
        OrderRequest order = OrderRequest.builder().id(1L).createdAt(Instant.now()).build();
        EmailMessage email = EmailMessage.builder().messageId("mid").build();
        Mockito.when(emailServerService.fetchEmails(Mockito.anyString(), Mockito.any())).thenReturn(List.of(email));
        Mockito.when(processedEmailRepository.existsByMessageIdAndOrderRequestId("mid", 1L)).thenReturn(false);
        Mockito.when(processedEmailRepository.save(Mockito.any(ProcessedEmailEntity.class))).thenThrow(new RuntimeException("DB error"));
        service.fetchAndPersistNewEmails(order);
        Mockito.verify(processedEmailRepository).save(Mockito.any(ProcessedEmailEntity.class));
        Mockito.verify(emailServerService, Mockito.never()).markEmailAsProcessed("mid");
    }
}
