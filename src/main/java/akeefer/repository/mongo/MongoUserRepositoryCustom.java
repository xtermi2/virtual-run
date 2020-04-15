package akeefer.repository.mongo;

import java.util.List;

public interface MongoUserRepositoryCustom {

    List<String> findAllUsernames();
}
