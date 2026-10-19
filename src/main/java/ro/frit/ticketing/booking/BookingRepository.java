package ro.frit.ticketing.booking;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

@Repository
@Validated
public interface BookingRepository extends MongoRepository<Booking, String> {
}
