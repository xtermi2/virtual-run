package akeefer.service.impl;

import akeefer.model.AktivitaetsAufzeichnung;
import akeefer.model.AktivitaetsTyp;
import akeefer.model.mongo.Aktivitaet;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class AktivitaetsDatumVonBisFilterTest {

    @Test
    public void testApply() {
        PersonServiceImpl.AktivitaetsDatumVonBisFilter filter = new PersonServiceImpl.AktivitaetsDatumVonBisFilter(new LocalDate().minusDays(1), new LocalDate().plusDays(1));

        assertEquals(true, filter.apply(createBaseAkt().aktivitaetsDatum(new LocalDate().toDate()).build()));
        assertEquals(true, filter.apply(createBaseAkt().aktivitaetsDatum(new LocalDate().plusDays(1).toDate()).build()));
        assertEquals(true, filter.apply(createBaseAkt().aktivitaetsDatum(new LocalDate().minusDays(1).toDate()).build()));
        assertEquals(false, filter.apply(createBaseAkt().aktivitaetsDatum(new LocalDate().plusDays(2).toDate()).build()));
        assertEquals(false, filter.apply(createBaseAkt().aktivitaetsDatum(new LocalDate().minusDays(2).toDate()).build()));
    }

    private Aktivitaet.AktivitaetBuilder createBaseAkt() {
        return Aktivitaet.builder()
                .distanzInKilometer(BigDecimal.ONE)
                .typ(AktivitaetsTyp.laufen)
                .aufzeichnungsart(AktivitaetsAufzeichnung.aufgezeichnet);
    }
}