package ro.frit.ticketing.event.dto;

import java.time.LocalDateTime;

public record EventSearchRequest(String city,
                                 String name,
                                 LocalDateTime from,
                                 LocalDateTime to) {
}
