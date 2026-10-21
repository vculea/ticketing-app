package ro.frit.ticketing.event;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Validated
public interface EventRepository extends MongoRepository<Event, String>, EventSearchRepository {

    @Query("""
            { '$expr': { '$and': [
                { '$cond': [ { '$eq': [?0, null] }, true,
                    { '$regexMatch': { 'input': '$venue.city', 'regex': ?0, 'options': 'i' } } ] },
                { '$regexMatch': { 'input': '$name', 'regex': ?1, 'options': 'i' } },
                { '$cond': [ { '$eq': [?2, null] }, true, { '$gte': ['$startsAt', ?2] } ] },
                { '$cond': [ { '$eq': [?3, null] }, true, { '$lte': ['$startsAt', ?3] } ] }
            ] } }
            """)
    List<Event> search(String city, String name, LocalDateTime from, LocalDateTime to);

}
