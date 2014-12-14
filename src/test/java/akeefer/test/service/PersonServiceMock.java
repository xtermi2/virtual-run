package akeefer.test.service;

import akeefer.model.Aktivitaet;
import akeefer.model.BenachrichtigunsIntervall;
import akeefer.model.SecurityRole;
import akeefer.model.User;
import akeefer.service.PersonService;
import akeefer.service.dto.Statistic;
import akeefer.test.TestScopedComponent;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@TestScopedComponent
public class PersonServiceMock implements PersonService {

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public List<User> getAllUser() {
        return Lists.newArrayList(getUserByUsername("foo"));
    }

    @Override
    public User getUserByUsername(String username) {
        User user = new User();
        user.setId(KeyFactory.createKey("User", "username"));
        user.setUsername(username);
        user.setPassword(encoder.encode("bar"));
        user.addRole(SecurityRole.USER);
        return user;
    }

    @Override
    public String createPersonScript(Key logedInUserId) {
        return "";
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

    @Override
    public List<Aktivitaet> loadAktivitaeten(Key userId) {
        return null;
    }

    @Override
    public void changePassword(Key userId, String cleartextPassword) {
    }

    @Override
    public User updateUser(User user) {
        return user;
    }

    @Override
    public User findUserById(Key userId) {
        return getUserByUsername("foo");
    }

    @Override
    public Set<Statistic> createStatistic(BenachrichtigunsIntervall interval) {
        return Collections.emptySet();
    }

    @Override
    public void sendStatisticMail(BenachrichtigunsIntervall interval) {
    }
}
