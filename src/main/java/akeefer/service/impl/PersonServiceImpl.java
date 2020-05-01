package akeefer.service.impl;

import akeefer.model.AktivitaetsTyp;
import akeefer.model.BenachrichtigunsIntervall;
import akeefer.model.SecurityRole;
import akeefer.model.mongo.Aktivitaet;
import akeefer.model.mongo.User;
import akeefer.repository.mongo.MongoAktivitaetRepository;
import akeefer.repository.mongo.MongoUserRepository;
import akeefer.repository.mongo.dto.AktivitaetSearchRequest;
import akeefer.repository.mongo.dto.TotalUserDistance;
import akeefer.repository.mongo.dto.UserDistanceByType;
import akeefer.service.PersonService;
import akeefer.service.dto.DbBackupMongo;
import akeefer.service.dto.Statistic;
import akeefer.util.Profiling;
import akeefer.web.charts.ChartIntervall;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.joda.time.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.cache.annotation.CacheKey;
import javax.cache.annotation.CachePut;
import javax.cache.annotation.CacheResult;
import javax.cache.annotation.CacheValue;
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
import java.util.stream.Collectors;

@Service("personServiceImpl")
@Transactional
public class PersonServiceImpl implements PersonService, UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(PersonServiceImpl.class);
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private final MongoUserRepository userRepository;

    private final MongoAktivitaetRepository aktivitaetRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PersonServiceImpl(MongoUserRepository userRepository,
                             MongoAktivitaetRepository aktivitaetRepository,
                             PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.aktivitaetRepository = aktivitaetRepository;
        this.passwordEncoder = passwordEncoder;
    }

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
        return userRepository.findByUsername(username);
    }

    @Override
    @Profiling
    @Transactional(readOnly = true)
    public String createPersonScript(String logedInUserId) {
        Validate.notNull(logedInUserId);

        StringBuilder personScript = new StringBuilder("var personen = [\n");
        Set<User> users = new TreeSet<User>(new UserHintenComparator(logedInUserId));
        List<User> allUser = getAllUser();
        users.addAll(allUser);
        Map<String, Integer> username2DistanzInMeter = aktivitaetRepository.calculateTotalDistanceForAllUsers().stream()
                .collect(Collectors.toMap(TotalUserDistance::getOwner, TotalUserDistance::getDistanzInMeter));
        for (User user : users) {
            personScript.append("        {id: '").append(user.getUsername())
                    .append("', nickname: '").append(StringUtils.isBlank(user.getNickname()) ? user.getUsername() : user.getNickname())
                    .append("', distance: ").append(username2DistanzInMeter.get(user.getUsername())).append("},\n");
        }
        //das letzte ',' entfernen
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

        private String userId;

        UserHintenComparator(String userId) {

            this.userId = userId;
        }

        @Override
        public int compare(User o1, User o2) {
            if (null == userId) {
                return o1.compareTo(o2);
            } else if (userId.equals(o1.getId())) {
                return 1;
            } else if (userId.equals(o2.getId())) {
                return -1;
            }
            return o1.compareTo(o2);
        }
    }

    @Override
    @Profiling
//    @CacheRemove(cacheName = "aktivitaeten")
    public Aktivitaet createAktivitaet(@CacheKey Aktivitaet akt, final User user, boolean setDate) {
        User userTmp = userRepository.findById(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user with id '" + user.getId() + "' not found"));
        if (null == akt.getId()) {
            // neue Aktivitaet
            if (setDate) {
                akt.setEingabeDatum(new Date());
            }
        } else if (setDate) {
            akt.setUpdatedDatum(new Date());
        }
        akt.setUser(userTmp);

        return aktivitaetRepository.save(akt);
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

        return userRepository.save(user);
    }

    @Override
    @Profiling
    public void deleteAktivitaet(User user, Aktivitaet aktivitaet) {
        if (null != aktivitaet) {
            Aktivitaet toDelete = aktivitaetRepository.findById(aktivitaet.getId())
                    .orElse(null);
            if (null != toDelete) {
                logger.info("loesche aktivitaet " + toDelete.getId());
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
    public List<Aktivitaet> loadAktivitaeten(String userId) {
        User user = userRepository.findById(userId)
                .orElse(null);
        if (null == user) {
            return Collections.emptyList();
        }
        return loadAktivitaetenByOwner(user.getUsername());
    }

    List<Aktivitaet> loadAktivitaetenByOwner(String owner) {
        final List<Aktivitaet> aktivitaeten = aktivitaetRepository.findAllByOwner(owner);
        logger.info("Anzahl gefundene Aktivitaeten(" + owner + "): " + aktivitaeten.size());
        return aktivitaeten;
    }

    @Override
    @Profiling
    public void changePassword(String userId, String cleartextPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user with id '" + userId + "' not found"));
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
    public User findUserById(String userId) {
        if (null == userId) {
            logger.info("findUserById(null) returns null");
            return null;
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user with id '" + userId + "' not found"));
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
                List<Aktivitaet> aktivitaeten = loadAktivitaetenByOwner(user.getUsername());
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
    public Map<AktivitaetsTyp, BigDecimal> createPieChartData(final String userId,
                                                              final LocalDate von,
                                                              final LocalDate bis) {
        String username = findUserById(userId).getUsername();
        return aktivitaetRepository.sumDistanceGroupedByActivityTypeAndFilterByOwnerAndDateRange(username, von, bis)
                .stream()
                .collect(Collectors.toMap(UserDistanceByType::getTyp, UserDistanceByType::getTotalDistanzInKilometer));
    }

    @Override
    @Profiling
    public Map<Interval, Map<AktivitaetsTyp, BigDecimal>> createStackedColumsChartData(String userId, ChartIntervall chartIntervall) {
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
        final List<Aktivitaet> aktivitaeten = loadAktivitaetenByOwner(username);
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
        BigDecimal dummyTotalDistance = BigDecimal.valueOf(13372);
        logger.info("returning dummy totalDistance: {}", dummyTotalDistance);
        return dummyTotalDistance;
    }

    @Override
    @Profiling
    @Transactional(readOnly = true)
    public DbBackupMongo createBackup(String... username) {
        Set<String> usernameFilter = new HashSet<>(Arrays.asList(username));
        List<User> users = usernameFilter.isEmpty()
                ? userRepository.findAll()
                : userRepository.findByUsernameIn(usernameFilter);

        // copy aktivitaeten
        List<Aktivitaet> aktivitaeten = usernameFilter.isEmpty()
                ? aktivitaetRepository.findAll()
                : aktivitaetRepository.findByOwnerIn(usernameFilter);

        return DbBackupMongo.builder()
                .users(users)
                .aktivitaeten(aktivitaeten)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    @Profiling
    public List<Aktivitaet> searchActivities(AktivitaetSearchRequest searchRequest) {
        Sort sort = searchRequest.isSortAsc()
                ? Sort.by(searchRequest.getSortProperty().getFieldName()).ascending()
                : Sort.by(searchRequest.getSortProperty().getFieldName()).descending();

        int page = searchRequest.getPageableFirstElement() / searchRequest.getPageSize();
        Pageable pageable = PageRequest.of(page, searchRequest.getPageSize(), sort);

        return aktivitaetRepository.findByOwner(searchRequest.getOwner(), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    @Profiling
    public long countActivities(String username) {
        return aktivitaetRepository.countByOwner(username);
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
            mailBody.append("https://afrika-run.de");
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
