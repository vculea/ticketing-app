package ro.frit.ticketing.booking;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;

import java.util.Map;
import java.util.stream.Collectors;

import static ro.frit.ticketing.booking.Booking.Fields.seats;

@RequiredArgsConstructor
public class BookingReportRepositoryImpl implements BookingReportRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public Map<String, Integer> sumSeatsPerEvent() {
        Aggregation pipeline = Aggregation.newAggregation(
                Aggregation.group(Booking.Fields.eventId).sum(seats).as(seats)
        );
        return mongoTemplate.aggregate(pipeline, Booking.class, SeatsPerEvent.class)
                .getMappedResults()
                .stream()
                .collect(Collectors.toMap(SeatsPerEvent::id, SeatsPerEvent::totalSeats));
    }

    @FieldNameConstants
    private record SeatsPerEvent(String id, int totalSeats) {
    }
}
