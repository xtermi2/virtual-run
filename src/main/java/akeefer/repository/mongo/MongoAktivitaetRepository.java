package akeefer.repository.mongo;

import akeefer.model.mongo.Aktivitaet;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Set;

public interface MongoAktivitaetRepository extends MongoRepository<Aktivitaet, String>, MongoAktivitaetRepositoryCustom {

    List<Aktivitaet> findByOwnerIn(Set<String> usernameFilter);
}
