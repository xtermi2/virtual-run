package akeefer.service.impl;

import akeefer.model.Aktivitaet;
import akeefer.model.AktivitaetsTyp;
import akeefer.model.BenachrichtigunsIntervall;
import akeefer.model.User;
import akeefer.service.PersonService;
import akeefer.service.dto.Statistic;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.Sets;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static akeefer.test.util.ProxyUtil.getTargetObject;
import static com.google.appengine.api.datastore.KeyFactory.createKey;
import static org.apache.commons.lang3.SystemUtils.LINE_SEPARATOR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testApplicationContext.xml"})
public class PersonServiceImplTest {

    @Autowired
    @Qualifier("personServiceImpl")
    private PersonService personService;

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
        PersonService spy = spy(impl);
        // Mocks
        User user1 = new User(createKey("User", "user1"));
        user1.setUsername("foo");
        user1.setNickname("Foo User");
        Aktivitaet aktUser1 = new Aktivitaet();
        aktUser1.setDistanzInMeter(4711);
        user1.setAktivitaeten(Arrays.asList(aktUser1));
        //user1 = userRepository.save(user1);
        User user2 = new User(createKey("User", "user2"));
        user2.setUsername("user2");
        //user2 = userRepository.save(user2);
        User user3 = new User(createKey("User", "user3"));
        user3.setUsername("user3");
        //user3 = userRepository.save(user3);
        assertFalse("user1 is equals user2", user1.equals(user2));
        List<User> users = Arrays.asList(user3, user1, user2);
        doReturn(users).when(spy).getAllUser();

        User logedIn = new User(user1.getId());
        logedIn.setUsername("hallo");
        Aktivitaet aktivitaet = new Aktivitaet();
        aktivitaet.setDistanzInMeter(1234);
        logedIn.getAktivitaeten().add(aktivitaet);
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
    }

    @Test
    public void testCreateStatistic() throws Exception {
        PersonServiceImpl impl = getTargetObject(personService, PersonServiceImpl.class);
        PersonService spy = spy(impl);
        // Mocks
        User user1 = new User(createKey("User", "user1"));
        user1.setUsername("user1");
        user1.setBenachrichtigunsIntervall(BenachrichtigunsIntervall.taeglich);
        user1.setAktivitaeten(Arrays.asList(
                Aktivitaet.newBuilder().distanzInKilometer(BigDecimal.ONE).typ(AktivitaetsTyp.laufen).eingabeDatum(new DateTime().toDate()).build(),
                Aktivitaet.newBuilder().distanzInKilometer(BigDecimal.valueOf(2)).typ(AktivitaetsTyp.radfahren).eingabeDatum(new DateTime().minusDays(1).toDate()).build(),
                Aktivitaet.newBuilder().distanzInKilometer(BigDecimal.valueOf(3)).typ(AktivitaetsTyp.laufen).eingabeDatum(new DateTime().minusDays(2).toDate()).build(),
                Aktivitaet.newBuilder().distanzInKilometer(BigDecimal.valueOf(4)).typ(AktivitaetsTyp.laufen).eingabeDatum(new DateTime().minusDays(7).toDate()).build(),
                Aktivitaet.newBuilder().distanzInKilometer(BigDecimal.valueOf(5)).typ(AktivitaetsTyp.laufen).eingabeDatum(new DateTime().minusDays(8).toDate()).build()
        ));

        User user2 = new User(createKey("User", "user2"));
        user2.setBenachrichtigunsIntervall(BenachrichtigunsIntervall.woechnetlich);
        user2.setAktivitaeten(user1.getAktivitaeten());
        user2.setUsername("user2");

        User user3 = new User(createKey("User", "user3"));
        user3.setBenachrichtigunsIntervall(BenachrichtigunsIntervall.deaktiviert);
        user3.setAktivitaeten(user1.getAktivitaeten());
        user3.setUsername("user3");

        List<User> users = Arrays.asList(user3, user1, user2);
        doReturn(users).when(spy).getAllUser();

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

    @Test
    public void testBuildMailBodyNurEigeneAktivitaeten() throws Exception {
        PersonServiceImpl impl = getTargetObject(personService, PersonServiceImpl.class);
        // Mocks
        User user1 = new User(createKey("User", "user1"));
        user1.setUsername("user1");
        user1.setBenachrichtigunsIntervall(BenachrichtigunsIntervall.taeglich);

        String mailBody = impl.buildMailBody(Sets.newHashSet(new Statistic(user1).add(AktivitaetsTyp.wandern, BigDecimal.TEN)), user1, BenachrichtigunsIntervall.taeglich);
        assertEquals("Hallo user1," + LINE_SEPARATOR +
                LINE_SEPARATOR +
                "gestern ist nichts pasiert. ", mailBody);
    }

    @Test
    public void testBuildMailBodyTaeglich() throws Exception {
        PersonServiceImpl impl = getTargetObject(personService, PersonServiceImpl.class);
        // Mocks
        User user1 = new User(createKey("User", "user1"));
        user1.setUsername("user1");
        user1.setBenachrichtigunsIntervall(BenachrichtigunsIntervall.taeglich);

        User user2 = new User(createKey("User", "user2"));
        user2.setUsername("user2");
        user2.setBenachrichtigunsIntervall(BenachrichtigunsIntervall.taeglich);

        String mailBody = impl.buildMailBody(Sets.newHashSet(new Statistic(user2).add(AktivitaetsTyp.wandern, BigDecimal.TEN)), user1, BenachrichtigunsIntervall.taeglich);
        assertEquals("Hallo user1," + LINE_SEPARATOR +
                        LINE_SEPARATOR +
                        "gestern ist folgendes passiert: " + LINE_SEPARATOR +
                        LINE_SEPARATOR +
                        "user2 ist ..." + LINE_SEPARATOR +
                        "... 10km gewandert" + LINE_SEPARATOR,
                mailBody);
    }

    @Test
    public void testBuildMailBodyWoechentlich() throws Exception {
        PersonServiceImpl impl = getTargetObject(personService, PersonServiceImpl.class);
        // Mocks
        User user1 = new User(createKey("User", "user1"));
        user1.setUsername("user1");
        user1.setBenachrichtigunsIntervall(BenachrichtigunsIntervall.woechnetlich);

        User user2 = new User(createKey("User", "user2"));
        user2.setUsername("user2");
        user2.setBenachrichtigunsIntervall(BenachrichtigunsIntervall.taeglich);

        String mailBody = impl.buildMailBody(Sets.newHashSet(new Statistic(user2).add(AktivitaetsTyp.wandern, BigDecimal.TEN)), user1, BenachrichtigunsIntervall.woechnetlich);
        assertEquals("Hallo user1," + LINE_SEPARATOR +
                        LINE_SEPARATOR +
                        "letzte Woche ist folgendes passiert: " + LINE_SEPARATOR +
                        LINE_SEPARATOR +
                        "user2 ist ..." + LINE_SEPARATOR +
                        "... 10km gewandert" + LINE_SEPARATOR,
                mailBody);
    }

    private void printHash(org.springframework.security.crypto.password.PasswordEncoder encoder,
                           String user) {
        System.out.println(user + ": " + encoder.encode(user));
    }
}