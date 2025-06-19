package com.fmd.email_processor.service.impl;

import com.fmd.email_processor.dto.BatchJobProperties;
import com.fmd.email_processor.entity.OrderRequest;
import com.fmd.email_processor.repository.OrderRequestRepository;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

class OrderRequestServiceImplTest {
    @Mock
    private OrderRequestRepository orderRequestRepository;
    @Mock
    private BatchJobProperties properties;
    @InjectMocks
    private OrderRequestServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(properties.maxRetry()).thenReturn(3);
        Mockito.when(properties.chunkSize()).thenReturn(2);
    }

    @Test
    @DisplayName("fetchNextBatch should delegate to repository and return results")
    void fetchNextBatch_shouldDelegate() {
        List<OrderRequest> expected = List.of(OrderRequest.builder().id(1L).build());
        Mockito.when(orderRequestRepository.fetchNextBatch(3, 2)).thenReturn(expected);
        Assertions.assertThat(service.fetchNextBatch()).isEqualTo(expected);
    }

    @Test
    @DisplayName("fetchNextBatch should return empty list if repository returns empty")
    void fetchNextBatch_shouldReturnEmpty() {
        Mockito.when(orderRequestRepository.fetchNextBatch(3, 2)).thenReturn(List.of());
        Assertions.assertThat(service.fetchNextBatch()).isEmpty();
    }

    @Test
    @DisplayName("markProcessed should update order request and reset retry count")
    void markProcessed_shouldUpdate() {
        OrderRequest req = OrderRequest.builder().id(1L).retryCount(2).build();
        Mockito.when(orderRequestRepository.findById(1L)).thenReturn(Optional.of(req));
        Mockito.when(orderRequestRepository.updateOrderRequest(Mockito.eq(1L), Mockito.any(), Mockito.anyBoolean(), Mockito.anyInt(), Mockito.isNull())).thenReturn(1);
        service.markProcessed(1L);
        Mockito.verify(orderRequestRepository).updateOrderRequest(1L, "Completed", true, 0, null);
    }

    @Test
    @DisplayName("markFailed should increment retry count and set failure reason")
    void markFailed_shouldUpdateWithFailure() {
        OrderRequest req = OrderRequest.builder().id(1L).retryCount(1).build();
        Mockito.when(orderRequestRepository.findById(1L)).thenReturn(Optional.of(req));
        Mockito.when(orderRequestRepository.updateOrderRequest(Mockito.eq(1L), Mockito.any(), Mockito.anyBoolean(), Mockito.anyInt(), Mockito.any())).thenReturn(1);
        service.markFailed(1L, "fail");
        Mockito.verify(orderRequestRepository).updateOrderRequest(1L, "Error", false, 2, "fail");
    }

    @Test
    @DisplayName("should throw if not found")
    void updateOrderRequest_shouldThrowIfNotFound() {
        Mockito.when(orderRequestRepository.findById(2L)).thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(() -> service.markProcessed(2L)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("should throw if max retry exceeded")
    void updateOrderRequest_shouldThrowIfMaxRetryExceeded() {
        OrderRequest req = OrderRequest.builder().id(1L).retryCount(3).build();
        Mockito.when(orderRequestRepository.findById(1L)).thenReturn(Optional.of(req));
        Assertions.assertThatThrownBy(() -> service.markFailed(1L, "fail")).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("should log error if update fails (update count != 1)")
    void updateOrderRequest_shouldLogErrorIfUpdateFails() {
        OrderRequest req = OrderRequest.builder().id(1L).retryCount(0).build();
        Mockito.when(orderRequestRepository.findById(1L)).thenReturn(Optional.of(req));
        Mockito.when(orderRequestRepository.updateOrderRequest(Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyInt(), Mockito.any())).thenReturn(0);
        service.markProcessed(1L);
        Mockito.verify(orderRequestRepository).updateOrderRequest(1L, "Completed", true, 0, null);
    }
}
