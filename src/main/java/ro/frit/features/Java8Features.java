package ro.frit.features;

import ro.frit.ticheting.domain.Event;
import ro.frit.ticheting.domain.Venue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Java8Features {
    static void main(String[] args) {
        lamdas();
        streams();
        optional();
    }

    static List<Event> events() {
        return Arrays.asList(
                new Event("1", "Rock Night", new Venue("Stadium A", "Cluj"), LocalDateTime.of(2024, 6, 15, 20, 0), 1000, 500),
                new Event("2", "Jazz Evening", new Venue("Stadium B", "Bucuresti"), LocalDateTime.of(2024, 7, 20, 19, 30), 1500, 1200),
                new Event("3", "La Travita", new Venue("Stadium C", "Iasi"), LocalDateTime.of(2024, 8, 10, 18, 0), 800, 600),
                new Event("4", "Concert D", new Venue("Stadium D", "Oradea"), LocalDateTime.of(2024, 9, 5, 21, 0), 2000, 1800)
        );
    }

    private static void optional() {
        System.out.println("----Optional----");
        List<Event> events = events();
        Event found = null;
        for (Event event : events) {
            if (event.getId().equalsIgnoreCase("nu-exista")) {
                found = event;
                break;
            }

        }
        if (found != null) {
            System.out.println(found);
        } else {
            System.out.println("Nu exista");
        }

        Optional<Event> any = events.stream().filter(event -> event.getId().equalsIgnoreCase("nu-exista"))
                .findAny();
        if (any.isPresent()) {
            System.out.println(any.get());
        } else {
            System.out.println("Nu exista");
        }

        events.stream().filter(event -> event.getId().equalsIgnoreCase("nu-exista"))
                .findAny().orElseGet(() -> new Event("0", "Nu exista", new Venue("N/A", "N/A"), LocalDateTime.now(), 0, 0));
    }

    private static void lamdas() {
        System.out.println("Lamdas");
        List<Event> events = events();
        Predicate<Event> predicate = new Predicate<Event>() {
            @Override
            public boolean test(Event event) {
                return event.getBookedSeats() >= 200;
            }
        };

        List<Event> filteredEvents = new ArrayList<>();
        for (Event event : events) {
            if (predicate.test(event)) {
                filteredEvents.add(event);
            }
        }

        filteredEvents.forEach(System.out::println);
        System.out.println("Using Lamdas");

        List<Event> collect = events.stream()
                .filter(event -> event.getBookedSeats() >= 200)
                .toList();
    }

    private static void streams() {
        System.out.println("----- Streams ------");
        List<String> filteredEvents = events().stream()
                .filter(event -> event.getCapacity() > 300)
                .map(Event::getName)
                .toList();

        filteredEvents.forEach(System.out::println);
    }
}
