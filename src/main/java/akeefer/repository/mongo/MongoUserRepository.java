package akeefer.repository.mongo;

import akeefer.model.mongo.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Set;

public interface MongoUserRepository extends MongoRepository<User, String>, MongoUserRepositoryCustom {

    User findByUsername(String username);

    List<User> findByUsernameIn(Set<String> usernames);
}
