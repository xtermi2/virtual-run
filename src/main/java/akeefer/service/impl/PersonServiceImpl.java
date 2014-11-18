package akeefer.service.impl;

import akeefer.model.Aktivitaet;
import akeefer.model.User;
import akeefer.service.PersonService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PersonServiceImpl implements PersonService {

    private EntityManager em;

    @Override
    public List<User> getAll() {
        EntityManager em = EMFService.get().createEntityManager();
        Query q = em.createQuery("select u from User u");
        List<User> users = q.getResultList();
        return users;
    }

    @Override
    public User getUserByUsername(String username) {
        EntityManager em = EMFService.get().createEntityManager();
        Query q = em.createQuery("select u from User u where u.username = :username");
        q.setParameter("username", username);
        User user = (User) q.getSingleResult();
        return user;
    }

    @Override
    public String createPersonScript(User logedInUser) {
        StringBuilder personScript = new StringBuilder("var personen = [\n");
        Set<User> users = new HashSet<User>(getAll());
        users.remove(logedInUser);
        for (User user : users) {
            // TODO (ak) berechnung von KM
            personScript.append("        {id: '").append(user.getUsername())
                    .append("', distance: ").append(berechneDistanzInMeter(user)).append("},\n");
        }
        // der eingeloggte user kommt als letztes, damit falls mehrere personen an der gleichen stelle sein der
        // eingeloggte oben gerendert wird
        personScript.append("        {id: '").append(logedInUser.getUsername())
                .append("', distance: ").append(berechneDistanzInMeter(logedInUser)).append("}\n");
        personScript.append("    ];");
        return personScript.toString();
//        return "var personen = [\n" +
//                "        {id: 'andi', distance: 1500000, done: false},\n" +
//                "        {id: 'sabine', distance: 500000, done: false},\n" +
//                "//        {id: 'uli-hans', distance: 1000000, done: false},\n" +
//                "        {id: 'roland', distance: 2500000, done: false},\n" +
//                "        {id: 'norbert', distance: 2000000, done: false}\n" +
//                "    ];";
    }

    @Override
    public int berechneDistanzInMeter(User user) {
        int distanz = 0;
        //for (Aktivitaet akt : getAktivitaetenByUser(user)) {
        for (Aktivitaet akt : user.getAktivitaeten()) {
            distanz = distanz + akt.getMeter();
        }
        return distanz;
    }

    @Override
    public List<Aktivitaet> getAktivitaetenByUser(User user) {
        EntityManager em = EMFService.get().createEntityManager();
        Query q = em.createQuery("select akt from Aktivitaet akt where akt.user = :user");
        q.setParameter("user", user);
        List<Aktivitaet> aktivitaeten = q.getResultList();
        return aktivitaeten;
    }

    @Override
    public void createAktivitaet(Aktivitaet akt, User user) {
        EntityManager em = EMFService.get().createEntityManager();
        em.getTransaction().begin();
        try {
            //em.persist(user);
            em.persist(akt);
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
    }

    //@PersistenceContext
    public void setEm(EntityManager em) {
        this.em = em;
    }
}
