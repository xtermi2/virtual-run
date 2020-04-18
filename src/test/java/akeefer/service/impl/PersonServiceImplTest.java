package akeefer.service.impl;

import akeefer.model.AktivitaetsAufzeichnung;
import akeefer.model.AktivitaetsTyp;
import akeefer.model.BenachrichtigunsIntervall;
import akeefer.model.SecurityRole;
import akeefer.model.mongo.Aktivitaet;
import akeefer.model.mongo.User;
import akeefer.repository.mongo.MongoUserRepository;
import akeefer.service.PersonService;
import akeefer.service.dto.Statistic;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.Sets;
import org.assertj.core.api.Assertions;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.*;

import static akeefer.test.util.ProxyUtil.getTargetObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testApplicationContext.xml"})
public class PersonServiceImplTest {

    private static String LINE_SEPARATOR = System.lineSeparator();

    @Autowired
    @Qualifier("personServiceImpl")
    private PersonService personService;

    @Autowired
    private MongoUserRepository userRepository;

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testCreatePersonScript() throws Exception {
        PersonServiceImpl impl = getTargetObject(personService, PersonServiceImpl.class);
        PersonServiceImpl spy = spy(impl);
        // Mocks
        User user1 = new User("1");
        user1.setUsername("foo");
        user1.setNickname("Foo User");
        Aktivitaet aktUser1 = new Aktivitaet();
        aktUser1.setOwner(user1.getUsername());
        aktUser1.setDistanzInMeter(4711);
        User user2 = new User("2");
        user2.setUsername("user2");
        User user3 = new User("3");
        user3.setUsername("user3");
        assertFalse("user1 is equals user2", user1.equals(user2));
        final List<User> users = Arrays.asList(user3, user1, user2);
        final List<Aktivitaet> aktivitaetList = Arrays.asList(aktUser1);
        doReturn(users).when(spy).getAllUser();
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                List<Aktivitaet> res = new ArrayList<>();
                for (Aktivitaet akt : aktivitaetList) {
                    if (invocation.getArguments()[0].equals(akt.getOwner())) {
                        res.add(akt);
                    }
                }
                return res;
            }
        }).when(spy).loadAktivitaetenByOwner(anyString());

        User logedIn = new User(user1.getId());
        logedIn.setUsername("hallo");
        Aktivitaet aktivitaet = new Aktivitaet();
        aktivitaet.setDistanzInMeter(1234);
        aktivitaet.setOwner(logedIn.getUsername());
        String personScript = spy.createPersonScript(logedIn.getId());
        System.out.println(personScript);
        assertEquals("var personen = [\n" +
                "        {id: 'user2', nickname: 'user2', distance: 0},\n" +
                "        {id: 'user3', nickname: 'user3', distance: 0},\n" +
                "        {id: 'foo', nickname: 'Foo User', distance: 4711}\n" +
                "    ];", personScript);
    }

    @Test
    public void testPasswordEncoding() throws Exception {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        printHash(encoder, "andi");
        printHash(encoder, "sabine");
        printHash(encoder, "norbert");
        printHash(encoder, "roland");
        printHash(encoder, "uli-hans");
        printHash(encoder, "frank");
    }

    @Test
    public void testCreateStatistic() throws Exception {
        PersonServiceImpl impl = getTargetObject(personService, PersonServiceImpl.class);
        PersonServiceImpl spy = spy(impl);
        // Mocks
        User user1 = new User(UUID.randomUUID());
        user1.setUsername("user1");
        user1.setBenachrichtigunsIntervall(BenachrichtigunsIntervall.taeglich);
        final List<Aktivitaet> aktivitaetList = new ArrayList<>();
        aktivitaetList.add(createDefaultAkt().owner(user1.getUsername()).distanzInKilometer(BigDecimal.ONE).typ(AktivitaetsTyp.laufen).eingabeDatum(new DateTime().toDate()).build());
        aktivitaetList.add(createDefaultAkt().owner(user1.getUsername()).distanzInKilometer(BigDecimal.valueOf(2)).typ(AktivitaetsTyp.radfahren).eingabeDatum(new DateTime().minusDays(1).toDate()).build());
        aktivitaetList.add(createDefaultAkt().owner(user1.getUsername()).distanzInKilometer(BigDecimal.valueOf(3)).typ(AktivitaetsTyp.laufen).eingabeDatum(new DateTime().minusDays(2).toDate()).build());
        aktivitaetList.add(createDefaultAkt().owner(user1.getUsername()).distanzInKilometer(BigDecimal.valueOf(4)).typ(AktivitaetsTyp.laufen).eingabeDatum(new DateTime().minusDays(7).toDate()).build());
        aktivitaetList.add(createDefaultAkt().owner(user1.getUsername()).distanzInKilometer(BigDecimal.valueOf(5)).typ(AktivitaetsTyp.laufen).eingabeDatum(new DateTime().minusDays(8).toDate()).build());

        User user2 = new User(UUID.randomUUID());
        user2.setUsername("user2");
        user2.setBenachrichtigunsIntervall(BenachrichtigunsIntervall.woechnetlich);
        aktivitaetList.add(createDefaultAkt().owner(user2.getUsername()).distanzInKilometer(BigDecimal.ONE).typ(AktivitaetsTyp.laufen).eingabeDatum(new DateTime().toDate()).build());
        aktivitaetList.add(createDefaultAkt().owner(user2.getUsername()).distanzInKilometer(BigDecimal.valueOf(2)).typ(AktivitaetsTyp.radfahren).eingabeDatum(new DateTime().minusDays(1).toDate()).build());
        aktivitaetList.add(createDefaultAkt().owner(user2.getUsername()).distanzInKilometer(BigDecimal.valueOf(3)).typ(AktivitaetsTyp.laufen).eingabeDatum(new DateTime().minusDays(2).toDate()).build());
        aktivitaetList.add(createDefaultAkt().owner(user2.getUsername()).distanzInKilometer(BigDecimal.valueOf(4)).typ(AktivitaetsTyp.laufen).eingabeDatum(new DateTime().minusDays(7).toDate()).build());
        aktivitaetList.add(createDefaultAkt().owner(user2.getUsername()).distanzInKilometer(BigDecimal.valueOf(5)).typ(AktivitaetsTyp.laufen).eingabeDatum(new DateTime().minusDays(8).toDate()).build());

        User user3 = new User(UUID.randomUUID());
        user3.setBenachrichtigunsIntervall(BenachrichtigunsIntervall.deaktiviert);
        user3.setUsername("user3");
        aktivitaetList.add(createDefaultAkt().owner(user3.getUsername()).distanzInKilometer(BigDecimal.ONE).typ(AktivitaetsTyp.laufen).eingabeDatum(new DateTime().toDate()).build());
        aktivitaetList.add(createDefaultAkt().owner(user3.getUsername()).distanzInKilometer(BigDecimal.valueOf(2)).typ(AktivitaetsTyp.radfahren).eingabeDatum(new DateTime().minusDays(1).toDate()).build());
        aktivitaetList.add(createDefaultAkt().owner(user3.getUsername()).distanzInKilometer(BigDecimal.valueOf(3)).typ(AktivitaetsTyp.laufen).eingabeDatum(new DateTime().minusDays(2).toDate()).build());
        aktivitaetList.add(createDefaultAkt().owner(user3.getUsername()).distanzInKilometer(BigDecimal.valueOf(4)).typ(AktivitaetsTyp.laufen).eingabeDatum(new DateTime().minusDays(7).toDate()).build());
        aktivitaetList.add(createDefaultAkt().owner(user3.getUsername()).distanzInKilometer(BigDecimal.valueOf(5)).typ(AktivitaetsTyp.laufen).eingabeDatum(new DateTime().minusDays(8).toDate()).build());


        final List<User> users = Arrays.asList(user3, user1, user2);
        doReturn(users).when(spy).getAllUser();
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                List<Aktivitaet> res = new ArrayList<>();
                for (Aktivitaet akt : aktivitaetList) {
                    if (invocation.getArguments()[0].equals(akt.getOwner())) {
                        res.add(akt);
                    }
                }
                return res;
            }
        }).when(spy).loadAktivitaetenByOwner(anyString());

        Set<Statistic> statistics = spy.createStatistic(BenachrichtigunsIntervall.deaktiviert);
        assertEquals(3, statistics.size());
        for (Statistic statistic : statistics) {
            assertEquals(0, statistic.getAggregated().size());
        }

        statistics = spy.createStatistic(BenachrichtigunsIntervall.taeglich);
        assertEquals(3, statistics.size());
        Statistic statistic = statistics.iterator().next();
        assertEquals(1, statistic.getAggregated().size());
        Map.Entry<AktivitaetsTyp, BigDecimal> entry = statistic.getAggregated().entrySet().iterator().next();
        assertEquals(AktivitaetsTyp.radfahren, entry.getKey());
        assertEquals(new BigDecimal("2"), entry.getValue());

        statistics = spy.createStatistic(BenachrichtigunsIntervall.woechnetlich);
        assertEquals(3, statistics.size());
        statistic = statistics.iterator().next();
        assertEquals(2, statistic.getAggregated().size());
        assertEquals(new BigDecimal("2"), statistic.getAggregated().get(AktivitaetsTyp.radfahren));
        assertEquals(new BigDecimal("7"), statistic.getAggregated().get(AktivitaetsTyp.laufen));
    }

    private Aktivitaet.AktivitaetBuilder createDefaultAkt() {
        return Aktivitaet.builder()
                .aufzeichnungsart(AktivitaetsAufzeichnung.aufgezeichnet);
    }

    @Test
    public void testBuildMailBodyNurEigeneAktivitaeten() throws Exception {
        PersonServiceImpl impl = getTargetObject(personService, PersonServiceImpl.class);
        // Mocks
        User user1 = new User(UUID.randomUUID());
        user1.setUsername("user1");
        user1.setBenachrichtigunsIntervall(BenachrichtigunsIntervall.taeglich);

        String mailBody = impl.buildMailBody(Sets.newHashSet(new Statistic(user1).add(AktivitaetsTyp.wandern, BigDecimal.TEN)), user1, BenachrichtigunsIntervall.taeglich);
        assertEquals("Hallo user1," + LINE_SEPARATOR +
                        LINE_SEPARATOR +
                        "gestern ist nichts passiert. " + LINE_SEPARATOR + LINE_SEPARATOR +
                        "http://localhost:8080"
                , mailBody);
    }

    @Test
    public void testBuildMailBodyTaeglich() throws Exception {
        PersonServiceImpl impl = getTargetObject(personService, PersonServiceImpl.class);
        // Mocks
        User user1 = new User(UUID.randomUUID());
        user1.setUsername("user1");
        user1.setBenachrichtigunsIntervall(BenachrichtigunsIntervall.taeglich);

        User user2 = new User(UUID.randomUUID());
        user2.setUsername("user2");
        user2.setBenachrichtigunsIntervall(BenachrichtigunsIntervall.taeglich);

        String mailBody = impl.buildMailBody(Sets.newHashSet(new Statistic(user2).add(AktivitaetsTyp.wandern, BigDecimal.TEN)), user1, BenachrichtigunsIntervall.taeglich);
        assertEquals("Hallo user1," + LINE_SEPARATOR +
                        LINE_SEPARATOR +
                        "gestern ist folgendes passiert: " + LINE_SEPARATOR +
                        LINE_SEPARATOR +
                        "user2 ist ..." + LINE_SEPARATOR +
                        "... 10km gewandert" + LINE_SEPARATOR + LINE_SEPARATOR + LINE_SEPARATOR +
                        "http://localhost:8080",
                mailBody);
    }

    @Test
    public void testBuildMailBodyIncludeMe() throws Exception {
        PersonServiceImpl impl = getTargetObject(personService, PersonServiceImpl.class);
        // Mocks
        User user1 = new User(UUID.randomUUID());
        user1.setUsername("user1");
        user1.setBenachrichtigunsIntervall(BenachrichtigunsIntervall.taeglich);

        String mailBody = impl.buildMailBody(Sets.newHashSet(new Statistic(user1).add(AktivitaetsTyp.wandern, BigDecimal.TEN)), user1, BenachrichtigunsIntervall.taeglich);
        assertEquals("Hallo user1," + LINE_SEPARATOR +
                        LINE_SEPARATOR +
                        "gestern ist nichts passiert. " +
                        LINE_SEPARATOR + LINE_SEPARATOR +
                        "http://localhost:8080",
                mailBody);

        // user 1 wanna see his own Activities.
        user1.setIncludeMeInStatisticMail(true);

        mailBody = impl.buildMailBody(Sets.newHashSet(new Statistic(user1).add(AktivitaetsTyp.wandern, BigDecimal.TEN)), user1, BenachrichtigunsIntervall.taeglich);
        assertEquals("Hallo user1," + LINE_SEPARATOR +
                        LINE_SEPARATOR +
                        "gestern ist folgendes passiert: " + LINE_SEPARATOR +
                        LINE_SEPARATOR +
                        "user1 ist ..." + LINE_SEPARATOR +
                        "... 10km gewandert" + LINE_SEPARATOR + LINE_SEPARATOR + LINE_SEPARATOR +
                        "http://localhost:8080",
                mailBody);
    }

    @Test
    public void testBuildMailBodyWoechentlich() throws Exception {
        PersonServiceImpl impl = getTargetObject(personService, PersonServiceImpl.class);
        // Mocks
        User user1 = new User(UUID.randomUUID());
        user1.setUsername("user1");
        user1.setBenachrichtigunsIntervall(BenachrichtigunsIntervall.woechnetlich);

        User user2 = new User(UUID.randomUUID());
        user2.setUsername("user2");
        user2.setBenachrichtigunsIntervall(BenachrichtigunsIntervall.taeglich);

        String mailBody = impl.buildMailBody(Sets.newHashSet(new Statistic(user2).add(AktivitaetsTyp.wandern, BigDecimal.TEN)), user1, BenachrichtigunsIntervall.woechnetlich);
        assertEquals("Hallo user1," + LINE_SEPARATOR +
                        LINE_SEPARATOR +
                        "letzte Woche ist folgendes passiert: " + LINE_SEPARATOR +
                        LINE_SEPARATOR +
                        "user2 ist ..." + LINE_SEPARATOR +
                        "... 10km gewandert" + LINE_SEPARATOR + LINE_SEPARATOR + LINE_SEPARATOR +
                        "http://localhost:8080",
                mailBody);
    }

    @Test
    public void testMongo() {
        userRepository.deleteAll();
        List<akeefer.model.mongo.User> res = userRepository.findAll();
        Assertions.assertThat(res)
                .as("all Users")
                .isEmpty();

        String id = String.valueOf(5099850341285888L);
        akeefer.model.mongo.User andi = userRepository.save(akeefer.model.mongo.User.builder()
                .id(id)
                .username("andi")
                .password("andi")
                .role(SecurityRole.ADMIN)
                .role(SecurityRole.USER)
                .build());
        Assertions.assertThat(andi.getId())
                .as("id")
                .isNotNull();

        akeefer.model.mongo.User one = userRepository.findOne(id);
        Assertions.assertThat(one.getId())
                .as("id")
                .isEqualTo(id);

        akeefer.model.mongo.User findByUsername = userRepository.findByUsername("andi");
        Assertions.assertThat(findByUsername.getId())
                .as("id")
                .isEqualTo(id);
    }

    private void printHash(PasswordEncoder encoder, String user) {
        System.out.println(user + ": " + encoder.encode(user));
    }
}