package akeefer.repository.mongo;

import akeefer.model.mongo.Aktivitaet;
import akeefer.repository.mongo.dto.TotalUserDistance;
import akeefer.util.Profiling;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Slf4j
public class MongoAktivitaetRepositoryImpl implements MongoAktivitaetRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<String> findAllIds() {
        return StreamSupport.stream(mongoTemplate.getCollection(mongoTemplate.getCollectionName(Aktivitaet.class))
                .distinct("_id", String.class).spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
//    @CacheResult(cacheName = "aktivitaeten")
    @Profiling
    @Transactional(readOnly = true)
    public List<Aktivitaet> findAllByOwner(String username) {
        log.info("findAllByOwner({}) in Database", username);
        return mongoTemplate.find(query(where("owner").is(username)), Aktivitaet.class);
    }

    @Override
//    @CacheRemove(cacheName = "aktivitaeten")
    @Transactional
    @Profiling
    public void deleteAktivitaet(Aktivitaet aktivitaet) {
        mongoTemplate.remove(aktivitaet);
    }

    @Override
    @Profiling
    public List<TotalUserDistance> calculateTotalDistanceForAllUsers() {
        Aggregation totalDistance = newAggregation(
                project("owner", "distanzInKilometer")
                        .and("owner").as("dummy"),
                group("owner", "dummy") // I have no glue, why group only works as expected when 2 arguments are provided
                        .sum("distanzInKilometer")
                        .as("totalDistanzInKilometer"),
                sort(Sort.Direction.ASC, previousOperation(), "owner"));

        AggregationResults<TotalUserDistance> res = mongoTemplate.aggregate(totalDistance, Aktivitaet.class, TotalUserDistance.class);
        return res.getMappedResults();
    }
}
