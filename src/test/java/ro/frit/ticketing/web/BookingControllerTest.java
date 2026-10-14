package ro.frit.ticketing.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ro.frit.ticketing.domain.Booking;
import ro.frit.ticketing.domain.BookingRequest;
import ro.frit.ticketing.domain.BookingResult;
import ro.frit.ticketing.service.TicketOffice;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.TEXT_PLAIN;
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
    void addsBookingForCustomer() throws Exception {
        LocalDateTime bookedAt = LocalDateTime.of(2026, 3, 1, 19, 0);
        Booking booking = Booking.builder()
                .id("b1")
                .eventId("e1")
                .customerEmail("john.doe@example.com")
                .seats(2)
                .bookedAt(bookedAt)
                .build();
        when(ticketOffice.book(VALID_REQUEST)).thenReturn(new BookingResult.Confirmed(booking));

        mockMvc.perform(post("/events/e1/bookings").contentType(APPLICATION_JSON.toString()).content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("b1"))
                .andExpect(jsonPath("$.eventId").value("e1"))
                .andExpect(jsonPath("$.customerEmail").value("john.doe@example.com"))
                .andExpect(jsonPath("$.seats").value(2))
                .andExpect(jsonPath("$.bookedAt").value("2026-03-01T19:00:00"));

        verify(ticketOffice).book(VALID_REQUEST);
    }

    @Test
    void unknownEventIsNotFound() throws Exception {
        when(ticketOffice.book(new BookingRequest("e9", "john.doe@example.com", 2)))
                .thenReturn(new BookingResult.UnknownEvent("e9"));

        mockMvc.perform(post("/events/e9/bookings").contentType(APPLICATION_JSON.toString()).content(VALID_BODY))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("No event with id e9"));

        verify(ticketOffice).book(new BookingRequest("e9", "john.doe@example.com", 2));
    }

    @Test
    void soldOutIsConflictWithAvailableSeats() throws Exception {
        when(ticketOffice.book(VALID_REQUEST)).thenReturn(new BookingResult.SoldOut("e1", 1));

        mockMvc.perform(post("/events/e1/bookings").contentType(APPLICATION_JSON.toString()).content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").value("Not enough seats available for event e1"))
                .andExpect(jsonPath("$.availableSeats").value(1));

        verify(ticketOffice).book(VALID_REQUEST);
    }

    @Test
    void alreadyStartedIsConflictWithStartsAt() throws Exception {
        when(ticketOffice.book(VALID_REQUEST))
                .thenReturn(new BookingResult.AlreadyStarted("e1", LocalDateTime.of(2026, 3, 1, 20, 0)));

        mockMvc.perform(post("/events/e1/bookings").contentType(APPLICATION_JSON.toString()).content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").value("Event e1 has already started"))
                .andExpect(jsonPath("$.startsAt").value("2026-03-01T20:00:00"));

        verify(ticketOffice).book(VALID_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(strings = {"not-an-email", "john.doe@", "@example.com", "", " "})
    void invalidEmailIsBadRequest(String email) throws Exception {
        assertBadRequest("{\"customerEmail\":\"" + email + "\",\"seats\":2}");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void invalidSeatNumberIsBadRequest(int seats) throws Exception {
        assertBadRequest("{\"customerEmail\":\"john.doe@example.com\",\"seats\":" + seats + "}");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{}",
            "{\"seats\":2}",
            "{\"customerEmail\":\"john.doe@example.com\"}",
            "{\"customerEmail\":null,\"seats\":2}",
            "{\"customerEmail\":\"john.doe@example.com\",\"seats\":null}",
            "null",
            ""
    })
    void incompletePayloadIsBadRequest(String body) throws Exception {
        assertBadRequest(body);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"customerEmail\":\"john.doe@example.com\",\"seats\":2",
            "{customerEmail:\"john.doe@example.com\",\"seats\":2}"
    })
    void brokenJsonIsBadRequest(String body) throws Exception {
        assertBadRequest(body);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"customerEmail\":\"john.doe@example.com\",\"seats\":\"two\"}",
            "{\"customerEmail\":\"john.doe@example.com\",\"seats\":true}",
            "{\"customerEmail\":\"john.doe@example.com\",\"seats\":[]}",
            "{\"customerEmail\":{},\"seats\":2}"
    })
    void wrongFieldTypeIsBadRequest(String body) throws Exception {
        assertBadRequest(body);
    }

    @Test
    void nonJsonBodyIsBadRequest() throws Exception {
        assertBadRequest("this is not JSON");
    }

    @Test
    void nonJsonContentTypeIsUnsupportedMediaType() throws Exception {
        mockMvc.perform(post("/events/e1/bookings").contentType(TEXT_PLAIN).content(VALID_BODY))
                .andExpect(status().isUnsupportedMediaType());

        verifyNoInteractions(ticketOffice);
    }

    private void assertBadRequest(String body) throws Exception {
        mockMvc.perform(post("/events/e1/bookings").contentType(APPLICATION_JSON.toString()).content(body))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(ticketOffice);
    }
}