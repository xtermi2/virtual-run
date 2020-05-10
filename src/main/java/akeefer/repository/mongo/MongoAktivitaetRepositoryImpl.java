package akeefer.repository.mongo;

import akeefer.model.mongo.Aktivitaet;
import akeefer.repository.mongo.dto.TotalUserDistance;
import akeefer.repository.mongo.dto.UserDistanceByDate;
import akeefer.repository.mongo.dto.UserDistanceByDateAndType;
import akeefer.repository.mongo.dto.UserDistanceByType;
import akeefer.util.Profiling;
import akeefer.web.charts.ChartIntervall;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.DateOperators;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    @Override
    @Profiling
    public List<UserDistanceByType> sumDistanceGroupedByActivityTypeAndFilterByOwnerAndDateRange(String owner,
                                                                                                 LocalDate from,
                                                                                                 LocalDate to) {
        LocalDateTime toEndOfDay = to.toLocalDateTime(LocalTime.MIDNIGHT.minusMillis(1));

        Aggregation totalDistance = newAggregation(
                match(where("owner").is(owner)
                        .andOperator(
                                where("aktivitaetsDatum").gte(from.toLocalDateTime(LocalTime.MIDNIGHT)),
                                where("aktivitaetsDatum").lte(toEndOfDay))),
                group("owner", "typ")
                        .sum("distanzInKilometer")
                        .as("totalDistanzInKilometer"));

        AggregationResults<UserDistanceByType> res = mongoTemplate.aggregate(totalDistance, Aktivitaet.class, UserDistanceByType.class);
        return res.getMappedResults();
    }

    @Override
    @Profiling
    public List<UserDistanceByDateAndType> sumDistanceGroupedByDateAndActivityTypeAndFilterByOwnerAndDateRange(String owner,
                                                                                                               LocalDate from,
                                                                                                               LocalDate to,
                                                                                                               ChartIntervall chartIntervall) {
        LocalDateTime toEndOfDay = to.toLocalDateTime(LocalTime.MIDNIGHT.minusMillis(1));

        Aggregation totalDistance = newAggregation(
                match(where("owner").is(owner)
                        .andOperator(
                                where("aktivitaetsDatum").gte(from.toLocalDateTime(LocalTime.MIDNIGHT)),
                                where("aktivitaetsDatum").lt(toEndOfDay))),
                project("owner", "aktivitaetsDatum", "typ", "distanzInKilometer")
                        .and("aktivitaetsDatum").dateAsFormattedString(chartIntervall.getMongoAggregationPattern()).as("dateKey"),
                group("owner", "dateKey", "typ")
                        .sum("distanzInKilometer")
                        .as("totalDistanzInKilometer"));

        AggregationResults<UserDistanceByDateAndType> res = mongoTemplate.aggregate(totalDistance, Aktivitaet.class, UserDistanceByDateAndType.class);
        return res.getMappedResults();
    }

    @Override
    @Profiling
    public List<UserDistanceByDate> sumDistanceGroupedByDateAndFilterByOwner(String... owners) {
        List<AggregationOperation> operations = new ArrayList<>();
        if (null != owners && owners.length > 0) {
            operations.add(match(where("owner").in(owners)));
        }
        operations.add(project("owner", "distanzInKilometer")
                .and(DateOperators.IsoWeekYear.isoWeekYearOf("aktivitaetsDatum")).as("isoWeekYear")
                .and(DateOperators.IsoWeek.isoWeekOf("aktivitaetsDatum")).as("isoWeekOfYear")
        );
        operations.add(group("owner", "isoWeekYear", "isoWeekOfYear")
                .sum("distanzInKilometer")
                .as("totalDistanzInKilometer"));

        Aggregation totalDistance = newAggregation(operations);

        AggregationResults<UserDistanceByDate> res = mongoTemplate.aggregate(totalDistance, Aktivitaet.class, UserDistanceByDate.class);
        return res.getMappedResults();
    }
}
