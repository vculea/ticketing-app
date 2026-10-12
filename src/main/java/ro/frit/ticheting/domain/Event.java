package ro.frit.ticheting.domain;

import java.time.LocalDateTime;

public class Event {
    private final String id;
    private final String name;
    private final Venue venue;
    private final LocalDateTime startAt;
    private final int capacity;
    private final int bookedSeats;

    public Event(String id, String name, Venue venue, LocalDateTime startAt, int capacity, int bookedSeats) {
        this.id = id;
        this.name = name;
        this.venue = venue;
        this.startAt = startAt;
        this.capacity = capacity;
        this.bookedSeats = bookedSeats;
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Venue getVenue() {
        return venue;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getBookedSeats() {
        return bookedSeats;
    }
}
