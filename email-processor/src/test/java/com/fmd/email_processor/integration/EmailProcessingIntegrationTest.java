package com.fmd.email_processor.integration;

import com.fmd.email_processor.dto.EmailServerProperties;
import com.fmd.email_processor.entity.OrderRequest;
import com.fmd.email_processor.repository.OrderRequestRepository;
import com.fmd.email_processor.repository.ProcessedEmailRepository;
import com.fmd.email_processor.service.impl.EmailProcessingServiceImpl;
import com.fmd.email_processor.service.impl.EmailServerServiceImpl;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;

@DataJpaTest
@ExtendWith(SpringExtension.class)
class EmailProcessingIntegrationTest {
    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(new ServerSetup[]{ServerSetupTest.SMTP, ServerSetupTest.IMAP});

    @Autowired
    private OrderRequestRepository orderRequestRepository;
    @Autowired
    private ProcessedEmailRepository processedEmailRepository;

    private EmailProcessingServiceImpl emailProcessingService;

    @BeforeEach
    void setUp() {
        EmailServerProperties props = new EmailServerProperties(
                "localhost",
                "user",
                "password",
                "INBOX",
                ServerSetupTest.IMAP.getPort(),
                "imap"
        );
        EmailServerServiceImpl emailServerService = new EmailServerServiceImpl(props);
        emailProcessingService = new EmailProcessingServiceImpl(emailServerService, processedEmailRepository);
        greenMail.setUser("user@localhost", "user", "password");
    }

    @Test
    @DisplayName("should fetch and persist new emails end-to-end")
    void fetchAndPersistNewEmails_endToEnd() {
        OrderRequest order = OrderRequest.builder().name("A").status("NEW").processed(false).retryCount(0).createdAt(Instant.now().minusSeconds(60)).build();
        order = orderRequestRepository.save(order);
        GreenMailUtil.sendTextEmailTest("user@localhost", "sender@localhost", String.valueOf(order.getId()), "body");
        greenMail.waitForIncomingEmail(1);
        emailProcessingService.fetchAndPersistNewEmails(order);
        Assertions.assertThat(processedEmailRepository.findAll()).isNotEmpty();
    }

    @Test
    @DisplayName("should not persist if no emails are present")
    void shouldNotPersistIfNoEmailsPresent() {
        OrderRequest order = OrderRequest.builder().name("B").status("NEW").processed(false).retryCount(0).createdAt(Instant.now().minusSeconds(60)).build();
        order = orderRequestRepository.save(order);
        // No email sent
        emailProcessingService.fetchAndPersistNewEmails(order);
        Assertions.assertThat(processedEmailRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("should not persist duplicate emails for same order request")
    void shouldNotPersistDuplicateEmails() {
        OrderRequest order = OrderRequest.builder().name("C").status("NEW").processed(false).retryCount(0).createdAt(Instant.now().minusSeconds(60)).build();
        order = orderRequestRepository.save(order);
        GreenMailUtil.sendTextEmailTest("user@localhost", "sender@localhost", String.valueOf(order.getId()), "body");
        greenMail.waitForIncomingEmail(1);
        // First processing
        emailProcessingService.fetchAndPersistNewEmails(order);
        int countAfterFirst = processedEmailRepository.findAll().size();
        // Second processing (should not add duplicate)
        emailProcessingService.fetchAndPersistNewEmails(order);
        int countAfterSecond = processedEmailRepository.findAll().size();
        Assertions.assertThat(countAfterSecond).isEqualTo(countAfterFirst);
    }

    @Test
    @DisplayName("should persist emails for multiple order requests independently")
    void shouldPersistEmailsForMultipleOrders() {
        OrderRequest order1 = OrderRequest.builder().name("D1").status("NEW").processed(false).retryCount(0).createdAt(Instant.now().minusSeconds(120)).build();
        OrderRequest order2 = OrderRequest.builder().name("D2").status("NEW").processed(false).retryCount(0).createdAt(Instant.now().minusSeconds(60)).build();
        order1 = orderRequestRepository.save(order1);
        order2 = orderRequestRepository.save(order2);
        GreenMailUtil.sendTextEmailTest("user@localhost", "sender@localhost", String.valueOf(order1.getId()), "body1");
        GreenMailUtil.sendTextEmailTest("user@localhost", "sender@localhost", String.valueOf(order2.getId()), "body2");
        greenMail.waitForIncomingEmail(2);
        emailProcessingService.fetchAndPersistNewEmails(order1);
        emailProcessingService.fetchAndPersistNewEmails(order2);
        Assertions.assertThat(processedEmailRepository.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("should not persist if email server fails")
    void shouldNotPersistIfEmailServerFails() {
        // Simulate by using wrong credentials
        EmailServerProperties badProps = new EmailServerProperties(
                "localhost",
                "baduser",
                "badpassword",
                "INBOX",
                3143,
                "imap"
        );
        EmailServerServiceImpl badServer = new EmailServerServiceImpl(badProps);
        EmailProcessingServiceImpl badProcessing = new EmailProcessingServiceImpl(badServer, processedEmailRepository);
        OrderRequest order = OrderRequest.builder().name("E").status("NEW").processed(false).retryCount(0).createdAt(Instant.now().minusSeconds(60)).build();
        order = orderRequestRepository.save(order);
        // No email will be fetched due to bad credentials
        badProcessing.fetchAndPersistNewEmails(order);
        Assertions.assertThat(processedEmailRepository.findAll()).isEmpty();
    }
}
