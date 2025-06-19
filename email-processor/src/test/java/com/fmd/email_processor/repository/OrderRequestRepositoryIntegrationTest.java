package com.fmd.email_processor.repository;

import com.fmd.email_processor.entity.OrderRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@DataJpaTest
@ExtendWith(SpringExtension.class)
class OrderRequestRepositoryIntegrationTest {
    @Autowired
    private OrderRequestRepository repository;

    @Test
    @DisplayName("should fetch next batch of unprocessed order requests")
    void fetchNextBatch_shouldReturnUnprocessed() {
        addNewOrder("A");
        addNewOrder("B");
        List<OrderRequest> batch = repository.fetchNextBatch(3, 2);
        Assertions.assertThat(batch).hasSize(2);
    }

    /**
     * Inserts sample data into the repository for testing purposes.
     *
     * @param name the name of the order request
     */
    private void addNewOrder(String name) {
        OrderRequest orderRequest = OrderRequest.builder()
                .name(name)
                .status("NEW")
                .processed(false)
                .retryCount(0)
                .build();
        repository.save(orderRequest);
    }
}
