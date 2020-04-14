package akeefer.service.impl;

import akeefer.repository.mongo.MongoUserRepository;
import akeefer.service.dto.DbBackupMongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ImportService {

    @Autowired
    private MongoUserRepository userRepository;

    public void importData(DbBackupMongo dbBackup) {
        userRepository.save(dbBackup.getUsers());
    }

}
