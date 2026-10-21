package ro.frit.ticketing.booking;

import java.util.Map;

public interface BookingReportRepository {
    Map<String, Integer> sumSeatsPerEvent();
}
