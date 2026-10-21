package ro.frit.ticketing.event;

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
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
@ImportAutoConfiguration(ValidationAutoConfiguration.class)
@Import({MongoValidationConfig.class, TestcontainerConfiguration.class})
class SearchWithQueryDemo {
    private static final LocalDateTime NOW = LocalDateTime.of(2030, 1, 1, 0, 0);
    private static final LocalDateTime NEXT_YEAR = LocalDateTime.of(2031, 1, 1, 0, 0);

    @Autowired
    private EventRepository eventRepository;

    @BeforeEach
    void clearEvents() {
        eventRepository.deleteAll();
        eventRepository.saveAll(List.of(
                event("e1", "Rock Night", "Cluj", LocalDateTime.of(2030, 6, 12, 20, 0)),
                event("e2", "Rock Fest", "Cluj", LocalDateTime.of(2032, 6, 12, 20, 0)),
                event("e3", "Rock in Iasi", "Iasi", LocalDateTime.of(2030, 6, 12, 20, 0)),
                event("e4", "Jazz Night", "Cluj", LocalDateTime.of(2030, 6, 12, 20, 0))));
    }

    @Test
    void searchesWithOptionalCityAndDateFilters() {
        assertEquals(Set.of("e1"), ids(print("all four filters, city Cluj",
                () -> eventRepository.search("Cluj", "rock", NOW, NEXT_YEAR))));
        assertEquals(Set.of("e1", "e2"), ids(print("no dates sent",
                () -> eventRepository.search("Cluj", "rock", null, null))));
        assertEquals(Set.of("e1", "e3"), ids(print("no city sent",
                () -> eventRepository.search(null, "rock", NOW, NEXT_YEAR))));
        assertEquals(Set.of("e1", "e3"), ids(print("city ''",
                () -> eventRepository.search("", "rock", NOW, NEXT_YEAR))));
    }

    private static List<Event> print(String call, Supplier<List<Event>> search) {
        List<Event> events = search.get();
        System.out.printf("QUERY %s: %s%n", call, events.stream()
                .map(event -> event.getName() + " in " + event.getVenue().city())
                .toList());
        return events;
    }

    private static Event event(String id, String name, String city, LocalDateTime startsAt) {
        return Event.builder()
                .id(id)
                .name(name)
                .venue(new Venue("Arena", city))
                .startsAt(startsAt)
                .capacity(100)
                .build();
    }

    private static Set<String> ids(List<Event> events) {
        return events.stream().map(Event::getId).collect(Collectors.toSet());
    }
}
