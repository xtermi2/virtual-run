package akeefer.web.components;

import akeefer.web.charts.ChartIntervall;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StackedColumnChartPanelTest {

    @Test
    public void testGetIntervall() throws Exception {
        Interval intervall = ChartIntervall.Woche.getIntervall();
        assertEquals(false, intervall.contains(new DateTime().withMillisOfDay(0).plusDays(1)));
        assertEquals(true, intervall.contains(new DateTime().withMillisOfDay(0).plusDays(1).minusMillis(1)));
        assertEquals(true, intervall.contains(new DateTime().withMillisOfDay(0).plusDays(1).minusWeeks(1)));
        assertEquals(true, intervall.contains(new DateTime().withMillisOfDay(0).plusDays(1).minusDays(7)));
        assertEquals(false, intervall.contains(new DateTime().withMillisOfDay(0).plusDays(1).minusWeeks(1).minusMillis(1)));
    }

    @Test
    public void testGetIntervallGesamt() throws Exception {
        Interval intervall = ChartIntervall.Gesamt.getIntervall();
        DateTime base = new DateTime().withMillisOfDay(0).withDayOfYear(1).plusYears(1);
        assertEquals(false, intervall.contains(base));
        assertEquals(true, intervall.contains(base.minusMillis(1)));
    }

    @Test
    public void testGetIntervallJahr() throws Exception {
        Interval intervall = ChartIntervall.Jahr.getIntervall();
        DateTime base = new DateTime().withMillisOfDay(0).withDayOfMonth(1).plusMonths(1);
        assertEquals(false, intervall.contains(base));
        assertEquals(true, intervall.contains(base.minusMillis(1)));
    }

    @Test
    public void testGetBeschreibung() throws Exception {
        assertEquals("Do 1.", ChartIntervall.Woche.getBeschreibung(new Interval(new DateTime(2015, 1,1,0,0,0,0), new DateTime())));
        assertEquals("1. Jan", ChartIntervall.Monat.getBeschreibung(new Interval(new DateTime(2015, 1,1,0,0,0,0), new DateTime())));
        assertEquals("Jan 2015", ChartIntervall.Jahr.getBeschreibung(new Interval(new DateTime(2015, 1,1,0,0,0,0), new DateTime())));
        assertEquals("2015", ChartIntervall.Gesamt.getBeschreibung(new Interval(new DateTime(2015, 1,1,0,0,0,0), new DateTime())));
    }
}