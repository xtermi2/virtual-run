package akeefer.repository.mongo;

import akeefer.model.mongo.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Set;

public interface MongoUserRepository extends MongoRepository<User, String>, MongoUserRepositoryCustom, QuerydslPredicateExecutor<User> {

    User findByUsername(String username);

    List<User> findByUsernameIn(Set<String> usernames);
}
