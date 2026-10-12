package ro.fruit.packeting;

import org.junit.jupiter.api.Test;
import ro.frit.ticheting.domain.BookingResult;
import ro.frit.ticheting.domain.Event;
import ro.frit.ticheting.domain.TicketOffice;
import ro.frit.ticheting.domain.Venue;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class TicketOfficeTest {
    @Test
    void unknownEventIsReported() {
        TicketOffice ticketOffice = new TicketOffice(List.of(event("1", 100, 0)));
        ticketOffice.book(new BookingRequest("Event 20", "beni@nu.ro", 2));
        assertEquals(BookingResult.eventNotFound("Event 20"), ticketOffice.book(new BookingRequest("Event 20", "beni@nu.ro", 2)));
    }

    @Test
    void soldOutEventIsReported() {
        TicketOffice ticketOffice = new TicketOffice(List.of(event("event-1", 10, 8)));
        BookingResult book = ticketOffice.book(new BookingRequest("event-1", "beni@nu.ro", 5));
        assertEquals(BookingResult.notEnoughSeats("event-1"), book);
    }

    @Test
    void bookingIsSuccessful() {
        TicketOffice ticketOffice = new TicketOffice(List.of(event("event-1", 10, 8)));
        BookingResult result = ticketOffice.book(new BookingRequest("event-1", "beni@nu.ro", 2));
        BookingResult.Confirmed confirmed = assertInstanceOf(BookingResult.Confirmed.class, result);
        assertEquals("event-1", confirmed.eventId());
    }

    private static Event event(String id, int capacity, int bookedSeats) {
        return Event.builder()
                .id(id)
                .name("Event " + id)
                .venue(new Venue("Venue " + id, "Address " + id))
                .startAt(LocalDateTime.now().plusDays(7))
                .capacity(capacity)
                .bookedSeats(bookedSeats)
                .build();
    }
}
