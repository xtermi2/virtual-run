package akeefer.service.impl;

import akeefer.model.*;
import akeefer.repository.AktivitaetRepository;
import akeefer.repository.ParentRepository;
import akeefer.repository.UserRepository;
import akeefer.service.PersonService;
import akeefer.service.dto.DbBackup;
import akeefer.service.dto.Statistic;
import akeefer.util.Profiling;
import akeefer.web.charts.ChartIntervall;
import com.google.appengine.api.datastore.Key;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.joda.time.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.cache.annotation.*;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service("personServiceImpl")
@Transactional
public class PersonServiceImpl implements PersonService, UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(PersonServiceImpl.class);
    private static final String LINE_SEPARATOR = System.lineSeparator();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AktivitaetRepository aktivitaetRepository;

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Profiling
    @Transactional(readOnly = true)
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    @Profiling
    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
//        if (null != user) {
//            logger.info(String.format("user (username='%s') has %s Aktivitaeten", username,
//                    null == user.getAktivitaeten() ? "null" : user.getAktivitaeten().size()));
//        }
        return user;
    }

    @Override
    @Profiling
    @Transactional(readOnly = true)
    public String createPersonScript(Key logedInUserId) {
        Validate.notNull(logedInUserId);

        StringBuilder personScript = new StringBuilder("var personen = [\n");
        Set<User> users = new TreeSet<User>(new UserHintenComparator(logedInUserId));
        List<User> allUser = getAllUser();
        users.addAll(allUser);
        for (User user : users) {
            personScript.append("        {id: '").append(user.getUsername())
                    .append("', nickname: '").append(StringUtils.isBlank(user.getNickname()) ? user.getUsername() : user.getNickname())
                    .append("', distance: ").append(berechneDistanzInMeter(user)).append("},\n");
        }
        //das letzte ',' enfernen
        personScript.deleteCharAt(personScript.length() - 2);
        personScript.append("    ];");
        return personScript.toString();
    }

    @Override
    @Profiling
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getUserByUsername(username);
        if (null == user) {
            throw new UsernameNotFoundException(username + " not found in GAE Datastore.");
        }

        Set<GrantedAuthority> rollen = new HashSet<>();
        if (null != user.getRoles()) {
            for (SecurityRole role : user.getRoles()) {
                SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role.name());
                rollen.add(grantedAuthority);
            }
        }
        org.springframework.security.core.userdetails.User res = new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), rollen);

        return res;
    }

    // Sortiert den user mit dem Key nach unten
    static class UserHintenComparator implements Comparator<User> {

        private Key userKey;

        UserHintenComparator(Key userKey) {

            this.userKey = userKey;
        }

        @Override
        public int compare(User o1, User o2) {
            if (null == userKey) {
                return o1.compareTo(o2);
            } else if (userKey.equals(o1.getId())) {
                return 1;
            } else if (userKey.equals(o2.getId())) {
                return -1;
            }
            return o1.compareTo(o2);
        }
    }

    private int berechneDistanzInMeter(User user) {
        BigDecimal distanzInKm = BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP);
        for (Aktivitaet akt : loadAktivitaeten(user.getUsername())) {
            if (null != akt.getDistanzInMeter()) {
                distanzInKm = distanzInKm.add(akt.getDistanzInKilometer());
            }
        }
        return distanzInKm.multiply(BigDecimal.valueOf(1000L)).intValue();
    }

    @Override
    @Profiling
    @CacheRemove(cacheName = "aktivitaeten")
    public Aktivitaet createAktivitaet(@CacheKey Aktivitaet akt, final User user, boolean setDate) {
        User userTmp = userRepository.findOne(user.getId());
        if (null == akt.getId()) {
            // neue Aktivitaet
            if (setDate) {
                akt.setEingabeDatum(new Date());
            }
            // Relationen herstellen bei neuer Akt
            if (null == userTmp.getAktivitaeten()) {
                userTmp.setAktivitaeten(new ArrayList<Aktivitaet>());
            }
            userTmp.getAktivitaeten().add(akt);
        } else if (setDate) {
            akt.setUpdatedDatum(new Date());
        }
        akt.setUser(userTmp);

        akt = aktivitaetRepository.save(akt);

        // muss immer pasieren, sonst wird in PROD die Liste in der Uebersicht nicht aktualisiert
        //user.setAktivitaeten(userTmp.getAktivitaeten());

        return akt;
    }

    @Override
    @Profiling
    public User createUserIfAbsent(User user, boolean skipPwEncoding) {
        logger.info("createUserIfAbsent: " + user);
        User userInDb = findUserByUsername(getAllUser(), user.getUsername());
        if (null != userInDb) {
            logger.info(String.format("User username='%s' already exists, i will not create a new one", user.getUsername()));
            if (!userInDb.getRoles().containsAll(user.getRoles())) {
                logger.info("fuege Rollen hinzu");
                userInDb.getRoles().addAll(user.getRoles());
            }
            if (userInDb.getUsername().equals(userInDb.getPassword())) {
                logger.info("PW von user '" + userInDb.getUsername() + "' muss encoded werden");
                userInDb.setPassword(passwordEncoder.encode(userInDb.getPassword()));
            }

            return userInDb;
        }
        if (!skipPwEncoding) {
            logger.info("PW von user '" + user.getUsername() + "' wird encoded");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        Parent parent = getParent();
        user.setParent(parent);

        return userRepository.save(user);
    }

    @Override
    @Profiling
    public void deleteAktivitaet(User user, Aktivitaet aktivitaet) {
        if (null != aktivitaet) {
            //user.getAktivitaeten().remove(aktivitaet);
            Aktivitaet toDelete = aktivitaetRepository.findOne(aktivitaet.getId());
            if (null != toDelete) {
                logger.info("loesche aktivitaet " + toDelete.getId());
                toDelete.getUser().getAktivitaeten().remove(toDelete);
                aktivitaetRepository.deleteAktivitaet(toDelete);
            } else {
                logger.warn("akt nicht gefunden: " + aktivitaet.getId());
            }
        } else {
            logger.warn("null Akt kann nicht geloescht werden!");
        }
    }

    @Override
    @Profiling
    @Transactional(readOnly = true)
    public List<Aktivitaet> loadAktivitaeten(Key userId) {
        User user = userRepository.findOne(userId);
        if (null == user) {
            return Collections.emptyList();
        }
        return loadAktivitaeten(user.getUsername());
    }

    List<Aktivitaet> loadAktivitaeten(String owner) {
        final List<Aktivitaet> aktivitaeten = aktivitaetRepository.findAllByOwner(owner);
        logger.info("Anzahl gefundene Aktivitaeten(" + owner + "): " + aktivitaeten.size());
        return aktivitaeten;
    }

    @Override
    @Profiling
    public void changePassword(Key userId, String cleartextPassword) {
        User user = userRepository.findOne(userId);
        logger.info("change password of user " + user);
        user.setPassword(passwordEncoder.encode(cleartextPassword));
        userRepository.save(user);
    }

    @Override
    @Profiling
    public User updateUser(User user) {
        user = userRepository.save(user);
        return user;
    }

    @Override
    @Profiling
    public User findUserById(Key userId) {
        if (null == userId) {
            logger.info("findUserById(null) returns null");
            return null;
        }
        return userRepository.findOne(userId);
    }

    @Override
    @Profiling
    @Transactional(readOnly = true)
    public Set<Statistic> createStatistic(BenachrichtigunsIntervall interval) {
        logger.info("createStatistic: " + interval);
        List<User> users = getAllUser();
        if (Iterables.any(users, new BenachrichtigunsIntervallPredicate(interval))) {
            Set<Statistic> statistics = new HashSet<>(users.size());
            Interval intervall = new Interval(new DateTime().withTimeAtStartOfDay().minusDays(interval.getTage()), new DateTime().withTimeAtStartOfDay());
            AktErstellungsdatumPredicate aktErstellungsdatumPredicate = new AktErstellungsdatumPredicate(intervall);
            for (User user : users) {
                Statistic statistic = new Statistic(user);
                statistics.add(statistic);
                List<Aktivitaet> aktivitaeten = loadAktivitaeten(user.getUsername());
                for (Aktivitaet akt : Iterables.filter(aktivitaeten, aktErstellungsdatumPredicate)) {
                    statistic.add(akt.getTyp(), akt.getDistanzInKilometer());
                }
            }
            return statistics;
        } else {
            logger.info(String.format("no user configures '%s' as NotificationInterval", interval));
            return null;
        }
    }

    @Override
    @Profiling
    public void sendStatisticMail(BenachrichtigunsIntervall interval) {
        Validate.notNull(interval, "interval must not be null");
        logger.info("sendStatisticMail: " + interval);
        List<User> users = getAllUser();
        Set<Statistic> statistics = createStatistic(interval);
        if (CollectionUtils.isNotEmpty(statistics) && CollectionUtils.isNotEmpty(users)) {
            logger.info("Erzeuge Mail Session...");
            // Mail Session
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);
            logger.info("... Mail Session wurde erzeugt");
            for (User user : getAllUser()) {
                if (interval.equals(user.getBenachrichtigunsIntervall())) {
                    logger.info("user hat passenden Intervall: " + interval);
                    String mailBody = buildMailBody(statistics, user, interval);

                    logger.info("sending statistic mail to " + user.getEmail());
                    try {
                        final Message msg = new MimeMessage(session);
                        final String applicationId = getGaeApplicationId();
                        msg.setFrom(new InternetAddress("statistic@" + applicationId + ".appspotmail.com", "Africa Run Statistics"));
                        msg.addRecipient(Message.RecipientType.TO,
                                new InternetAddress(user.getEmail(), user.getAnzeigename()));
                        msg.setSubject("Africa Run Statistics");
                        msg.setText(mailBody);
                        Transport.send(msg);
                    } catch (MessagingException | UnsupportedEncodingException e) {
                        logger.warn("Fehler beim Mailversand", e);
                    }
                }
            }
        } else {
            logger.info("sendStatisticMail: keine passenden Daten fuer " + interval);
        }
    }

    @Override
    @Profiling
    public Map<AktivitaetsTyp, BigDecimal> createPieChartData(final Key userId,
                                                              final LocalDate von,
                                                              final LocalDate bis) {
        final List<Aktivitaet> aktivitaeten = loadAktivitaeten(userId);
        if (CollectionUtils.isEmpty(aktivitaeten)) {
            return Collections.emptyMap();
        }

        Map<AktivitaetsTyp, BigDecimal> res = new HashMap<>();
        for (Aktivitaet akt : Collections2.filter(aktivitaeten, new AktivitaetsDatumVonBisFilter(von, bis))) {
            BigDecimal distanz = res.get(akt.getTyp());
            if (null == distanz) {
                distanz = akt.getDistanzInKilometer();
            } else {
                distanz = distanz.add(akt.getDistanzInKilometer());
            }
            res.put(akt.getTyp(), distanz);
        }

        return res;
    }

    @Override
    @Profiling
    public Map<Interval, Map<AktivitaetsTyp, BigDecimal>> createStackedColumsChartData(Key userId, ChartIntervall chartIntervall) {
        final List<Aktivitaet> aktivitaeten = loadAktivitaeten(userId);
        if (CollectionUtils.isEmpty(aktivitaeten)) {
            return Collections.emptyMap();
        }

        Map<Interval, Map<AktivitaetsTyp, BigDecimal>> res = new TreeMap<>(INTERVAL_COMPARATOR);
        Interval zeitraum = chartIntervall.getIntervall();
        List<Aktivitaet> akts = new ArrayList<>(Collections2.filter(aktivitaeten,
                new AktivitaetsDatumVonBisFilter(zeitraum.getStart().toLocalDate(), zeitraum.getEnd().toLocalDate())));

        for (Iterator<Interval> iter = new IntervalIterator(zeitraum, chartIntervall.getIteratorResolution()); iter.hasNext(); ) {
            Interval interval = iter.next();
            Map<AktivitaetsTyp, BigDecimal> dataInInterval = res.get(interval);
            if (null == dataInInterval) {
                dataInInterval = new HashMap<>();
                res.put(interval, dataInInterval);
            }
            for (Iterator<Aktivitaet> aktIter = akts.iterator(); aktIter.hasNext(); ) {
                Aktivitaet akt = aktIter.next();
                if (interval.contains(new DateTime(akt.getAktivitaetsDatum()))) {
                    aktIter.remove();
                    BigDecimal distanz = dataInInterval.get(akt.getTyp());
                    if (null == distanz) {
                        distanz = BigDecimal.ZERO;
                    }
                    dataInInterval.put(akt.getTyp(), distanz.add(akt.getDistanzInKilometer()));
                }
            }
        }

        return res;
    }

    @Override
    @Profiling
    public Map<LocalDate, BigDecimal> createForecastData(String username,
                                                         BigDecimal totalDistanceInKm) {
        final List<Aktivitaet> aktivitaeten = loadAktivitaeten(username);
        if (CollectionUtils.isEmpty(aktivitaeten)) {
            return Collections.emptyMap();
        }
        final NavigableMap<LocalDate, BigDecimal> res = new TreeMap<>();

        for (Aktivitaet akt : aktivitaeten) {
            final LocalDate aktDate = new LocalDate(akt.getAktivitaetsDatum());

            // Tagesstatistik erstellen
            BigDecimal dayDistance = res.get(aktDate);
            if (null == dayDistance) {
                dayDistance = akt.getDistanzInKilometer();
            } else {
                dayDistance = dayDistance.add(akt.getDistanzInKilometer());
            }
            res.put(aktDate, dayDistance);
        }

        // aufsummieren
        BigDecimal distanceInKm = BigDecimal.ZERO;
        for (Map.Entry<LocalDate, BigDecimal> entry : res.entrySet()) {
            // Gesammtdaten berechnen
            distanceInKm = distanceInKm.add(entry.getValue());
            entry.setValue(distanceInKm);
        }

        final LocalDate firstAkt = res.firstKey();

        // forecast berechnen
        if (null != totalDistanceInKm) {
            Duration duration = firstAkt.toInterval().withEnd(new DateTime()).toDuration();
            BigDecimal days = BigDecimal.valueOf(duration.getStandardDays());
            BigDecimal forecastDays = days.divide(distanceInKm, 3, BigDecimal.ROUND_HALF_UP)
                    .multiply(totalDistanceInKm).setScale(0, RoundingMode.HALF_UP);
            res.put(firstAkt.plusDays(forecastDays.intValue()), totalDistanceInKm);
        } else {
            logger.warn("totalDistanceInKm is null! Can't calculate forecast!");
        }

        return res;
    }

    @Profiling
    @Override
    @CachePut(cacheName = "totalDistance")
    public BigDecimal updateTotalDistance(@CacheValue BigDecimal totalDistanceInKm) {
        return totalDistanceInKm;
    }

    @Profiling
    @Override
    @CacheResult(cacheName = "totalDistance")
    public BigDecimal getTotalDistance() {
        BigDecimal dummyTotalDistance = BigDecimal.valueOf(12867);
        logger.info("returning dummy totalDistance: {}", dummyTotalDistance);
        return dummyTotalDistance;
    }

    @Override
    @Profiling
    @Transactional(readOnly = true)
    public DbBackup createBackup(String... username) {
        List<String> usernameFilter = Arrays.asList(username);
        // copy users
        List<User> users = new LinkedList<>();
        for (User user : getAllUser()) {
            if (usernameFilter.isEmpty() || usernameFilter.contains(user.getUsername())) {
                users.add(User.newBuilder(user)//
                        .withParent(null)//
                        .withAktivitaeten(null)//
                        .build());
            }
        }

        // copy aktivitaeten
        List<Aktivitaet> aktivitaeten = new LinkedList<>();
        for (Aktivitaet akt : aktivitaetRepository.findAll()) {
            if (usernameFilter.isEmpty() || usernameFilter.contains(akt.getOwner())) {
                aktivitaeten.add(Aktivitaet.newBuilder(akt)//
                        .withUser(null)//
                        .build());
            }
        }
        return DbBackup.newBuilder()//
                .withUsers(users)//
                .withAktivitaeten(aktivitaeten)//
                .build();
    }

    @Override
    @Profiling
    public int importBackup(DbBackup dbBackup) {
        int res = HttpStatus.OK.value();
        if (dbBackup != null) {
            Collection<String> existingUsernames = Collections2.transform(getAllUser(), new Function<User, String>() {
                @Override
                public String apply(User input) {
                    return input.getUsername();
                }
            });
            final Map<String, User> usersInDbMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(dbBackup.getUsers())) {
                logger.info("importing users...");
                for (User user : dbBackup.getUsers()) {
                    if (!existingUsernames.contains(user.getUsername())) {
                        user.setId(null);
                        User userInDb = createUserIfAbsent(user, true);
                        usersInDbMap.put(userInDb.getUsername(), userInDb);
                        res = HttpStatus.CREATED.value();
                    }
                }
                logger.info("{} users imported", usersInDbMap.size());
            }
            if (CollectionUtils.isNotEmpty(dbBackup.getAktivitaeten())) {
                int importCounter = 0;
                logger.info("importing activities...");
                for (Aktivitaet akt : dbBackup.getAktivitaeten()) {
                    if (!existingUsernames.contains(akt.getOwner())) {
                        User userInDb = usersInDbMap.containsKey(akt.getOwner()) //
                                ? usersInDbMap.get(akt.getOwner())//
                                : getUserByUsername(akt.getOwner());
                        Assert.notNull(userInDb, "no user found in DB with username '" + akt.getOwner() + "' [" + akt + "]");
                        akt.setId(null);
                        createAktivitaet(akt, userInDb, false);
                        res = HttpStatus.CREATED.value();
                        importCounter++;
                    }
                }
                logger.info("{} activities imported", importCounter);
            }
        }

        return res;
    }

    private static final Comparator<Interval> INTERVAL_COMPARATOR = new IntervalComparator();

    static final class IntervalComparator implements Comparator<Interval> {
        @Override
        public int compare(Interval o1, Interval o2) {
            return o1.getStart().compareTo(o2.getStart());
        }
    }

    static final class IntervalIterator implements Iterator<Interval> {

        private Interval zeitraum;
        private ReadablePeriod resolution;

        IntervalIterator(Interval zeitraum, ReadablePeriod resolution) {

            this.zeitraum = zeitraum;
            this.resolution = resolution;
        }

        @Override
        public boolean hasNext() {
            DateTime start = zeitraum.getEnd().minus(resolution);
            return zeitraum.contains(start) || zeitraum.getStart().equals(start);
        }

        @Override
        public Interval next() {
            DateTime start = zeitraum.getEnd().minus(resolution);
            Interval res = zeitraum.withStart(start);
            zeitraum = zeitraum.withEnd(start);
            return res;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("IntervalIterator does not support remove!");
        }
    }


    String buildMailBody(Set<Statistic> statistics, User user, BenachrichtigunsIntervall interval) {
        StringBuilder mailBody = new StringBuilder("Hallo ")
                .append(user.getAnzeigename())
                .append(',')
                .append(LINE_SEPARATOR)
                .append(LINE_SEPARATOR);

        StringBuilder statisticsBody = new StringBuilder();
        for (Statistic statistic : Iterables.filter(statistics, new StatisticUserPredicate(user))) {
            statisticsBody.append(statistic.toMailString());
        }

        if (StringUtils.isNotBlank(statisticsBody)) {
            mailBody.append(interval.getZeitinterval())
                    .append(" ist folgendes passiert: ")
                    .append(LINE_SEPARATOR)
                    .append(LINE_SEPARATOR)
                    .append(statisticsBody);
        } else {
            mailBody.append(interval.getZeitinterval())
                    .append(" ist nichts passiert. ");
        }

        // host URL erzeugen
        mailBody.append(LINE_SEPARATOR)
                .append(LINE_SEPARATOR);
        final String applicationId = getGaeApplicationId();
        if (null != applicationId) {
            mailBody.append("https://").append(applicationId).append(".appspot.com");
        } else {
            mailBody.append("http://localhost:8080");
        }

        return mailBody.toString();
    }

    private String getGaeApplicationId() {
        String environment = System.getProperty("com.google.appengine.runtime.environment");
        if ("Production".equals(environment)) {
            return System.getProperty("com.google.appengine.application.id");
        }
        return null;
    }

    private Parent getParent() {
        List<Parent> parents = parentRepository.findAll();
        final Parent parent;
        if (CollectionUtils.isEmpty(parents)) {
            logger.info("Parent will be created...");
            parent = parentRepository.save(new Parent());
        } else {
            logger.info("using existing Parent from Database");
            parent = parents.get(0);
        }

        return parent;
    }

    private User findUserByUsername(Iterable<User> users, String username) {
        for (User user : users) {
            if (username.equals(user.getUsername())) {
                return user;
            }
        }
        return null;
    }

    static class BenachrichtigunsIntervallPredicate implements Predicate<User> {

        private final BenachrichtigunsIntervall intervall;

        public BenachrichtigunsIntervallPredicate(BenachrichtigunsIntervall intervall) {
            this.intervall = intervall;
        }

        @Override
        public boolean apply(User user) {
            return intervall.equals(user.getBenachrichtigunsIntervall());
        }
    }

    static class AktErstellungsdatumPredicate implements Predicate<Aktivitaet> {

        private final Interval interval;

        public AktErstellungsdatumPredicate(Interval interval) {
            this.interval = interval;
        }

        @Override
        public boolean apply(Aktivitaet aktivitaet) {
            return interval.contains(new DateTime(aktivitaet.getEingabeDatum()));
        }
    }

    static class StatisticUserPredicate implements Predicate<Statistic> {

        private final User user;

        public StatisticUserPredicate(User user) {
            this.user = user;
        }

        @Override
        public boolean apply(Statistic input) {
            return !user.equals(input.getUser()) || user.isIncludeMeInStatisticMail();
        }
    }

    static final class AktivitaetsDatumVonBisFilter implements Predicate<Aktivitaet> {

        private LocalDate von;
        private LocalDate bis;

        public AktivitaetsDatumVonBisFilter(LocalDate von, LocalDate bis) {
            this.von = von;
            this.bis = bis;
        }

        @Override
        public boolean apply(Aktivitaet input) {
            LocalDate aktDatum = new LocalDate(input.getAktivitaetsDatum());
            return aktDatum.isEqual(von) || aktDatum.isEqual(bis)
                    || (aktDatum.isAfter(von) && aktDatum.isBefore(bis));
        }
    }
}
