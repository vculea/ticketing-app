package ro.frit.ticketing.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import ro.frit.ticketing.event.dto.EventSearchRequest;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;

@Repository
@RequiredArgsConstructor
public class EventSearchRepositoryImpl implements EventSearchRepository {

    private final MongoTemplate mongoTemplate;

    public List<Event> search(EventSearchRequest filter) {
        List<Criteria> criteria = Stream.of(
                        ofNullable(filter.city())
                                .map(city -> Criteria.where("venue.city")
                                        .regex("^" + Pattern.quote(city) + "$", "i")),
                        ofNullable(filter.name())
                                .map(name -> Criteria.where("name").regex(Pattern.quote(name), "i")),
                        ofNullable(filter.from())
                                .map(from -> Criteria.where("startsAt").gte(from)),
                        ofNullable(filter.to())
                                .map(to -> Criteria.where("startsAt").lt(to)))
                .flatMap(Optional::stream)
                .toList();
        Query query = criteria.isEmpty() ? new Query() : new Query(new Criteria().andOperator(criteria));
        return mongoTemplate.find(query.with(Sort.by("startsAt")), Event.class);
    }
}
