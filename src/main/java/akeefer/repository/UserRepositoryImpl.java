package akeefer.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class UserRepositoryImpl implements UserRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

//    @Override
//    @Profiling
//    public List<UserMapView> findAllUserAsMapView() {
//        List<Object[]> queryRes = em.createQuery("select user.id, user.username, user.nickname, user.gesamtDistanzInKilometer from User user")
//                .getResultList();
//
//        List<UserMapView> res = new ArrayList<>(queryRes.size());
//        for (Object[] row : queryRes) {
//            res.add(new UserMapView((Key) row[0], (String) row[1], (String) row[2], (BigDecimal) row[3]));
//        }
//
//        return res;
//    }

}
