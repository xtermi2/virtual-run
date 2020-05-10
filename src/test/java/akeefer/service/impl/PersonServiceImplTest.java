package akeefer.service.impl;

import akeefer.model.AktivitaetsAufzeichnung;
import akeefer.model.AktivitaetsTyp;
import akeefer.model.BenachrichtigunsIntervall;
import akeefer.model.SecurityRole;
import akeefer.model.mongo.Aktivitaet;
import akeefer.model.mongo.User;
import akeefer.repository.mongo.MongoAktivitaetRepository;
import akeefer.repository.mongo.MongoUserRepository;
import akeefer.repository.mongo.dto.AktivitaetSearchRequest;
import akeefer.repository.mongo.dto.AktivitaetSortProperties;
import akeefer.repository.mongo.dto.TotalUserDistance;
import akeefer.service.PersonService;
import akeefer.service.dto.Statistic;
import akeefer.service.dto.UserForecast;
import akeefer.web.charts.ChartIntervall;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Interval;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static akeefer.model.AktivitaetsTyp.*;
import static akeefer.test.util.ProxyUtil.getTargetObject;
import static java.time.temporal.ChronoField.DAY_OF_WEEK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
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
        userRepository.deleteAll();
    }

    @After
    public void tearDown() {
        helper.tearDown();
        aktivitaetRepository.deleteAll();
        userRepository.deleteAll();
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
        assertNotEquals("user1 is equals user2", user1, user2);
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
        aktivitaetList.add(createDefaultAkt().owner(user1.getUsername()).distanzInKilometer(BigDecimal.valueOf(2)).typ(radfahren).eingabeDatum(new DateTime().minusDays(1).toDate()).build());
        aktivitaetList.add(createDefaultAkt().owner(user1.getUsername()).distanzInKilometer(BigDecimal.valueOf(3)).typ(AktivitaetsTyp.laufen).eingabeDatum(new DateTime().minusDays(2).toDate()).build());
        aktivitaetList.add(createDefaultAkt().owner(user1.getUsername()).distanzInKilometer(BigDecimal.valueOf(4)).typ(AktivitaetsTyp.laufen).eingabeDatum(new DateTime().minusDays(7).toDate()).build());
        aktivitaetList.add(createDefaultAkt().owner(user1.getUsername()).distanzInKilometer(BigDecimal.valueOf(5)).typ(AktivitaetsTyp.laufen).eingabeDatum(new DateTime().minusDays(8).toDate()).build());

        User user2 = new User(UUID.randomUUID());
        user2.setUsername("user2");
        user2.setBenachrichtigunsIntervall(BenachrichtigunsIntervall.woechnetlich);
        aktivitaetList.add(createDefaultAkt().owner(user2.getUsername()).distanzInKilometer(BigDecimal.ONE).typ(AktivitaetsTyp.laufen).eingabeDatum(new DateTime().toDate()).build());
        aktivitaetList.add(createDefaultAkt().owner(user2.getUsername()).distanzInKilometer(BigDecimal.valueOf(2)).typ(radfahren).eingabeDatum(new DateTime().minusDays(1).toDate()).build());
        aktivitaetList.add(createDefaultAkt().owner(user2.getUsername()).distanzInKilometer(BigDecimal.valueOf(3)).typ(AktivitaetsTyp.laufen).eingabeDatum(new DateTime().minusDays(2).toDate()).build());
        aktivitaetList.add(createDefaultAkt().owner(user2.getUsername()).distanzInKilometer(BigDecimal.valueOf(4)).typ(AktivitaetsTyp.laufen).eingabeDatum(new DateTime().minusDays(7).toDate()).build());
        aktivitaetList.add(createDefaultAkt().owner(user2.getUsername()).distanzInKilometer(BigDecimal.valueOf(5)).typ(AktivitaetsTyp.laufen).eingabeDatum(new DateTime().minusDays(8).toDate()).build());

        User user3 = new User(UUID.randomUUID());
        user3.setBenachrichtigunsIntervall(BenachrichtigunsIntervall.deaktiviert);
        user3.setUsername("user3");
        aktivitaetList.add(createDefaultAkt().owner(user3.getUsername()).distanzInKilometer(BigDecimal.ONE).typ(AktivitaetsTyp.laufen).eingabeDatum(new DateTime().toDate()).build());
        aktivitaetList.add(createDefaultAkt().owner(user3.getUsername()).distanzInKilometer(BigDecimal.valueOf(2)).typ(radfahren).eingabeDatum(new DateTime().minusDays(1).toDate()).build());
        aktivitaetList.add(createDefaultAkt().owner(user3.getUsername()).distanzInKilometer(BigDecimal.valueOf(3)).typ(AktivitaetsTyp.laufen).eingabeDatum(new DateTime().minusDays(2).toDate()).build());
        aktivitaetList.add(createDefaultAkt().owner(user3.getUsername()).distanzInKilometer(BigDecimal.valueOf(4)).typ(AktivitaetsTyp.laufen).eingabeDatum(new DateTime().minusDays(7).toDate()).build());
        aktivitaetList.add(createDefaultAkt().owner(user3.getUsername()).distanzInKilometer(BigDecimal.valueOf(5)).typ(AktivitaetsTyp.laufen).eingabeDatum(new DateTime().minusDays(8).toDate()).build());


        final List<User> users = Arrays.asList(user3, user1, user2);
        doReturn(users).when(spy).getAllUser();
        doAnswer(invocation -> {
            List<Aktivitaet> res = new ArrayList<>();
            for (Aktivitaet akt : aktivitaetList) {
                if (invocation.getArguments()[0].equals(akt.getOwner())) {
                    res.add(akt);
                }
            }
            return res;
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
        assertEquals(radfahren, entry.getKey());
        assertEquals(new BigDecimal("2"), entry.getValue());

        statistics = spy.createStatistic(BenachrichtigunsIntervall.woechnetlich);
        assertEquals(3, statistics.size());
        statistic = statistics.iterator().next();
        assertEquals(2, statistic.getAggregated().size());
        assertEquals(new BigDecimal("2"), statistic.getAggregated().get(radfahren));
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

        assertThat(firstPage)
                .containsExactlyElementsOf(aktivities.subList(0, 5));

        List<Aktivitaet> secondPage = personService.searchActivities(searchRequest.toBuilder()
                .pageableFirstElement(5)
                .build());

        assertThat(secondPage)
                .containsExactlyElementsOf(aktivities.subList(5, 10));

        List<Aktivitaet> thirdPage = personService.searchActivities(searchRequest.toBuilder()
                .pageableFirstElement(10)
                .build());

        assertThat(thirdPage)
                .containsExactlyElementsOf(aktivities.subList(10, 12));
    }

    @Test
    public void searchAktivities_filter_by_owner_works() {
        String andi = "andi";
        List<Aktivitaet> aktivitiesAndi = createAktivities(andi, 1);
        String foo = "foo";
        createAktivities(foo, 1);

        AktivitaetSearchRequest searchRequest = AktivitaetSearchRequest.builder()
                .owner(andi)
                .pageableFirstElement(0)
                .pageSize(5)
                .sortProperty(AktivitaetSortProperties.DISTANZ_IN_KILOMETER)
                .sortAsc(true)
                .build();

        List<Aktivitaet> firstPage = personService.searchActivities(searchRequest);

        assertThat(firstPage)
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
        assertThat(firstPage)
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
        assertThat(asc)
                .containsExactlyElementsOf(activities);

        List<Aktivitaet> desc = personService.searchActivities(searchRequest.toBuilder()
                .sortAsc(false)
                .build());

        activities.sort(Comparator.comparing(Aktivitaet::getAktivitaetsDatum).reversed());
        assertThat(desc)
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
        assertThat(asc)
                .containsExactlyElementsOf(activities);

        List<Aktivitaet> desc = personService.searchActivities(searchRequest.toBuilder()
                .sortAsc(false)
                .build());

        activities.sort(Comparator.comparing(Aktivitaet::getAufzeichnungsart).reversed());
        assertThat(desc)
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
        assertThat(asc)
                .containsExactlyElementsOf(activities);

        List<Aktivitaet> desc = personService.searchActivities(searchRequest.toBuilder()
                .sortAsc(false)
                .build());

        activities.sort(Comparator.comparing(Aktivitaet::getBezeichnung).reversed());
        assertThat(desc)
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

        activities.sort(Comparator.comparing(aktivitaet -> aktivitaet.getTyp().name()));
        assertThat(asc)
                .containsExactlyElementsOf(activities);

        List<Aktivitaet> desc = personService.searchActivities(searchRequest.toBuilder()
                .sortAsc(false)
                .build());

        activities.sort(Comparator.comparing((Aktivitaet aktivitaet) -> aktivitaet.getTyp().name()).reversed());
        assertThat(desc)
                .containsExactlyElementsOf(activities);
    }

    @Test
    public void countActivities_should_count_activities_of_given_user() {
        String owner = "andi";
        List<Aktivitaet> activitiesOfAndi = createAktivities(owner, 12);
        createAktivities("fred", 3);

        long res = personService.countActivities(owner);

        assertThat(res)
                .isEqualTo(activitiesOfAndi.size());
    }

    @Test
    public void createStackedColumsChartData_noActivities() {
        User user = userRepository.save(User.builder()
                .username("andi")
                .password("pw")
                .role(SecurityRole.USER)
                .build());
        ChartIntervall chartIntervall = ChartIntervall.Woche;

        Map<Interval, Map<AktivitaetsTyp, BigDecimal>> res = personService.createStackedColumsChartData(
                user.getId(), chartIntervall);

        assertThat(res)
                .isEmpty();
    }

    @Test
    public void createStackedColumsChartData_week() {
        String username = "andi";
        User user = userRepository.save(User.builder()
                .username(username)
                .password("pw")
                .role(SecurityRole.USER)
                .build());
        createAkt(username, LocalDate.now(), "21", radfahren);
        createAkt(username, LocalDate.now(), "1", radfahren);
        createAkt(username, LocalDate.now(), "2", laufen);
        createAkt(username, LocalDate.now().minusDays(6), "9", laufen);
        createAkt(username, LocalDate.now().minusDays(7), "8", laufen);
        createAkt(username, LocalDate.now().plusDays(1), "4", laufen);
        createAkt("foo", LocalDate.now(), "8", laufen);
        ChartIntervall chartIntervall = ChartIntervall.Woche;


        Map<Interval, Map<AktivitaetsTyp, BigDecimal>> res = personService.createStackedColumsChartData(
                user.getId(), chartIntervall);


        assertThat(res)
                .containsExactly(
                        new SimpleEntry<>(
                                new Interval(chartIntervall.getIteratorResolution(), DateTime.now().withMillisOfDay(0).minusDays(5)),
                                Collections.singletonMap(laufen, new BigDecimal("9"))),
                        new SimpleEntry<>(
                                new Interval(chartIntervall.getIteratorResolution(), DateTime.now().withMillisOfDay(0).minusDays(4)),
                                Collections.emptyMap()),
                        new SimpleEntry<>(
                                new Interval(chartIntervall.getIteratorResolution(), DateTime.now().withMillisOfDay(0).minusDays(3)),
                                Collections.emptyMap()),
                        new SimpleEntry<>(
                                new Interval(chartIntervall.getIteratorResolution(), DateTime.now().withMillisOfDay(0).minusDays(2)),
                                Collections.emptyMap()),
                        new SimpleEntry<>(
                                new Interval(chartIntervall.getIteratorResolution(), DateTime.now().withMillisOfDay(0).minusDays(1)),
                                Collections.emptyMap()),
                        new SimpleEntry<>(
                                new Interval(chartIntervall.getIteratorResolution(), DateTime.now().withMillisOfDay(0)),
                                Collections.emptyMap()),
                        new SimpleEntry<>(
                                new Interval(chartIntervall.getIteratorResolution(), DateTime.now().withMillisOfDay(0).plusDays(1)),
                                ImmutableMap.of(laufen, new BigDecimal("2"), radfahren, new BigDecimal("22")))
                );
    }

    @Test
    public void createStackedColumsChartData_month() {
        String username = "andi";
        User user = userRepository.save(User.builder()
                .username(username)
                .password("pw")
                .role(SecurityRole.USER)
                .build());
        createAkt(username, LocalDate.now().minusMonths(1).plusDays(1), "5", radfahren);
        createAkt(username, LocalDate.now().minusMonths(1).plusDays(1), "5", radfahren);
        createAkt(username, LocalDate.now().minusMonths(1), "5", radfahren);
        createAkt(username, LocalDate.now(), "1", laufen);
        createAkt(username, LocalDate.now().plusDays(1), "4", laufen);
        createAkt("foo", LocalDate.now(), "8", laufen);
        ChartIntervall chartIntervall = ChartIntervall.Monat;


        Map<Interval, Map<AktivitaetsTyp, BigDecimal>> res = personService.createStackedColumsChartData(
                user.getId(), chartIntervall);


        PersonServiceImpl.IntervalIterator iterator = new PersonServiceImpl.IntervalIterator(
                new Interval(chartIntervall.getIntervall().getStart(), chartIntervall.getIntervall().getEnd()), Days.ONE);
        Map<Interval, Map<AktivitaetsTyp, BigDecimal>> expectedRes = new HashMap<>();
        boolean first = true;
        while (iterator.hasNext()) {
            Interval interval = iterator.next();
            if (first) {
                expectedRes.put(interval, ImmutableMap.of(laufen, BigDecimal.ONE));
                first = false;
            } else if (!iterator.hasNext()) {
                expectedRes.put(interval, ImmutableMap.of(radfahren, BigDecimal.TEN));
            } else {
                expectedRes.put(interval, ImmutableMap.of());
            }
        }
        assertThat(res)
                .containsExactlyInAnyOrderEntriesOf(expectedRes);
    }

    @Test
    public void createStackedColumsChartData_year() {
        String username = "andi";
        User user = userRepository.save(User.builder()
                .username(username)
                .password("pw")
                .role(SecurityRole.USER)
                .build());
        createAkt(username, LocalDate.now(), "21", radfahren);
        createAkt(username, LocalDate.now().withDayOfMonth(1).plusMonths(1), "4", laufen);
        createAkt("foo", LocalDate.now(), "8", laufen);
        ChartIntervall chartIntervall = ChartIntervall.Jahr;


        Map<Interval, Map<AktivitaetsTyp, BigDecimal>> res = personService.createStackedColumsChartData(
                user.getId(), chartIntervall);


        assertThat(res)
                .containsExactly(
                        new SimpleEntry<>(
                                new Interval(chartIntervall.getIteratorResolution(), DateTime.now().withMillisOfDay(0).withDayOfMonth(1).minusMonths(10)),
                                Collections.emptyMap()),
                        new SimpleEntry<>(
                                new Interval(chartIntervall.getIteratorResolution(), DateTime.now().withMillisOfDay(0).withDayOfMonth(1).minusMonths(9)),
                                Collections.emptyMap()),
                        new SimpleEntry<>(
                                new Interval(chartIntervall.getIteratorResolution(), DateTime.now().withMillisOfDay(0).withDayOfMonth(1).minusMonths(8)),
                                Collections.emptyMap()),
                        new SimpleEntry<>(
                                new Interval(chartIntervall.getIteratorResolution(), DateTime.now().withMillisOfDay(0).withDayOfMonth(1).minusMonths(7)),
                                Collections.emptyMap()),
                        new SimpleEntry<>(
                                new Interval(chartIntervall.getIteratorResolution(), DateTime.now().withMillisOfDay(0).withDayOfMonth(1).minusMonths(6)),
                                Collections.emptyMap()),
                        new SimpleEntry<>(
                                new Interval(chartIntervall.getIteratorResolution(), DateTime.now().withMillisOfDay(0).withDayOfMonth(1).minusMonths(5)),
                                Collections.emptyMap()),
                        new SimpleEntry<>(
                                new Interval(chartIntervall.getIteratorResolution(), DateTime.now().withMillisOfDay(0).withDayOfMonth(1).minusMonths(4)),
                                Collections.emptyMap()),
                        new SimpleEntry<>(
                                new Interval(chartIntervall.getIteratorResolution(), DateTime.now().withMillisOfDay(0).withDayOfMonth(1).minusMonths(3)),
                                Collections.emptyMap()),
                        new SimpleEntry<>(
                                new Interval(chartIntervall.getIteratorResolution(), DateTime.now().withMillisOfDay(0).withDayOfMonth(1).minusMonths(2)),
                                Collections.emptyMap()),
                        new SimpleEntry<>(
                                new Interval(chartIntervall.getIteratorResolution(), DateTime.now().withMillisOfDay(0).withDayOfMonth(1).minusMonths(1)),
                                Collections.emptyMap()),
                        new SimpleEntry<>(
                                new Interval(chartIntervall.getIteratorResolution(), DateTime.now().withMillisOfDay(0).withDayOfMonth(1)),
                                Collections.emptyMap()),
                        new SimpleEntry<>(
                                new Interval(chartIntervall.getIteratorResolution(), DateTime.now().withMillisOfDay(0).withDayOfMonth(1).plusMonths(1)),
                                ImmutableMap.of(radfahren, new BigDecimal("21")))
                );
    }

    @Test
    public void createStackedColumsChartData_total() {
        String username = "andi";
        User user = userRepository.save(User.builder()
                .username(username)
                .password("pw")
                .role(SecurityRole.USER)
                .build());
        createAkt(username, LocalDate.now().withDayOfMonth(31).withMonth(12), "21", radfahren);
        createAkt(username, LocalDate.now().withDayOfYear(1).withMonth(1).plusYears(1), "4", laufen);
        createAkt("foo", LocalDate.now(), "8", laufen);
        ChartIntervall chartIntervall = ChartIntervall.Gesamt;


        Map<Interval, Map<AktivitaetsTyp, BigDecimal>> res = personService.createStackedColumsChartData(
                user.getId(), chartIntervall);


        assertThat(res)
                .containsExactly(
                        new SimpleEntry<>(
                                new Interval(chartIntervall.getIteratorResolution(), DateTime.now().withMillisOfDay(0).withDayOfYear(1).minusYears(8)),
                                Collections.emptyMap()),
                        new SimpleEntry<>(
                                new Interval(chartIntervall.getIteratorResolution(), DateTime.now().withMillisOfDay(0).withDayOfYear(1).minusYears(7)),
                                Collections.emptyMap()),
                        new SimpleEntry<>(
                                new Interval(chartIntervall.getIteratorResolution(), DateTime.now().withMillisOfDay(0).withDayOfYear(1).minusYears(6)),
                                Collections.emptyMap()),
                        new SimpleEntry<>(
                                new Interval(chartIntervall.getIteratorResolution(), DateTime.now().withMillisOfDay(0).withDayOfYear(1).minusYears(5)),
                                Collections.emptyMap()),
                        new SimpleEntry<>(
                                new Interval(chartIntervall.getIteratorResolution(), DateTime.now().withMillisOfDay(0).withDayOfYear(1).minusYears(4)),
                                Collections.emptyMap()),
                        new SimpleEntry<>(
                                new Interval(chartIntervall.getIteratorResolution(), DateTime.now().withMillisOfDay(0).withDayOfYear(1).minusYears(3)),
                                Collections.emptyMap()),
                        new SimpleEntry<>(
                                new Interval(chartIntervall.getIteratorResolution(), DateTime.now().withMillisOfDay(0).withDayOfYear(1).minusYears(2)),
                                Collections.emptyMap()),
                        new SimpleEntry<>(
                                new Interval(chartIntervall.getIteratorResolution(), DateTime.now().withMillisOfDay(0).withDayOfYear(1).minusYears(1)),
                                Collections.emptyMap()),
                        new SimpleEntry<>(
                                new Interval(chartIntervall.getIteratorResolution(), DateTime.now().withMillisOfDay(0).withDayOfYear(1)),
                                Collections.emptyMap()),
                        new SimpleEntry<>(
                                new Interval(chartIntervall.getIteratorResolution(), DateTime.now().withMillisOfDay(0).withDayOfYear(1).plusYears(1)),
                                ImmutableMap.of(radfahren, new BigDecimal("21")))
                );
    }

    @Test
    public void createForecastData_singleUser_noData() {
        String username = "andi";
        userRepository.save(User.builder()
                .username(username)
                .password("pw")
                .role(SecurityRole.USER)
                .build());

        List<UserForecast> res = personService.createForecastData(BigDecimal.valueOf(1000), username);

        assertThat(res)
                .isEmpty();
    }

    @Test
    public void createForecastData_singleUser_withData() {
        String usernameAndi = "andi";
        createAkt(usernameAndi, LocalDate.now().minusMonths(1).with(DAY_OF_WEEK, 2), "5", wandern);
        createAkt(usernameAndi, LocalDate.now().minusMonths(1).with(DAY_OF_WEEK, 2), "5", radfahren);
        createAkt(usernameAndi, LocalDate.now().minusMonths(1).with(DAY_OF_WEEK, 1), "245", radfahren);
        createAkt(usernameAndi, LocalDate.now().with(DAY_OF_WEEK, 1), "1", laufen);
        createAkt(usernameAndi, LocalDate.now().with(DAY_OF_WEEK, 1).minusDays(1), "4", laufen);
        String usernameFoo = "foo";
        createAkt(usernameFoo, LocalDate.now().with(DAY_OF_WEEK, 2), "8", laufen);
        createAkt(usernameFoo, LocalDate.now().with(DAY_OF_WEEK, 1).minusDays(1), "42", radfahren);


        List<UserForecast> res = personService.createForecastData(BigDecimal.valueOf(1000), usernameAndi);


        org.joda.time.LocalDate now = org.joda.time.LocalDate.now();
        assertThat(res)
                .hasSize(1)
                .allSatisfy(userForecast -> {
                    assertThat(userForecast.getUsername()).isEqualTo(usernameAndi);
                    assertThat(userForecast.getAggregatedDistancesPerDay())
                            .contains(
                                    new SimpleEntry<>(now.minusMonths(1).withDayOfWeek(7), new BigDecimal("255")),
                                    new SimpleEntry<>(now.minusWeeks(1).withDayOfWeek(7), new BigDecimal("259")),
                                    new SimpleEntry<>(now.withDayOfWeek(7), new BigDecimal("260"))
                            )
                            .hasSize(4);
                    Map.Entry<org.joda.time.LocalDate, BigDecimal> lastEntry = userForecast.getAggregatedDistancesPerDay().lastEntry();
                    assertThat(lastEntry.getValue())
                            .isEqualByComparingTo(new BigDecimal("1000"));
                    assertThat(lastEntry.getKey())
                            .isBetween(now.withDayOfWeek(1).plusWeeks(12), now.withDayOfWeek(7).plusWeeks(12));
                });
    }

    @Test
    public void createForecastData_multiUser_withData() {
        String usernameAndi = "andi";
        createAkt(usernameAndi, LocalDate.now().minusMonths(1).with(DAY_OF_WEEK, 2), "5", wandern);
        createAkt(usernameAndi, LocalDate.now().minusMonths(1).with(DAY_OF_WEEK, 2), "5", radfahren);
        createAkt(usernameAndi, LocalDate.now().minusMonths(1).with(DAY_OF_WEEK, 1), "245", radfahren);
        createAkt(usernameAndi, LocalDate.now().with(DAY_OF_WEEK, 1), "1", laufen);
        createAkt(usernameAndi, LocalDate.now().with(DAY_OF_WEEK, 1).minusDays(1), "4", laufen);
        String usernameFoo = "foo";
        createAkt(usernameFoo, LocalDate.now().with(DAY_OF_WEEK, 2), "8", laufen);
        createAkt(usernameFoo, LocalDate.now().with(DAY_OF_WEEK, 1).minusDays(1), "42", radfahren);


        List<UserForecast> res = personService.createForecastData(BigDecimal.valueOf(1000), usernameAndi, usernameFoo)
                .stream()
                .sorted(Comparator.comparing(UserForecast::getUsername))
                .collect(Collectors.toList());


        org.joda.time.LocalDate now = org.joda.time.LocalDate.now();
        assertThat(res)
                .hasSize(2);
        {
            UserForecast andiForecast = res.get(0);
            assertThat(andiForecast.getUsername()).isEqualTo(usernameAndi);
            assertThat(andiForecast.getAggregatedDistancesPerDay())
                    .contains(
                            new SimpleEntry<>(now.minusMonths(1).withDayOfWeek(7), new BigDecimal("255")),
                            new SimpleEntry<>(now.minusWeeks(1).withDayOfWeek(7), new BigDecimal("259")),
                            new SimpleEntry<>(now.withDayOfWeek(7), new BigDecimal("260"))
                    )
                    .hasSize(4);
            Map.Entry<org.joda.time.LocalDate, BigDecimal> lastEntryAndi = andiForecast.getAggregatedDistancesPerDay().lastEntry();
            assertThat(lastEntryAndi.getValue())
                    .isEqualByComparingTo(new BigDecimal("1000"));
            assertThat(lastEntryAndi.getKey())
                    .isBetween(now.withDayOfWeek(1).plusWeeks(12), now.withDayOfWeek(7).plusWeeks(12));
        }
        {
            UserForecast fooForecast = res.get(1);
            assertThat(fooForecast.getUsername()).isEqualTo(usernameFoo);
            assertThat(fooForecast.getAggregatedDistancesPerDay())
                    .contains(
                            new SimpleEntry<>(now.withDayOfWeek(7).minusWeeks(1), new BigDecimal("42")),
                            new SimpleEntry<>(now.withDayOfWeek(7), new BigDecimal("50"))
                    )
                    .hasSize(3);
            Map.Entry<org.joda.time.LocalDate, BigDecimal> lastEntryFoo = fooForecast.getAggregatedDistancesPerDay().lastEntry();
            assertThat(lastEntryFoo.getValue())
                    .isEqualByComparingTo(new BigDecimal("1000"));
            assertThat(lastEntryFoo.getKey())
                    .isBetween(now.withDayOfWeek(1).plusWeeks(19), now.withDayOfWeek(7).plusWeeks(19));
        }
    }

    private Aktivitaet createAkt(String owner,
                                 LocalDate aktDate,
                                 String distance,
                                 AktivitaetsTyp typ) {
        return aktivitaetRepository.save(Aktivitaet.builder()
                .owner(owner)
                .distanzInKilometer(new BigDecimal(distance))
                .aktivitaetsDatum(Date.from(aktDate.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .typ(typ)
                .aufzeichnungsart(AktivitaetsAufzeichnung.aufgezeichnet)
                .build());
    }

    private List<Aktivitaet> createAktivities(String owner, int count) {
        List<Aktivitaet> aktivitaets = IntStream.range(0, count)
                .mapToObj(i -> Aktivitaet.builder()
                        .owner(owner)
                        .bezeichnung("bez " + i)
                        .typ(AktivitaetsTyp.values()[i % AktivitaetsTyp.values().length])
                        .distanzInKilometer(BigDecimal.valueOf(i))
                        .aktivitaetsDatum(Date.valueOf(LocalDate.now().minusDays(i)))
                        .aufzeichnungsart(AktivitaetsAufzeichnung.values()[i % AktivitaetsAufzeichnung.values().length])
                        .build())
                .collect(Collectors.toList());
        return aktivitaetRepository.saveAll(aktivitaets);
    }

    private void printHash(PasswordEncoder encoder, String user) {
        System.out.println(user + ": " + encoder.encode(user));
    }
}