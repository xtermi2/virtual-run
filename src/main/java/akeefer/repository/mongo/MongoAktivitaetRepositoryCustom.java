package akeefer.repository.mongo;

import akeefer.model.mongo.Aktivitaet;

import java.util.List;

public interface MongoAktivitaetRepositoryCustom {
    List<String> findAllIds();

    List<Aktivitaet> findAllByOwner(String username);

    void deleteAktivitaet(Aktivitaet aktivitaet);
}
