package akeefer.repository.mongo;

import akeefer.model.AktivitaetsAufzeichnung;
import akeefer.model.AktivitaetsTyp;
import akeefer.model.mongo.Aktivitaet;
import akeefer.repository.mongo.dto.TotalUserDistance;
import akeefer.repository.mongo.dto.UserDistanceByType;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testApplicationContext.xml"})
public class MongoAktivitaetRepositoryTest {

    @Autowired
    private MongoAktivitaetRepository aktivitaetRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() {
        mongoTemplate.dropCollection(Aktivitaet.class);
    }

    @After
    public void tearDown() {
        mongoTemplate.dropCollection(Aktivitaet.class);
    }

    @Test
    public void calculateTotalDistanceForAllUsers() {

        aktivitaetRepository.save(defaultBuilder()
                .owner("foo")
                .distanzInKilometer(BigDecimal.ONE)
                .build());
        aktivitaetRepository.save(defaultBuilder()
                .owner("foo")
                .distanzInKilometer(BigDecimal.TEN)
                .build());
        aktivitaetRepository.save(defaultBuilder()
                .owner("foo")
                .distanzInKilometer(BigDecimal.ONE)
                .build());
        aktivitaetRepository.save(defaultBuilder()
                .owner("bar")
                .distanzInKilometer(BigDecimal.ONE)
                .build());

        List<TotalUserDistance> res = aktivitaetRepository.calculateTotalDistanceForAllUsers();

        assertThat(res)
                .containsExactly(new TotalUserDistance("bar", BigDecimal.ONE),
                        new TotalUserDistance("foo", BigDecimal.valueOf(12L)));
    }

    @Test
    public void sumDistanceGroupedByActivityTypeAndFilterByOwnerAndDateRange_emptyCollection() {
        String owner = "andi";
        LocalDate from = LocalDate.now().minusDays(1);
        LocalDate to = LocalDate.now();

        List<UserDistanceByType> res = aktivitaetRepository.sumDistanceGroupedByActivityTypeAndFilterByOwnerAndDateRange(
                owner, from, to);

        assertThat(res)
                .isEmpty();
    }

    @Test
    public void sumDistanceGroupedByActivityTypeAndFilterByOwnerAndDateRange_filtersAndGroupingWork() {
        String owner = "andi";
        LocalDateTime from = LocalDateTime.now().withMillisOfDay(0).minusDays(1);
        LocalDateTime to = LocalDateTime.now().withMillisOfDay(0).plusDays(1).minusMillis(1);
        aktivitaetRepository.save(defaultBuilder()
                .bezeichnung("andi-now")
                .owner(owner)
                .distanzInKilometer(new BigDecimal("0.2"))
                .aktivitaetsDatum(new Date())
                .build());
        aktivitaetRepository.save(defaultBuilder()
                .bezeichnung("andi-from")
                .owner(owner)
                .distanzInKilometer(new BigDecimal("3.5"))
                .aktivitaetsDatum(from.toDate())
                .build());
        aktivitaetRepository.save(defaultBuilder()
                .bezeichnung("andi-to")
                .owner(owner)
                .distanzInKilometer(BigDecimal.ONE)
                .aktivitaetsDatum(to.toDate())
                .build());
        aktivitaetRepository.save(defaultBuilder()
                .bezeichnung("andi-to-out-of-range")
                .owner(owner)
                .distanzInKilometer(new BigDecimal("123.9"))
                .aktivitaetsDatum(to.plusMillis(1).toDate())
                .build());
        aktivitaetRepository.save(defaultBuilder()
                .bezeichnung("andi-from-out-of-range")
                .owner(owner)
                .distanzInKilometer(BigDecimal.valueOf(99999L))
                .aktivitaetsDatum(from.minusMillis(1).toDate())
                .build());
        aktivitaetRepository.save(defaultBuilder()
                .bezeichnung("andi-now-swim")
                .owner(owner)
                .distanzInKilometer(new BigDecimal("0.222"))
                .aktivitaetsDatum(new Date())
                .typ(AktivitaetsTyp.schwimmen)
                .build());
        aktivitaetRepository.save(defaultBuilder()
                .owner("foo")
                .distanzInKilometer(new BigDecimal("19.999"))
                .build());


        List<UserDistanceByType> res = aktivitaetRepository.sumDistanceGroupedByActivityTypeAndFilterByOwnerAndDateRange(
                owner, from.toLocalDate(), to.toLocalDate());

        assertThat(res)
                .hasSize(2)
                .containsExactlyInAnyOrder(
                        new UserDistanceByType(owner, AktivitaetsTyp.radfahren, new BigDecimal("4.7")),
                        new UserDistanceByType(owner, AktivitaetsTyp.schwimmen, new BigDecimal("0.222"))
                );
    }

    private Aktivitaet.AktivitaetBuilder defaultBuilder() {
        return Aktivitaet.builder()
                .typ(AktivitaetsTyp.radfahren)
                .aufzeichnungsart(AktivitaetsAufzeichnung.aufgezeichnet);
    }
}