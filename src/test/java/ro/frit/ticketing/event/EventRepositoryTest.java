package ro.frit.ticketing.event;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration;
import org.springframework.context.annotation.Import;
import ro.frit.ticketing.config.MongoValidationConfig;
import ro.frit.ticketing.config.TestcontainerConfiguration;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Spring test (a Mongo slice with Spring Boot's validation setup): an invalid {@link Event} is rejected when it is
 * saved, and nothing is stored. The check is done by the {@code ValidatingEntityCallback} from {@link MongoValidationConfig}.
 */
@DataMongoTest
@ImportAutoConfiguration(ValidationAutoConfiguration.class)
@Import({MongoValidationConfig.class, TestcontainerConfiguration.class})
class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @BeforeEach
    void clearEvents() {
        eventRepository.deleteAll();
    }

    @Test
    void invalidEventIsNotSaved() {
        Event invalid = Event.builder()
                .id("e1")
                .name(" ")
                .venue(new Venue("Arena", "Cluj"))
                .startsAt(LocalDateTime.now().plusDays(7))
                .capacity(100)
                .build();

        assertThrows(ConstraintViolationException.class, () -> eventRepository.save(invalid));
        assertTrue(eventRepository.findById("e1").isEmpty());
    }

}
