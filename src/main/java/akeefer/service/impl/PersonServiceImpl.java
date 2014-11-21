package akeefer.service.impl;

import akeefer.model.Aktivitaet;
import akeefer.model.Parent;
import akeefer.model.User;
import akeefer.repository.AktivitaetRepository;
import akeefer.repository.ParentRepository;
import akeefer.repository.UserRepository;
import akeefer.service.PersonService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.set.ListOrderedSet;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class PersonServiceImpl implements PersonService {

    private static final Logger logger = LoggerFactory.getLogger(PersonServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AktivitaetRepository aktivitaetRepository;

    @Autowired
    private ParentRepository parentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (null != user) {
            logger.info(String.format("user (username='%s') has %s Aktivitaeten", username,
                    null == user.getAktivitaeten() ? "null" : user.getAktivitaeten().size()));
        }
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public String createPersonScript(User logedInUser) {
        Validate.notNull(logedInUser);

        StringBuilder personScript = new StringBuilder("var personen = [\n");
        Set<User> users = new ListOrderedSet();
        users.addAll(getAllUser());
        // LogedInUser enfernen und ganz ans ende haengen
        users.remove(logedInUser);
        users.add(logedInUser);
        for (User user : users) {
            personScript.append("        {id: '").append(user.getUsername())
                    .append("', distance: ").append(berechneDistanzInMeter(user)).append("},\n");
        }
        //das letzte ',' enfernen
        personScript.deleteCharAt(personScript.length() - 2);
        personScript.append("    ];");
        return personScript.toString();
    }

    @Override
    @Transactional(readOnly = true)
    public int berechneDistanzInMeter(User user) {
        int distanz = 0;
        if (null != user.getAktivitaeten()) {
            for (Aktivitaet akt : user.getAktivitaeten()) {
                if (null != akt.getDistanzInMeter()) {
                    distanz = distanz + akt.getDistanzInMeter();
                }
            }
        }
        return distanz;
    }

    @Override
    public Aktivitaet createAktivitaet(Aktivitaet akt, final User user) {
        User userTmp = userRepository.findOne(user.getId());
        if (null == akt.getId()) {
            // Relationen herstellen bei neuer Akt
            akt.setUser(userTmp);
            if (null == userTmp.getAktivitaeten()) {
                userTmp.setAktivitaeten(new ArrayList<Aktivitaet>());
            }
            userTmp.getAktivitaeten().add(akt);
        }
        akt = aktivitaetRepository.save(akt);

        user.setAktivitaeten(userTmp.getAktivitaeten());
        return akt;
    }

    @Override
    public User createUserIfAbsent(User user) {
        User userInDb = findUserByUsername(getAllUser(), user.getUsername());
        if (null != userInDb) {
            logger.info(String.format("User username='%s' already exists, i will not create a new one", user.getUsername()));
            return userInDb;
        }
        Parent parent = getParent();
        user.setParent(parent);

        return userRepository.save(user);
    }

    private Parent getParent() {
        List<Parent> parents = parentRepository.findAll();
        final Parent parent;
        if (CollectionUtils.isEmpty(parents)) {
            logger.info("Parent will be created...");
            parent = parentRepository.save(new Parent());
        } else {
            logger.info("using existing Parent from Database");
            parent = parents.get(0);
        }

        return parent;
    }

    private User findUserByUsername(Iterable<User> users, String username) {
        for (User user : users) {
            if (username.equals(user.getUsername())) {
                return user;
            }
        }
        return null;
    }

    //@PersistenceContext
//    public void setEm(EntityManager em) {
//        this.em = em;
//    }
}
