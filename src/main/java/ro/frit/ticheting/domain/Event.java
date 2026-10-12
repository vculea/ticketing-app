package ro.frit.ticheting.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@ToString
@Builder(toBuilder = true)
@EqualsAndHashCode
public class Event {
    private String id;
    private String name;
    private Venue venue;
    private LocalDateTime startAt;
    private int capacity;
    private int bookedSeats;
}
