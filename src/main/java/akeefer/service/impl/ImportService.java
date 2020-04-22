package akeefer.service.impl;

import akeefer.model.mongo.Aktivitaet;
import akeefer.model.mongo.User;
import akeefer.repository.mongo.MongoAktivitaetRepository;
import akeefer.repository.mongo.MongoUserRepository;
import akeefer.service.dto.DbBackupMongo;
import akeefer.util.Profiling;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class ImportService {

    @Autowired
    private MongoUserRepository userRepository;
    @Autowired
    private MongoAktivitaetRepository aktivitaetRepository;

    /**
     * @return HTTP status code
     */
    @Profiling
    public int importData(DbBackupMongo dbBackup) {
        int res = HttpStatus.OK.value();
        if (dbBackup != null) {
            if (CollectionUtils.isNotEmpty(dbBackup.getUsers())) {
                final List<String> existingUsernames = userRepository.findAllUsernames();
                Collection<User> unknownUsers = Collections2.filter(dbBackup.getUsers(), new Predicate<User>() {
                    @Override
                    public boolean apply(User user) {
                        return null != user && !existingUsernames.contains(user.getUsername());
                    }
                });
                if (!unknownUsers.isEmpty()) {
                    userRepository.saveAll(unknownUsers);
                    res = HttpStatus.CREATED.value();
                }
                log.info("imported {} users: {}", unknownUsers.size(), unknownUsers);
            }
            if (CollectionUtils.isNotEmpty(dbBackup.getAktivitaeten())) {
                final List<String> existingActivityIds = aktivitaetRepository.findAllIds();
                final List<String> existingUsernames = userRepository.findAllUsernames();
                Collection<Aktivitaet> unknownActivitiesOfKnownUsers = Collections2.filter(dbBackup.getAktivitaeten(), new Predicate<Aktivitaet>() {
                    @Override
                    public boolean apply(Aktivitaet aktivitaet) {
                        return null != aktivitaet
                                && existingUsernames.contains(aktivitaet.getOwner())
                                && !existingActivityIds.contains(aktivitaet.getId());
                    }
                });
                if (!unknownActivitiesOfKnownUsers.isEmpty()) {
                    List<List<Aktivitaet>> partitions = Lists.partition(new ArrayList<>(unknownActivitiesOfKnownUsers), 100);
                    for (List<Aktivitaet> partition : partitions) {
                        aktivitaetRepository.saveAll(partition);
                    }
                    res = HttpStatus.CREATED.value();
                }
                log.info("imported {} activities.", unknownActivitiesOfKnownUsers.size());
            }
        }
        return res;
    }

}
