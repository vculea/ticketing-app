package ro.frit.ticketing.event;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ro.frit.ticketing.config.TestcontainerConfiguration;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Spring test (a small context: the repository and Spring Boot's validation setup): an invalid {@link Event} is
 * rejected when it is saved, and nothing is stored. Without Spring, the {@code @Valid} on {@code save} is not checked.
 */
@SpringJUnitConfig(EventRepository.class)
@ImportAutoConfiguration(ValidationAutoConfiguration.class)
@DataMongoTest
@Import({EventMapper.class, EventService.class, TestcontainerConfiguration.class})
class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

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
