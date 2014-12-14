package akeefer.service;

import akeefer.model.Aktivitaet;
import akeefer.model.BenachrichtigunsIntervall;
import akeefer.model.User;
import akeefer.service.dto.Statistic;
import com.google.appengine.api.datastore.Key;

import java.util.List;
import java.util.Set;

public interface PersonService {
    List<User> getAllUser();

    User getUserByUsername(String username);

    String createPersonScript(Key logedInUserId);

    Aktivitaet createAktivitaet(Aktivitaet akt, User user);

    User createUserIfAbsent(User user);

    void deleteAktivitaet(User user, Aktivitaet aktivitaet);

    List<Aktivitaet> loadAktivitaeten(Key userId);

    void changePassword(Key userId, String cleartextPassword);

    User updateUser(User user);

    User findUserById(Key userId);

    Set<Statistic> createStatistic(BenachrichtigunsIntervall interval);

    void sendStatisticMail(BenachrichtigunsIntervall interval);
}
