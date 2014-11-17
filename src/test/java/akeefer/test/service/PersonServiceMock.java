package akeefer.test.service;

import akeefer.model.SecurityRole;
import akeefer.model.User;
import akeefer.service.PersonService;
import akeefer.test.TestScopedComponent;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;

@TestScopedComponent
public class PersonServiceMock implements PersonService {
    @Override
    public List<User> getAll() {
        return Lists.newArrayList(getUserByUsername("foo"));
    }

    @Override
    public User getUserByUsername(String username) {
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setPassword("bar");
        user.setRole(SecurityRole.USER);
        return user;
    }

    @Override
    public String createPersonScript(User user) {
        return "";
    }
}
