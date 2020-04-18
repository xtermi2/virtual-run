package akeefer.repository.mongo;

import akeefer.model.AktivitaetsAufzeichnung;
import akeefer.model.AktivitaetsTyp;
import akeefer.model.mongo.Aktivitaet;
import akeefer.repository.mongo.dto.TotalUserDistance;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.List;

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

        Assertions.assertThat(res)
                .containsExactly(new TotalUserDistance("bar", BigDecimal.ONE),
                        new TotalUserDistance("foo", BigDecimal.valueOf(12L)));
    }

    private Aktivitaet.AktivitaetBuilder defaultBuilder() {
        return Aktivitaet.builder()
                .typ(AktivitaetsTyp.radfahren)
                .aufzeichnungsart(AktivitaetsAufzeichnung.aufgezeichnet);
    }
}