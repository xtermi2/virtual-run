package akeefer.repository.mongo;

import akeefer.model.mongo.Aktivitaet;
import akeefer.repository.mongo.dto.TotalUserDistance;
import akeefer.repository.mongo.dto.UserDistanceByType;
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
}
