package akeefer.service;

import akeefer.model.Aktivitaet;
import akeefer.model.User;

import java.util.List;

/**
 * Created by akeefer on 17.11.14.
 */
public interface PersonService {
    List<User> getAll();

    User getUserByUsername(String username);

    String createPersonScript(User user);

    int berechneDistanzInMeter(User user);

    List<Aktivitaet> getAktivitaetenByUser(User user);

    void createAktivitaet(Aktivitaet akt, User user);
}
