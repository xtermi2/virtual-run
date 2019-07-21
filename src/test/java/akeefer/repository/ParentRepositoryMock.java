package akeefer.repository;

import akeefer.model.Parent;
import akeefer.test.TestScopedComponent;
import com.google.appengine.api.datastore.Key;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@TestScopedComponent
public class ParentRepositoryMock implements ParentRepository {
    @Override
    public <S extends Parent> S save(S entity) {
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
    public void delete(Parent entity) {

    }

    @Override
    public void delete(Iterable<? extends Parent> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Parent> findAll() {
        return null;
    }

    @Override
    public List<Parent> findAll(Sort sort) {
        return null;
    }

    @Override
    public List<Parent> findAll(Iterable<Key> keys) {
        return null;
    }

    @Override
    public Page<Parent> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public void flush() {

    }

    @Override
    public Parent saveAndFlush(Parent entity) {
        return null;
    }

    @Override
    public void deleteInBatch(Iterable<Parent> entities) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Parent getOne(Key key) {
        return null;
    }

    @Override
    public <S extends Parent> S findOne(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Parent> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Parent> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Parent> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Parent> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Parent> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Parent> List<S> save(Iterable<S> entities) {
        return null;
    }

    @Override
    public Parent findOne(Key key) {
        return null;
    }

    @Override
    public boolean exists(Key key) {
        return false;
    }
}
