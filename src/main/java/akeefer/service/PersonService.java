package akeefer.service;

import akeefer.model.Aktivitaet;
import akeefer.model.User;

import java.util.List;

/**
 * Created by akeefer on 17.11.14.
 */
public interface PersonService {
    List<User> getAllUser();

    User getUserByUsername(String username);

    String createPersonScript(User user);

    int berechneDistanzInMeter(User user);

    Aktivitaet createAktivitaet(Aktivitaet akt, User user);

    User createUserIfAbsent(User user);
}
