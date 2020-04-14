package akeefer.service.impl;

import akeefer.model.BenachrichtigunsIntervall;
import akeefer.model.SecurityRole;
import akeefer.model.mongo.User;
import akeefer.repository.mongo.MongoUserRepository;
import akeefer.service.dto.DbBackupMongo;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.List;

import static akeefer.service.rest.StatisticRestService.OBJECT_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testApplicationContext.xml"})
@Slf4j
public class ImportServiceTest {

    @Autowired
    private ImportService importService;

    @Autowired
    private MongoUserRepository userRepository;

    @Value("classpath:import.json")
    private Resource importData;

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Before
    public void setUp() {
        helper.setUp();
        userRepository.deleteAll();
    }

    @After
    public void tearDown() {
        helper.tearDown();
        userRepository.deleteAll();
    }

    @Test
    public void serializeDeserializeKey() throws IOException {
        Key inputKey = KeyFactory.createKey("User", 5099850341285888L);

        String keyString = OBJECT_MAPPER.writeValueAsString(inputKey);

        Key outputKey = OBJECT_MAPPER.readValue(keyString, Key.class);

        assertThat(outputKey)
                .isEqualTo(inputKey);
        assertThat(keyString)
                .isEqualTo("\"agR0ZXN0chELEgRVc2VyGICAgICXyYcJDA\"");
    }

    @Test
    public void importData() throws IOException {
        DbBackupMongo dbBackup = OBJECT_MAPPER.readValue(importData.getFile(), DbBackupMongo.class);
        log.info("{}", dbBackup);
        assertThat(dbBackup.getUsers().get(0).getId())
                .as("id after json parsing")
                .isEqualTo("agxzfmFmcmlrYS1ydW5yJAsSBlBhcmVudBiAgICAgPKICgwLEgRVc2VyGICAgICXyYcJDA");

        importService.importData(dbBackup);
        List<User> all = userRepository.findAll();

        assertThat(all)
                .hasSize(1);
        assertThat(all.get(0))
                .isEqualToComparingFieldByField(User.builder()
                        .id("agxzfmFmcmlrYS1ydW5yJAsSBlBhcmVudBiAgICAgPKICgwLEgRVc2VyGICAgICXyYcJDA")
                        .username("frank")
                        .password("secret")
                        .nickname("Frank")
                        .email("frank@foo.bar")
                        .role(SecurityRole.USER)
                        .benachrichtigunsIntervall(BenachrichtigunsIntervall.woechnetlich)
                        .includeMeInStatisticMail(true)
                        .build());
    }
}