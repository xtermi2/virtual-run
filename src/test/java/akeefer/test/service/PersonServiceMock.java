package akeefer.test.service;

import akeefer.model.Aktivitaet;
import akeefer.model.SecurityRole;
import akeefer.model.User;
import akeefer.service.PersonService;
import akeefer.test.TestScopedComponent;
import com.google.common.collect.Lists;

import java.util.List;

@TestScopedComponent
public class PersonServiceMock implements PersonService {
    @Override
    public List<User> getAllUser() {
        return Lists.newArrayList(getUserByUsername("foo"));
    }

    @Override
    public User getUserByUsername(String username) {
        User user = new User();
        user.setId(null);
        user.setUsername(username);
        user.setPassword("bar");
        user.setRole(SecurityRole.USER);
        return user;
    }

    @Override
    public String createPersonScript(User user) {
        return "";
    }

    @Override
    public int berechneDistanzInMeter(User user) {
        return 4711;
    }

    @Override
    public Aktivitaet createAktivitaet(Aktivitaet akt, User user) {
        return akt;
    }

    @Override
    public User createUserIfAbsent(User user) {
        user.setId(null);
        return user;
    }

    @Override
    public void deleteAktivitaet(User user, Aktivitaet aktivitaet) {
        user.getAktivitaeten().remove(aktivitaet);
    }
}
