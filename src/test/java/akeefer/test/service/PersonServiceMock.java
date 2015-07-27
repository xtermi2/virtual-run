package akeefer.test.service;

import akeefer.model.*;
import akeefer.service.PersonService;
import akeefer.service.dto.Statistic;
import akeefer.test.TestScopedComponent;
import akeefer.web.charts.ChartIntervall;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.common.collect.Lists;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    @Override
    public Map<AktivitaetsTyp, BigDecimal> createPieChartData(Key userId, LocalDate von, LocalDate bis) {
        return Collections.emptyMap();
    }

    @Override
    public Map<Interval, Map<AktivitaetsTyp, BigDecimal>> createStackedColumsChartData(Key userId, ChartIntervall chartIntervall) {
        return Collections.emptyMap();
    }

    @Override
    public Map<LocalDate, BigDecimal> createForecastData(String username, BigDecimal totalDistanceInKm) {
        return Collections.emptyMap();
    }
}
