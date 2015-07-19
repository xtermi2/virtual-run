package akeefer.repository;

import akeefer.model.User;
import com.google.appengine.api.datastore.Key;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Key>, UserRepositoryCustom {

    User findByUsername(String username);
}
