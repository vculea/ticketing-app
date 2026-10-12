package ro.frit.ticheting.domain;

import ro.fruit.packeting.BookingRequest;

import java.time.LocalDateTime;
import java.util.*;

public class TicketOffice {
    private Map<String, Event> events = new LinkedHashMap<>();
    private final List<Booking> bookings = new ArrayList<>();

    public TicketOffice(List<Event> events) {
        for (Event event : events) {
            this.events.put(event.getId(), event);
        }
    }

    public Optional<Booking> getBooking(String id) {
        return bookings.stream().filter(booking -> booking.getId().equals(id)).findFirst();
    }

    public BookingResult book(BookingRequest bookingRequest) {
        Event event = events.get(bookingRequest.event());
        if (event == null) {
            return new BookingResult.UnknownEvent(bookingRequest.event());
        }
        if (bookingRequest.tickets() > event.getAvailableSeats()) {
            return new BookingResult.NotEnoughSeats(event.getId());
        }

        LocalDateTime bookingTime = LocalDateTime.now();
        bookings.add(new Booking(
                UUID.randomUUID().toString(),
                event.getId(),
                bookingRequest.email(),
                bookingRequest.tickets()
        ));
        events.put(event.getId(), event.toBuilder()
                .bookedSeats(event.getBookedSeats() + bookingRequest.tickets())
                .build());
        return new BookingResult.Confirmed(event.getId(), bookingTime);
    }
}
