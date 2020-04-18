//package akeefer.repository;
//
//import akeefer.model.mongo.User;
//import akeefer.repository.mongo.MongoUserRepository;
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
//public class UserRepositoryMock implements MongoUserRepository {
//
//
//    @Override
//    public void delete(User entity) {
//
//    }
//
//    @Override
//    public void delete(Iterable<? extends User> entities) {
//
//    }
//
//    @Override
//    public void deleteAll() {
//
//    }
//
//    @Override
//    public List<User> findAll() {
//        return null;
//    }
//
//    @Override
//    public long count() {
//        return 0;
//    }
//
//    @Override
//    public void delete(String key) {
//
//    }
//
//    @Override
//    public List<User> findAll(Sort sort) {
//        return null;
//    }
//
//    @Override
//    public <S extends User> S insert(S entity) {
//        throw new UnsupportedOperationException("not yet implemented");
//    }
//
//    @Override
//    public <S extends User> List<S> insert(Iterable<S> entities) {
//        throw new UnsupportedOperationException("not yet implemented");
//    }
//
//    @Override
//    public List<User> findAll(Iterable<String> keys) {
//        return null;
//    }
//
//    @Override
//    public Page<User> findAll(Pageable pageable) {
//        return null;
//    }
//
//    @Override
//    public <S extends User> S findOne(Example<S> example) {
//        return null;
//    }
//
//    @Override
//    public <S extends User> List<S> findAll(Example<S> example) {
//        return null;
//    }
//
//    @Override
//    public <S extends User> List<S> findAll(Example<S> example, Sort sort) {
//        return null;
//    }
//
//    @Override
//    public <S extends User> Page<S> findAll(Example<S> example, Pageable pageable) {
//        return null;
//    }
//
//    @Override
//    public <S extends User> long count(Example<S> example) {
//        return 0;
//    }
//
//    @Override
//    public <S extends User> boolean exists(Example<S> example) {
//        return false;
//    }
//
//    @Override
//    public <S extends User> S save(S entity) {
//        return null;
//    }
//
//    @Override
//    public <S extends User> List<S> save(Iterable<S> entities) {
//        return null;
//    }
//
//    @Override
//    public User findOne(String key) {
//        return null;
//    }
//
//    @Override
//    public boolean exists(String key) {
//        return false;
//    }
//
//    @Override
//    public User findByUsername(String username) {
//        return null;
//    }
//
//    @Override
//    public List<User> findByUsernameIn(Set<String> usernames) {
//        throw new UnsupportedOperationException("not yet implemented");
//    }
//
//    @Override
//    public List<String> findAllUsernames() {
//        throw new UnsupportedOperationException("not yet implemented");
//    }
//}
