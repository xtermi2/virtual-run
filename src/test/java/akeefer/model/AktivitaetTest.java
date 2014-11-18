package akeefer.model;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class AktivitaetTest {

    @Test
    public void testGetKilometer() throws Exception {
        Aktivitaet akt = new Aktivitaet();

        akt.setMeter(0);
        assertEquals(new BigDecimal("0.00"), akt.getKilometer());

        akt.setMeter(1);
        assertEquals(new BigDecimal("0.00"), akt.getKilometer());

        akt.setMeter(5);
        assertEquals(new BigDecimal("0.01"), akt.getKilometer());

        akt.setMeter(65);
        assertEquals(new BigDecimal("0.07"), akt.getKilometer());

        akt.setMeter(100);
        assertEquals(new BigDecimal("0.10"), akt.getKilometer());

        akt.setMeter(12345);
        assertEquals(new BigDecimal("12.35"), akt.getKilometer());
    }

    @Test
    public void testSetKilometer() throws Exception {
        Aktivitaet akt = new Aktivitaet();

        akt.setKilometer(null);
        assertEquals(0, akt.getMeter());

        akt.setKilometer(new BigDecimal(1.5));
        assertEquals(1500, akt.getMeter());

        akt.setKilometer(new BigDecimal(0.54321));
        assertEquals(543, akt.getMeter());

        akt.setKilometer(new BigDecimal(1.9999));
        assertEquals(1999, akt.getMeter());
    }
}