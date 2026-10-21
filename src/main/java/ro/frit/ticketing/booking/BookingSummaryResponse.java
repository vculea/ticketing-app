package ro.frit.ticketing.booking;

public record BookingSummaryResponse(String eventId,
                                     int bookings,
                                     int seats,
                                     Double averageSeatsPerBooking) {
}
