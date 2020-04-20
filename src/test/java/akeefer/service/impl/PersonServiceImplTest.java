package akeefer.service.impl;

import akeefer.model.AktivitaetsAufzeichnung;
import akeefer.model.AktivitaetsTyp;
import akeefer.model.BenachrichtigunsIntervall;
import akeefer.model.mongo.Aktivitaet;
import akeefer.model.mongo.User;
import akeefer.repository.mongo.MongoAktivitaetRepository;
import akeefer.repository.mongo.MongoUserRepository;
import akeefer.repository.mongo.dto.AktivitaetSearchRequest;
import akeefer.repository.mongo.dto.AktivitaetSortProperties;
import akeefer.repository.mongo.dto.TotalUserDistance;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static akeefer.test.util.ProxyUtil.getTargetObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testApplicationContext.xml"})
public class PersonServiceImplTest {

    private static final String LINE_SEPARATOR = System.lineSeparator();

    @Autowired
    private MongoUserRepository userRepository;

    @Autowired
    private MongoAktivitaetRepository aktivitaetRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Qualifier("personServiceImpl")
    private PersonService personService;

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Before
    public void setUp() {
        helper.setUp();
        aktivitaetRepository.deleteAll();
    }

    @After
    public void tearDown() {
        helper.tearDown();
        aktivitaetRepository.deleteAll();
    }

    @Test
    public void testCreatePersonScript() {
        MongoAktivitaetRepository aktivitaetRepositoryMock = mock(MongoAktivitaetRepository.class);
        PersonServiceImpl spy = spy(new PersonServiceImpl(userRepository, aktivitaetRepositoryMock, passwordEncoder));
        // Mocks
        User user1 = new User("1");
        user1.setUsername("foo");
        user1.setNickname("Foo User");
        User user2 = new User("2");
        user2.setUsername("user2");
        User user3 = new User("3");
        user3.setUsername("user3");
        assertFalse("user1 is equals user2", user1.equals(user2));
        final List<User> users = Arrays.asList(user3, user1, user2);
        doReturn(users).when(spy).getAllUser();
        User logedIn = new User(user1.getId());
        logedIn.setUsername("hallo");
        doReturn(Arrays.asList(
                new TotalUserDistance(user1.getUsername(), new BigDecimal("4.711")),
                new TotalUserDistance(user2.getUsername(), BigDecimal.ZERO),
                new TotalUserDistance(user3.getUsername(), BigDecimal.ZERO))
        ).when(aktivitaetRepositoryMock).calculateTotalDistanceForAllUsers();

        String personScript = spy.createPersonScript(logedIn.getId());

        System.out.println(personScript);
        assertEquals("var personen = [\n" +
                "        {id: 'user2', nickname: 'user2', distance: 0},\n" +
                "        {id: 'user3', nickname: 'user3', distance: 0},\n" +
                "        {id: 'foo', nickname: 'Foo User', distance: 4711}\n" +
                "    ];", personScript);
    }

    @Test
    public void testPasswordEncoding() {
        printHash(passwordEncoder, "andi");
        printHash(passwordEncoder, "sabine");
        printHash(passwordEncoder, "norbert");
        printHash(passwordEncoder, "roland");
        printHash(passwordEncoder, "uli-hans");
        printHash(passwordEncoder, "frank");
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
    public void searchAktivities_paging_works() {
        String owner = "andi";
        List<Aktivitaet> aktivities = createAktivities(owner, 12);

        AktivitaetSearchRequest searchRequest = AktivitaetSearchRequest.builder()
                .owner(owner)
                .pageableFirstElement(0)
                .pageSize(5)
                .sortProperty(AktivitaetSortProperties.DISTANZ_IN_KILOMETER)
                .sortAsc(true)
                .build();

        List<Aktivitaet> firstPage = personService.searchActivities(searchRequest);

        Assertions.assertThat(firstPage)
                .containsExactlyElementsOf(aktivities.subList(0, 5));

        List<Aktivitaet> secondPage = personService.searchActivities(searchRequest.toBuilder()
                .pageableFirstElement(5)
                .build());

        Assertions.assertThat(secondPage)
                .containsExactlyElementsOf(aktivities.subList(5, 10));

        List<Aktivitaet> thirdPage = personService.searchActivities(searchRequest.toBuilder()
                .pageableFirstElement(10)
                .build());

        Assertions.assertThat(thirdPage)
                .containsExactlyElementsOf(aktivities.subList(10, 12));
    }

    @Test
    public void searchAktivities_filter_by_owner_works() {
        String andi = "andi";
        List<Aktivitaet> aktivitiesAndi = createAktivities(andi, 1);
        String foo = "foo";
        List<Aktivitaet> aktivitiesFoo = createAktivities(foo, 1);

        AktivitaetSearchRequest searchRequest = AktivitaetSearchRequest.builder()
                .owner(andi)
                .pageableFirstElement(0)
                .pageSize(5)
                .sortProperty(AktivitaetSortProperties.DISTANZ_IN_KILOMETER)
                .sortAsc(true)
                .build();

        List<Aktivitaet> firstPage = personService.searchActivities(searchRequest);

        Assertions.assertThat(firstPage)
                .containsExactlyElementsOf(aktivitiesAndi);
    }

    @Test
    public void searchAktivities_sort_By_distance() {
        String owner = "andi";
        List<Aktivitaet> activities = createAktivities(owner, 12);

        AktivitaetSearchRequest searchRequest = AktivitaetSearchRequest.builder()
                .owner(owner)
                .pageableFirstElement(0)
                .pageSize(12)
                .sortProperty(AktivitaetSortProperties.DISTANZ_IN_KILOMETER)
                .sortAsc(false)
                .build();

        List<Aktivitaet> firstPage = personService.searchActivities(searchRequest);

        activities.sort(Comparator.comparing(Aktivitaet::getDistanzInKilometer).reversed());
        Assertions.assertThat(firstPage)
                .containsExactlyElementsOf(activities);
    }

    @Test
    public void searchAktivities_sort_By_activityDate() {
        String owner = "andi";
        List<Aktivitaet> activities = createAktivities(owner, 12);

        AktivitaetSearchRequest searchRequest = AktivitaetSearchRequest.builder()
                .owner(owner)
                .pageableFirstElement(0)
                .pageSize(12)
                .sortProperty(AktivitaetSortProperties.AKTIVITAETS_DATUM)
                .sortAsc(true)
                .build();

        List<Aktivitaet> asc = personService.searchActivities(searchRequest);

        activities.sort(Comparator.comparing(Aktivitaet::getAktivitaetsDatum));
        Assertions.assertThat(asc)
                .containsExactlyElementsOf(activities);

        List<Aktivitaet> desc = personService.searchActivities(searchRequest.toBuilder()
                .sortAsc(false)
                .build());

        activities.sort(Comparator.comparing(Aktivitaet::getAktivitaetsDatum).reversed());
        Assertions.assertThat(desc)
                .containsExactlyElementsOf(activities);
    }

    @Test
    public void searchAktivities_sort_By_aufzeichnungsart() {
        String owner = "andi";
        List<Aktivitaet> activities = createAktivities(owner, 12);

        AktivitaetSearchRequest searchRequest = AktivitaetSearchRequest.builder()
                .owner(owner)
                .pageableFirstElement(0)
                .pageSize(12)
                .sortProperty(AktivitaetSortProperties.AUFZEICHNUNGSART)
                .sortAsc(true)
                .build();

        List<Aktivitaet> asc = personService.searchActivities(searchRequest);

        activities.sort(Comparator.comparing(Aktivitaet::getAufzeichnungsart));
        Assertions.assertThat(asc)
                .containsExactlyElementsOf(activities);

        List<Aktivitaet> desc = personService.searchActivities(searchRequest.toBuilder()
                .sortAsc(false)
                .build());

        activities.sort(Comparator.comparing(Aktivitaet::getAufzeichnungsart).reversed());
        Assertions.assertThat(desc)
                .containsExactlyElementsOf(activities);
    }

    @Test
    public void searchAktivities_sort_By_bezeichnung() {
        String owner = "andi";
        List<Aktivitaet> activities = createAktivities(owner, 12);

        AktivitaetSearchRequest searchRequest = AktivitaetSearchRequest.builder()
                .owner(owner)
                .pageableFirstElement(0)
                .pageSize(12)
                .sortProperty(AktivitaetSortProperties.BEZEICHNUNG)
                .sortAsc(true)
                .build();

        List<Aktivitaet> asc = personService.searchActivities(searchRequest);

        activities.sort(Comparator.comparing(Aktivitaet::getBezeichnung));
        Assertions.assertThat(asc)
                .containsExactlyElementsOf(activities);

        List<Aktivitaet> desc = personService.searchActivities(searchRequest.toBuilder()
                .sortAsc(false)
                .build());

        activities.sort(Comparator.comparing(Aktivitaet::getBezeichnung).reversed());
        Assertions.assertThat(desc)
                .containsExactlyElementsOf(activities);
    }

    @Test
    public void searchAktivities_sort_By_typ() {
        String owner = "andi";
        List<Aktivitaet> activities = createAktivities(owner, 12);

        AktivitaetSearchRequest searchRequest = AktivitaetSearchRequest.builder()
                .owner(owner)
                .pageableFirstElement(0)
                .pageSize(12)
                .sortProperty(AktivitaetSortProperties.TYP)
                .sortAsc(true)
                .build();

        List<Aktivitaet> asc = personService.searchActivities(searchRequest);

        activities.sort(Comparator.comparing(Aktivitaet::getTyp));
        Assertions.assertThat(asc)
                .containsExactlyElementsOf(activities);

        List<Aktivitaet> desc = personService.searchActivities(searchRequest.toBuilder()
                .sortAsc(false)
                .build());

        activities.sort(Comparator.comparing(Aktivitaet::getTyp).reversed());
        Assertions.assertThat(desc)
                .containsExactlyElementsOf(activities);
    }

    @Test
    public void countActivities_should_count_activities_of_given_user() {
        String owner = "andi";
        List<Aktivitaet> activitiesOfAndi = createAktivities(owner, 12);
        createAktivities("fred", 3);

        long res = personService.countActivities(owner);

        Assertions.assertThat(res)
                .isEqualTo(activitiesOfAndi.size());
    }

    private List<Aktivitaet> createAktivities(String owner, int count) {
        List<Aktivitaet> aktivitaets = IntStream.range(0, count)
                .mapToObj(i -> Aktivitaet.builder()
                        .owner(owner)
                        .bezeichnung("bez " + i)
                        .typ(AktivitaetsTyp.values()[count % AktivitaetsTyp.values().length])
                        .distanzInKilometer(BigDecimal.valueOf(i))
                        .aktivitaetsDatum(Date.valueOf(LocalDate.now().minusDays(i)))
                        .aufzeichnungsart(AktivitaetsAufzeichnung.values()[count % AktivitaetsAufzeichnung.values().length])
                        .build())
                .collect(Collectors.toList());
        return aktivitaetRepository.saveAll(aktivitaets);
    }

    private void printHash(PasswordEncoder encoder, String user) {
        System.out.println(user + ": " + encoder.encode(user));
    }
}