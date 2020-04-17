package akeefer.repository.mongo;

import akeefer.model.mongo.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoUserRepository extends MongoRepository<User, String>, MongoUserRepositoryCustom {

    User findByUsername(String username);
}