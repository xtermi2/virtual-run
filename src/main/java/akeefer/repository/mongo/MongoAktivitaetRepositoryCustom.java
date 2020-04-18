package akeefer.repository.mongo;

import akeefer.model.mongo.Aktivitaet;
import akeefer.repository.mongo.dto.TotalUserDistance;

import java.util.List;

public interface MongoAktivitaetRepositoryCustom {
    List<String> findAllIds();

    List<Aktivitaet> findAllByOwner(String username);

    void deleteAktivitaet(Aktivitaet aktivitaet);

    List<TotalUserDistance> calculateTotalDistanceForAllUsers();
}
