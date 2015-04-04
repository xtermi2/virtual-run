package akeefer.service.impl;

import akeefer.model.Aktivitaet;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class AktivitaetsDatumVonBisFilterTest {

    @Test
    public void testApply() throws Exception {
        PersonServiceImpl.AktivitaetsDatumVonBisFilter filter = new PersonServiceImpl.AktivitaetsDatumVonBisFilter(new LocalDate().minusDays(1), new LocalDate().plusDays(1));

        assertEquals(true, filter.apply(Aktivitaet.newBuilder().aktivitaetsDatum(new LocalDate().toDate()).build()));
        assertEquals(true, filter.apply(Aktivitaet.newBuilder().aktivitaetsDatum(new LocalDate().plusDays(1).toDate()).build()));
        assertEquals(true, filter.apply(Aktivitaet.newBuilder().aktivitaetsDatum(new LocalDate().minusDays(1).toDate()).build()));
        assertEquals(false, filter.apply(Aktivitaet.newBuilder().aktivitaetsDatum(new LocalDate().plusDays(2).toDate()).build()));
        assertEquals(false, filter.apply(Aktivitaet.newBuilder().aktivitaetsDatum(new LocalDate().minusDays(2).toDate()).build()));
    }
}