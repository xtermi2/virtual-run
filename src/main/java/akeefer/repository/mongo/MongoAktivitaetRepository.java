package akeefer.repository.mongo;

import akeefer.model.mongo.Aktivitaet;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoAktivitaetRepository extends MongoRepository<Aktivitaet, String>, MongoAktivitaetRepositoryCustom {

}
