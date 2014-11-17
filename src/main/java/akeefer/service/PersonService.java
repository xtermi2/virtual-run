package akeefer.service;

import akeefer.model.User;

import java.util.List;

/**
 * Created by akeefer on 17.11.14.
 */
public interface PersonService {
    List<User> getAll();

    User getUserByUsername(String username);

    String createPersonScript(User user);
}
