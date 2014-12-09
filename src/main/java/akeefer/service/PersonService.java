package akeefer.service;

import akeefer.model.Aktivitaet;
import akeefer.model.User;
import com.google.appengine.api.datastore.Key;

import java.util.List;

public interface PersonService {
    List<User> getAllUser();

    User getUserByUsername(String username);

    String createPersonScript(Key logedInUserId);

    Aktivitaet createAktivitaet(Aktivitaet akt, User user);

    User createUserIfAbsent(User user);

    void deleteAktivitaet(User user, Aktivitaet aktivitaet);

    List<Aktivitaet> loadAktivitaeten(Key userId);

    void changePassword(Key userId, String cleartextPassword);
}
