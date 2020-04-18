package akeefer.test.service;

import akeefer.model.AktivitaetsTyp;
import akeefer.model.BenachrichtigunsIntervall;
import akeefer.model.SecurityRole;
import akeefer.model.mongo.Aktivitaet;
import akeefer.model.mongo.User;
import akeefer.service.PersonService;
import akeefer.service.dto.DbBackupMongo;
import akeefer.service.dto.Statistic;
import akeefer.test.TestScopedComponent;
import akeefer.web.charts.ChartIntervall;
import com.google.common.collect.Lists;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.*;

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
        return User.builder()
                .id(UUID.randomUUID().toString())
                .username(username)
                .password(encoder.encode("bar"))
                .role(SecurityRole.USER)
                .build();
    }

    @Override
    public String createPersonScript(String logedInUserId) {
        return "";
    }

    @Override
    public Aktivitaet createAktivitaet(Aktivitaet akt, User user, boolean setDate) {
        return akt;
    }

    @Override
    public User createUserIfAbsent(User user, boolean skipPwEncoding) {
        user.setId(null);
        return user;
    }

    @Override
    public void deleteAktivitaet(User user, Aktivitaet aktivitaet) {

    }

    @Override
    public List<Aktivitaet> loadAktivitaeten(String userId) {
        return null;
    }

    @Override
    public void changePassword(String userId, String cleartextPassword) {
    }

    @Override
    public User updateUser(User user) {
        return user;
    }

    @Override
    public User findUserById(String userId) {
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
    public Map<AktivitaetsTyp, BigDecimal> createPieChartData(String userId, LocalDate von, LocalDate bis) {
        return Collections.emptyMap();
    }

    @Override
    public Map<Interval, Map<AktivitaetsTyp, BigDecimal>> createStackedColumsChartData(String userId, ChartIntervall chartIntervall) {
        return Collections.emptyMap();
    }

    @Override
    public Map<LocalDate, BigDecimal> createForecastData(String username, BigDecimal totalDistanceInKm) {
        return Collections.emptyMap();
    }

    @Override
    public BigDecimal updateTotalDistance(BigDecimal totalDistanceInKm) {
        return totalDistanceInKm;
    }

    @Override
    public BigDecimal getTotalDistance() {
        return BigDecimal.TEN;
    }

    @Override
    public DbBackupMongo createBackup(String... username) {
        return DbBackupMongo.builder().build();
    }
}
