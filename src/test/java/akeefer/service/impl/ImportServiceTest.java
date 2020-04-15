package akeefer.service.impl;

import akeefer.model.AktivitaetsAufzeichnung;
import akeefer.model.AktivitaetsTyp;
import akeefer.model.BenachrichtigunsIntervall;
import akeefer.model.SecurityRole;
import akeefer.model.mongo.Aktivitaet;
import akeefer.model.mongo.User;
import akeefer.repository.mongo.MongoAktivitaetRepository;
import akeefer.repository.mongo.MongoUserRepository;
import akeefer.service.dto.DbBackupMongo;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.math.BigDecimal;
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

    @Autowired
    private MongoAktivitaetRepository aktivitaetRepository;

    @Value("classpath:import.json")
    private Resource importData;

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Before
    public void setUp() {
        helper.setUp();
        userRepository.deleteAll();
        aktivitaetRepository.deleteAll();
    }

    @After
    public void tearDown() {
        helper.tearDown();
        userRepository.deleteAll();
        aktivitaetRepository.deleteAll();
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

        int httpStatus = importService.importData(dbBackup);

        assertThat(httpStatus)
                .as("httpStatus")
                .isEqualTo(HttpStatus.CREATED.value());

        List<User> allUsers = userRepository.findAll();
        assertThat(allUsers)
                .hasSize(1);
        assertThat(allUsers.get(0))
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

        List<Aktivitaet> allActivities = aktivitaetRepository.findAll();
        assertThat(allActivities)
                .hasSize(2);
        assertThat(allActivities.get(0))
                .isEqualToComparingFieldByField(Aktivitaet.builder()
                        .id("agxzfmFmcmlrYS1ydW5yOwsSBlBhcmVudBiAgICAgPKICgwLEgRVc2VyGICAgICAgIAKDAsSCkFrdGl2aXRhZXQYgICAgIC5xAgM")
                        .distanzInKilometer(new BigDecimal("18.123"))
                        .typ(AktivitaetsTyp.radfahren)
                        .aktivitaetsDatum(new LocalDateTime(2014, 11, 25, 0, 0).toDate())
                        .eingabeDatum(new LocalDateTime(2014, 11, 25, 9, 53, 41, 243).toDate())
                        .updatedDatum(new LocalDateTime(2014, 11, 25, 9, 55, 41, 243).toDate())
                        .aufzeichnungsart(AktivitaetsAufzeichnung.aufgezeichnet)
                        .bezeichnung("arbeit und zurück")
                        .owner("frank")
                        .build());
    }
}