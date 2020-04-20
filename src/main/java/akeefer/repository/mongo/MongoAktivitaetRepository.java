package akeefer.repository.mongo;

import akeefer.model.mongo.Aktivitaet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Set;

public interface MongoAktivitaetRepository extends MongoRepository<Aktivitaet, String>, MongoAktivitaetRepositoryCustom
        //, QuerydslPredicateExecutor<Aktivitaet>
{
    List<Aktivitaet> findByOwnerIn(Set<String> usernameFilter);

    List<Aktivitaet> findByOwner(String owner, Pageable pageable);

    long countByOwner(String owner);
}
