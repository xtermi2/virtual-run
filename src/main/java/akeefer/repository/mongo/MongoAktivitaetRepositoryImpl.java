package akeefer.repository.mongo;

import akeefer.model.mongo.Aktivitaet;
import akeefer.util.Profiling;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Slf4j
public class MongoAktivitaetRepositoryImpl implements MongoAktivitaetRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<String> findAllIds() {
        return mongoTemplate.getCollection(mongoTemplate.getCollectionName(Aktivitaet.class))
                .distinct("_id");
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
}
