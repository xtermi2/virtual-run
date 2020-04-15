package akeefer.repository.mongo;

import akeefer.model.mongo.Aktivitaet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

public class MongoAktivitaetRepositoryImpl implements MongoAktivitaetRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<String> findAllIds() {
        return mongoTemplate.getCollection(mongoTemplate.getCollectionName(Aktivitaet.class))
                .distinct("_id");
    }
}
