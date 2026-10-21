package ro.frit.ticketing.booking;

import org.springframework.stereotype.Component;
import ro.frit.ticketing.booking.dto.BookingResponse;
import ro.frit.ticketing.booking.dto.CreateBookingRequest;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Translates between the {@link Booking} entity and the booking DTOs, so neither side knows the other's shape.
 */
@Component
public class BookingMapper {

    public Booking toEntity(String id, String eventId, CreateBookingRequest request, LocalDateTime bookedAt) {
        return Booking.builder()
                .id(id)
                .eventId(eventId)
                .customerEmail(request.customerEmail())
                .seats(request.seats())
                .bookedAt(bookedAt)
                .build();
    }

    public BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getEventId(),
                booking.getCustomerEmail(),
                booking.getSeats(),
                booking.getBookedAt());
    }

    public BookingSummaryResponse toSummary(String eventId, List<Booking> bookings) {
        int bookingCount = bookings.size();
        int totalSeats = bookings.stream().mapToInt(Booking::getSeats).sum();
        Double averageSeats = bookingCount == 0 ? null : (double) totalSeats / bookingCount;
        return new BookingSummaryResponse(eventId, bookingCount, totalSeats, averageSeats);
    }
}
