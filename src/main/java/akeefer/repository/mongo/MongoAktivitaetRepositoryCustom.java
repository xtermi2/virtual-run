package akeefer.repository.mongo;

import akeefer.model.mongo.Aktivitaet;
import akeefer.repository.mongo.dto.TotalUserDistance;
import akeefer.repository.mongo.dto.UserDistanceByDate;
import akeefer.repository.mongo.dto.UserDistanceByDateAndType;
import akeefer.repository.mongo.dto.UserDistanceByType;
import akeefer.web.charts.ChartIntervall;
import org.joda.time.LocalDate;

import java.util.List;

public interface MongoAktivitaetRepositoryCustom {
    List<String> findAllIds();

    List<Aktivitaet> findAllByOwner(String username);

    void deleteAktivitaet(Aktivitaet aktivitaet);

    List<TotalUserDistance> calculateTotalDistanceForAllUsers();

    /**
     * @param owner a user to filter by
     * @param from  Date range start filter (inclusive)
     * @param to    Date range end filter (inclusive)
     * @return List containing all aggregated (summed) distances of the given user grouped by ActivityType
     */
    List<UserDistanceByType> sumDistanceGroupedByActivityTypeAndFilterByOwnerAndDateRange(String owner,
                                                                                          LocalDate from,
                                                                                          LocalDate to);

    /**
     * @param owner          a user to filter by
     * @param from           Date range start filter (inclusive)
     * @param to             Date range end filter (exclusive)
     * @param chartIntervall defines the date pattern (date interval aggregation)
     * @return List containing all aggregated (summed) distances of the given user grouped by date and ActivityType
     */
    List<UserDistanceByDateAndType> sumDistanceGroupedByDateAndActivityTypeAndFilterByOwnerAndDateRange(String owner,
                                                                                                        LocalDate from,
                                                                                                        LocalDate to,
                                                                                                        ChartIntervall chartIntervall);

    /**
     * @param owners usernames to filter by, if empty, no filter is set
     * @return List containing all aggregated (summed) distances of the given users grouped by year and week number
     */
    List<UserDistanceByDate> sumDistanceGroupedByDateAndFilterByOwner(String... owners);
}
