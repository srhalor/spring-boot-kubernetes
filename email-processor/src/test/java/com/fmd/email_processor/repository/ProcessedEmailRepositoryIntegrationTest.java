package com.fmd.email_processor.repository;

import com.fmd.email_processor.entity.ProcessedEmailEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
class ProcessedEmailRepositoryIntegrationTest {
    @Autowired
    private ProcessedEmailRepository repository;

    @Test
    @DisplayName("should check existence by messageId and orderRequestId")
    void existsByMessageIdAndOrderRequestId_shouldWork() {
        ProcessedEmailEntity entity = ProcessedEmailEntity.builder().messageId("mid").orderRequestId(1L).build();
        repository.save(entity);
        Assertions.assertThat(repository.existsByMessageIdAndOrderRequestId("mid", 1L)).isTrue();
        Assertions.assertThat(repository.existsByMessageIdAndOrderRequestId("other", 1L)).isFalse();
    }
}

