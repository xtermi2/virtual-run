package akeefer.repository;

import akeefer.model.Aktivitaet;
import akeefer.model.User;
import akeefer.test.TestScopedComponent;
import com.google.appengine.api.datastore.Key;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@TestScopedComponent
public class AktivitaetRepositoryMock implements AktivitaetRepository {
    @Override
    public List<Aktivitaet> findByUser(User user) {
        return null;
    }

    @Override
    public <S extends Aktivitaet> S save(S entity) {
        return null;
    }

    @Override
    public Aktivitaet findOne(Key key) {
        return null;
    }

    @Override
    public boolean exists(Key key) {
        return false;
    }

    @Override
    public List<Aktivitaet> findAll() {
        return null;
    }

    @Override
    public Iterable<Aktivitaet> findAll(Iterable<Key> keys) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void delete(Key key) {

    }

    @Override
    public void delete(Aktivitaet entity) {

    }

    @Override
    public void delete(Iterable<? extends Aktivitaet> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Aktivitaet> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Aktivitaet> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public void flush() {

    }

    @Override
    public Aktivitaet saveAndFlush(Aktivitaet entity) {
        return null;
    }

    @Override
    public void deleteInBatch(Iterable<Aktivitaet> entities) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public <S extends Aktivitaet> List<S> save(Iterable<S> entities) {
        return null;
    }
}
