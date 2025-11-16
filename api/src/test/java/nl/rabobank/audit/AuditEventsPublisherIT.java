package nl.rabobank.audit;

import lombok.val;
import nl.rabobank.config.AsyncConfig;
import nl.rabobank.repository.AuditLogRepository;
import nl.rabobank.service.IdGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.Executor;

import static nl.rabobank.TestUtils.givenPowerOfAttorney;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        AsyncConfig.class,
        AuditEventsPublisher.class,
        AuditLogEventListener.class,
        AuditEventsPublisherIT.TestConfig.class
})
@Import({})
class AuditEventsPublisherIT {

    @MockitoBean
    private AuditLogRepository auditLogRepository;

    @Autowired
    private AuditEventsPublisher publisher;

    @Test
    void publishCreated_shouldReachAsyncListener_andPersist() {
        // Given
        val poa = givenPowerOfAttorney();

        // When
        publisher.publishCreated(poa);

        // Then
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    // Test configuration that overrides the async executor to run synchronously
    static class TestConfig {
        @Bean(name = "auditExecutor")
        @Primary
        public Executor auditExecutor() {
            return Runnable::run; // synchronous
        }

        @Bean
        @Primary
        public Clock clock() {
            return Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));
        }

        @Bean
        @Primary
        public IdGenerator idGenerator() {
            return new IdGenerator() {
                @Override
                public String generateUUID() {
                    return "it-audit-id";
                }
            };
        }
    }
}
