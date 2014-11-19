package akeefer.service.impl;

import akeefer.model.Aktivitaet;
import akeefer.model.User;
import akeefer.service.PersonService;
import org.apache.commons.collections.set.ListOrderedSet;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Set;

@Service
public class PersonServiceImpl implements PersonService {

    private EntityManager em = EMFService.get().createEntityManager();

    @Override
    public List<User> getAllUser() {
        //EntityManager em = EMFService.get().createEntityManager();
        Query q = em.createQuery("select u from User u");
        List<User> users = q.getResultList();
        //em.close();
        return users;
    }

    @Override
    public User getUserByUsername(String username) {
        //EntityManager em = EMFService.get().createEntityManager();
        Query q = em.createQuery("select u from User u where u.username = :username");
        q.setParameter("username", username);
        User user = (User) q.getSingleResult();
        //em.close();
        return user;
    }

    @Override
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
    public int berechneDistanzInMeter(User user) {
        int distanz = 0;
        //for (Aktivitaet akt : getAktivitaetenByUser(user)) {
        for (Aktivitaet akt : user.getAktivitaeten()) {
            if (null != akt.getMeter()) {
                distanz = distanz + akt.getMeter();
            }
        }
        return distanz;
    }

    @Override
    public List<Aktivitaet> getAktivitaetenByUser(User user) {
        //EntityManager em = EMFService.get().createEntityManager();
        Query q = em.createQuery("select akt from Aktivitaet akt where akt.user = :user");
        q.setParameter("user", user);
        List<Aktivitaet> aktivitaeten = q.getResultList();
        //em.close();
        return aktivitaeten;
    }

    @Override
    public void createAktivitaet(Aktivitaet akt, User user) {
        if (null == akt.getId()) {
            // Relationen herstellen bei neuer Akt
            akt.setUser(user);
            user.getAktivitaeten().add(akt);
        }

        //EntityManager em = EMFService.get().createEntityManager();
        em.getTransaction().begin();
        try {
            if (null == akt.getId()) {
                // save new Akt
                em.persist(akt);
            } else {
                // update existing Akt
                em.merge(akt);
            }
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            //em.close();
        }
    }

    @Override
    public User createUserIfAbsent(User user) {

        User userInDb = findUserByUsername(getAllUser(), user.getUsername());
        if (null != userInDb) {
            return userInDb;
        }

        //EntityManager em = EMFService.get().createEntityManager();
        em.getTransaction().begin();
        try {
            em.persist(user);
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            //em.close();
        }

        return user;
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
