package ro.frit.ticheting.domain;

import java.time.LocalDateTime;

public sealed interface BookingResult permits BookingResult.AlreadyBooked, BookingResult.Confirmed, BookingResult.NotEnoughSeats, BookingResult.SoldOut, BookingResult.UnknownEvent {
    static UnknownEvent eventNotFound(String eventId) {
        return new UnknownEvent(eventId);
    }

    static NotEnoughSeats notEnoughSeats(String eventId) {
        return new NotEnoughSeats(eventId);
    }

    record SoldOut() implements BookingResult {
        @Override
        public String eventId() {
            return "";
        }
    }

    record UnknownEvent(String eventId) implements BookingResult {
    }

    record AlreadyBooked(String eventId, LocalDateTime bookingTime) implements BookingResult {
    }

    record Confirmed(String eventId, LocalDateTime bookingTime) implements BookingResult {
    }

    default String describe() {
        return switch (this) {
            case SoldOut s -> "Sold out";
            case AlreadyBooked a -> "Already booked: " + a.eventId() + ", booking time: " + a.bookingTime();
            case Confirmed c -> "Confirmed: " + c.eventId() + ", booking time: " + c.bookingTime();
            case UnknownEvent u -> "Unknown event: " + u.eventId();
            case NotEnoughSeats n -> "Not enough seats: " + n.eventId();
        };
    }

    String eventId();

    record NotEnoughSeats(String eventId) implements BookingResult {
    }
}
