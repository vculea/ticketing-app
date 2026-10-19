package ro.frit.ticketing.event;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

@Repository
@Validated
public interface EventRepository extends MongoRepository<Event, String> {

}
