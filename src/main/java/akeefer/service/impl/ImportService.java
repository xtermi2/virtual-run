package akeefer.service.impl;

import akeefer.model.mongo.User;
import akeefer.repository.mongo.MongoUserRepository;
import akeefer.service.dto.DbBackupMongo;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@Transactional
@Slf4j
public class ImportService {

    @Autowired
    private MongoUserRepository userRepository;

    /**
     * @return HTTP status code
     */
    public int importData(DbBackupMongo dbBackup) {
        int res = HttpStatus.OK.value();
        if (dbBackup != null) {
            if (CollectionUtils.isNotEmpty(dbBackup.getUsers())) {
                final Collection<String> existingUsernames = Collections2.transform(userRepository.findAll(), new Function<User, String>() {
                    @Override
                    public String apply(User input) {
                        return input.getUsername();
                    }
                });
                Collection<User> unknownUsers = Collections2.filter(dbBackup.getUsers(), new Predicate<User>() {
                    @Override
                    public boolean apply(@NullableDecl User user) {
                        if (null != user && !existingUsernames.contains(user.getUsername())) {
                            return true;
                        }
                        return false;
                    }
                });
                if (!unknownUsers.isEmpty()) {
                    userRepository.save(unknownUsers);
                    res = HttpStatus.CREATED.value();
                }
                log.info("imported users: {}", unknownUsers);
            }
        }
        return res;
    }

}
