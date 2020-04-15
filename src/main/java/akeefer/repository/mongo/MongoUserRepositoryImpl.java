package akeefer.repository.mongo;

import akeefer.model.mongo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

@Slf4j
public class MongoUserRepositoryImpl implements MongoUserRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<String> findAllUsernames() {
        return mongoTemplate.getCollection(mongoTemplate.getCollectionName(User.class))
                .distinct("username");
    }
}
