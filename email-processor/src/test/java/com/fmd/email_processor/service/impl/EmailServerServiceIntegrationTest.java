package com.fmd.email_processor.service.impl;

import com.fmd.email_processor.dto.EmailMessage;
import com.fmd.email_processor.dto.EmailServerProperties;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.Instant;
import java.util.List;

class EmailServerServiceIntegrationTest {
    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.IMAP);

    private EmailServerServiceImpl service;

    @BeforeEach
    void setUp() {
        EmailServerProperties props = new EmailServerProperties(
                "localhost",
                "user",
                "password",
                "INBOX",
                3143,
                "imap"
        );
        service = new EmailServerServiceImpl(props);
        greenMail.setUser("user", "password");
    }

    @Test
    @DisplayName("should fetch emails from GreenMail server")
    void fetchEmails_shouldWork() {
        GreenMailUtil.sendTextEmailTest("user@localhost", "sender@localhost", "123", "body");
        greenMail.waitForIncomingEmail(1);
        List<EmailMessage> emails = service.fetchEmails("123", Instant.now().minusSeconds(60));
        Assertions.assertThat(emails).isNotEmpty();
        EmailMessage msg = emails.getFirst();
        Assertions.assertThat(msg.subject()).isEqualTo("123");
    }
}

