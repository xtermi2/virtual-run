package akeefer.repository;

import akeefer.model.Aktivitaet;
import akeefer.util.Profiling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.cache.annotation.CacheRemove;
import javax.cache.annotation.CacheResult;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andreas Keefer
 */
public class AktivitaetRepositoryImpl implements AktivitaetRepositoryCustom {

    private static final Logger logger = LoggerFactory.getLogger(AktivitaetRepositoryImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private AktivitaetRepository aktivitaetRepository;

    @Override
    //@Cacheable("aktivitaeten")
    @CacheResult(cacheName = "aktivitaeten")
    @Profiling
    @Transactional(readOnly = true)
    public List<Aktivitaet> findAllByOwner(String username) {
        logger.info("findAllByOwner({}) in Database", username);
        List<Aktivitaet> gefundeneAktivitaeten = em.createQuery("select akt from Aktivitaet akt where akt.owner = :username")
                .setParameter("username", username)
                .getResultList();
        List<Aktivitaet> res = new ArrayList<>(gefundeneAktivitaeten.size());
        for (Aktivitaet akt : gefundeneAktivitaeten) {
            res.add(akt.cloneWithoutUser());
        }
        return res;
    }

    @Override
    //@CacheEvict(value = "aktivitaeten", key = "#aktivitaet.owner")
    @CacheRemove(cacheName = "aktivitaeten")
    @Transactional
    @Profiling
    public void deleteAktivitaet(Aktivitaet aktivitaet) {
        em.remove(aktivitaet);
        em.flush();
    }
}
