package ro.frit.ticheting.domain;

import java.time.LocalDateTime;

public sealed interface BookingResult permits BookingResult.AlreadyBooked, BookingResult.SoldOut, BookingResult.UnknownEvent {
    record SoldOut(String message, String availableSeats) implements BookingResult {
    }

    record UnknownEvent(String eventId) implements BookingResult {
    }

    record AlreadyBooked(String eventId, LocalDateTime bookingTime) implements BookingResult {
    }

    default String describe() {
        return switch (this) {
            case SoldOut s -> "Sold out: " + s.message() + ", available seats: " + s.availableSeats();
            case AlreadyBooked a -> "Already booked: " + a.eventId() + ", booking time: " + a.bookingTime();
            case UnknownEvent u -> "Unknown event: " + u.eventId();
        };
    }
}
