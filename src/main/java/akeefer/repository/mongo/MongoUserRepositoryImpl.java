package akeefer.repository.mongo;

import akeefer.model.mongo.User;
import akeefer.util.Profiling;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
public class MongoUserRepositoryImpl implements MongoUserRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    @Profiling
    public List<String> findAllUsernames() {
        return StreamSupport.stream(mongoTemplate.getCollection(mongoTemplate.getCollectionName(User.class))
                .distinct("username", String.class).spliterator(), false)
                .collect(Collectors.toList());
    }
}
