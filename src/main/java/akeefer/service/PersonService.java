package akeefer.service;

import akeefer.model.AktivitaetsTyp;
import akeefer.model.BenachrichtigunsIntervall;
import akeefer.model.mongo.Aktivitaet;
import akeefer.model.mongo.User;
import akeefer.service.dto.DbBackupMongo;
import akeefer.service.dto.Statistic;
import akeefer.web.charts.ChartIntervall;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PersonService {
    List<User> getAllUser();

    User getUserByUsername(String username);

    String createPersonScript(String logedInUserId);

    Aktivitaet createAktivitaet(Aktivitaet akt, User user, boolean setDate);

    User createUserIfAbsent(User user, boolean skipPwEncoding);

    void deleteAktivitaet(User user, Aktivitaet aktivitaet);

    List<Aktivitaet> loadAktivitaeten(String userId);

    void changePassword(String userId, String cleartextPassword);

    User updateUser(User user);

    User findUserById(String userId);

    Set<Statistic> createStatistic(BenachrichtigunsIntervall interval);

    void sendStatisticMail(BenachrichtigunsIntervall interval);

    Map<AktivitaetsTyp, BigDecimal> createPieChartData(String userId, LocalDate von, LocalDate bis);

    Map<Interval, Map<AktivitaetsTyp, BigDecimal>> createStackedColumsChartData(String userId,
                                                                                ChartIntervall chartIntervall);

    Map<LocalDate, BigDecimal> createForecastData(String username, BigDecimal totalDistanceInKm);

    BigDecimal updateTotalDistance(BigDecimal totalDistanceInKm);

    BigDecimal getTotalDistance();

    DbBackupMongo createBackup(String... username);
}
