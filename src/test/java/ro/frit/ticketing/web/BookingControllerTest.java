package ro.frit.ticketing.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ro.frit.ticketing.domain.BookingRequest;
import ro.frit.ticketing.domain.BookingResult;
import ro.frit.ticketing.service.TicketOffice;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    private static final String VALID_BODY = "{\n" +
            "  \"customerEmail\": \"john.doe@example.com\",\n" +
            "  \"seats\": 2\n" +
            "}";

    private static final BookingRequest VALID_REQUEST = BookingRequest.builder()
            .eventId("e1")
            .customerEmail("john.doe@example.com")
            .seats(2)
            .build();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TicketOffice ticketOffice;

    @Test
    void unknownEventIsNotFound() throws Exception {
        when(ticketOffice.book(new BookingRequest("e9", "john.doe@example.com", 2)))
                .thenReturn(new BookingResult.UnknownEvent("e9"));

        mockMvc.perform(post("/events/e9/bookings").contentType(APPLICATION_JSON.toString()).content(VALID_BODY))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("No event with id e9"));
    }

    @Test
    void soldOutIsConflictWithAvailableSeats() throws Exception {
        when(ticketOffice.book(VALID_REQUEST)).thenReturn(new BookingResult.SoldOut("e1", 1));

        mockMvc.perform(post("/events/e1/bookings").contentType(APPLICATION_JSON.toString()).content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.availableSeats").value(1));
    }

    @Test
    void alreadyStartedIsConflictWithStartsAt() throws Exception {
        when(ticketOffice.book(VALID_REQUEST))
                .thenReturn(new BookingResult.AlreadyStarted("e1", LocalDateTime.of(2026, 3, 1, 20, 0)));

        mockMvc.perform(post("/events/e1/bookings").contentType(APPLICATION_JSON.toString()).content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.startsAt").value("2026-03-01T20:00:00"));
    }
}