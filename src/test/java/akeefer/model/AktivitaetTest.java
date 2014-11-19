package akeefer.model;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class AktivitaetTest {

    @Test
    public void testGetKilometer() throws Exception {
        Aktivitaet akt = new Aktivitaet();

        akt.setDistanzInMeter(0);
        assertEquals(new BigDecimal("0.000"), akt.getDistanzInKilometer());

        akt.setDistanzInMeter(1);
        assertEquals(new BigDecimal("0.001"), akt.getDistanzInKilometer());

        akt.setDistanzInMeter(5);
        assertEquals(new BigDecimal("0.005"), akt.getDistanzInKilometer());

        akt.setDistanzInMeter(65);
        assertEquals(new BigDecimal("0.065"), akt.getDistanzInKilometer());

        akt.setDistanzInMeter(100);
        assertEquals(new BigDecimal("0.100"), akt.getDistanzInKilometer());

        akt.setDistanzInMeter(12345);
        assertEquals(new BigDecimal("12.345"), akt.getDistanzInKilometer());
    }

    @Test
    public void testSetKilometer() throws Exception {
        Aktivitaet akt = new Aktivitaet();
        akt.setDistanzInMeter(1);

        akt.setDistanzInKilometer(null);
        assertEquals(null, akt.getDistanzInMeter());

        akt.setDistanzInKilometer(new BigDecimal(1.5));
        assertEquals(Integer.valueOf(1500), akt.getDistanzInMeter());

        akt.setDistanzInKilometer(new BigDecimal(0.54321));
        assertEquals(Integer.valueOf(543), akt.getDistanzInMeter());

        akt.setDistanzInKilometer(new BigDecimal(1.9999));
        assertEquals(Integer.valueOf(1999), akt.getDistanzInMeter());
    }
}