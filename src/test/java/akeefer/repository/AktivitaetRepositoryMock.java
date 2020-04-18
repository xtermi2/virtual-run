//package akeefer.repository;
//
//import akeefer.model.mongo.Aktivitaet;
//import akeefer.repository.mongo.MongoAktivitaetRepository;
//import akeefer.test.TestScopedComponent;
//import org.springframework.data.domain.Example;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//
//import java.util.List;
//import java.util.Set;
//
//@TestScopedComponent
//public class AktivitaetRepositoryMock implements MongoAktivitaetRepository {
//
//    @Override
//    public <S extends Aktivitaet> S save(S entity) {
//        return null;
//    }
//
//    @Override
//    public Aktivitaet findOne(String key) {
//        return null;
//    }
//
//    @Override
//    public boolean exists(String key) {
//        return false;
//    }
//
//    @Override
//    public List<Aktivitaet> findAll() {
//        return null;
//    }
//
//    @Override
//    public long count() {
//        return 0;
//    }
//
//    @Override
//    public void delete(String s) {
//        throw new UnsupportedOperationException("not yet implemented");
//    }
//
//    @Override
//    public void delete(Aktivitaet entity) {
//
//    }
//
//    @Override
//    public void delete(Iterable<? extends Aktivitaet> entities) {
//
//    }
//
//    @Override
//    public void deleteAll() {
//
//    }
//
//    @Override
//    public List<Aktivitaet> findAll(Sort sort) {
//        return null;
//    }
//
//    @Override
//    public <S extends Aktivitaet> S insert(S entity) {
//        throw new UnsupportedOperationException("not yet implemented");
//    }
//
//    @Override
//    public <S extends Aktivitaet> List<S> insert(Iterable<S> entities) {
//        throw new UnsupportedOperationException("not yet implemented");
//    }
//
//    @Override
//    public List<Aktivitaet> findAll(Iterable<String> keys) {
//        return null;
//    }
//
//    @Override
//    public Page<Aktivitaet> findAll(Pageable pageable) {
//        return null;
//    }
//
//    @Override
//    public <S extends Aktivitaet> S findOne(Example<S> example) {
//        return null;
//    }
//
//    @Override
//    public <S extends Aktivitaet> List<S> findAll(Example<S> example) {
//        return null;
//    }
//
//    @Override
//    public <S extends Aktivitaet> List<S> findAll(Example<S> example, Sort sort) {
//        return null;
//    }
//
//    @Override
//    public <S extends Aktivitaet> Page<S> findAll(Example<S> example, Pageable pageable) {
//        return null;
//    }
//
//    @Override
//    public <S extends Aktivitaet> long count(Example<S> example) {
//        return 0;
//    }
//
//    @Override
//    public <S extends Aktivitaet> boolean exists(Example<S> example) {
//        return false;
//    }
//
//    @Override
//    public <S extends Aktivitaet> List<S> save(Iterable<S> entities) {
//        return null;
//    }
//
//    @Override
//    public List<String> findAllIds() {
//        throw new UnsupportedOperationException("not yet implemented");
//    }
//
//    @Override
//    public List<Aktivitaet> findAllByOwner(String username) {
//        return null;
//    }
//
//    @Override
//    public void deleteAktivitaet(Aktivitaet aktivitaet) {
//
//    }
//
//    @Override
//    public List<Aktivitaet> findByOwnerIn(Set<String> usernameFilter) {
//        throw new UnsupportedOperationException("not yet implemented");
//    }
//}
