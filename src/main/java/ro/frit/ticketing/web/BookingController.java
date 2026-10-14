package ro.frit.ticketing.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.frit.ticketing.domain.BookingRequest;
import ro.frit.ticketing.domain.BookingResult;
import ro.frit.ticketing.service.TicketOffice;

@RestController
@RequestMapping("/events/{eventId}/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final TicketOffice ticketOffice;

    @PostMapping
    public ResponseEntity<?> book(
            @PathVariable String eventId,
            @Valid @RequestBody BookingPayload payload) {
        BookingResult result = ticketOffice.book(
                new BookingRequest(eventId, payload.customerEmail(), payload.seats()));

        return switch (result) {
            case BookingResult.Confirmed confirmed ->
                    ResponseEntity.status(HttpStatus.CREATED).body(BookingResponse.from(confirmed.booking()));
            case BookingResult.UnknownEvent unknownEvent ->
                    ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ProblemDetail.forStatusAndDetail(
                                    HttpStatus.NOT_FOUND, "No event with id " + unknownEvent.eventId()));
            case BookingResult.SoldOut soldOut -> {
                ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                        HttpStatus.CONFLICT, "Not enough seats available for event " + soldOut.eventId());
                problem.setProperty("availableSeats", soldOut.availableSeats());
                yield ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
            }
            case BookingResult.AlreadyStarted alreadyStarted -> {
                ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                        HttpStatus.CONFLICT, "Event " + alreadyStarted.eventId() + " has already started");
                problem.setProperty("startsAt", alreadyStarted.startsAt());
                yield ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
            }
        };
    }

    private record BookingPayload(
            @NotBlank @Email String customerEmail,
            @Positive int seats) {
    }
}
