package akeefer.web.components;

import akeefer.web.charts.ChartIntervall;
import org.assertj.core.api.SoftAssertions;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StackedColumnChartPanelTest {

    @Test
    public void testGetIntervall() throws Exception {
        Interval intervall = ChartIntervall.Woche.getIntervall();
        assertThat(intervall.contains(new DateTime().withMillisOfDay(0).plusDays(1)))
                .isEqualTo(false);
        assertThat(intervall.contains(new DateTime().withMillisOfDay(0).plusDays(1).minusMillis(1)))
                .isEqualTo(true);
        assertThat(intervall.contains(new DateTime().withMillisOfDay(0).plusDays(1).minusWeeks(1)))
                .isEqualTo(true);
        assertThat(intervall.contains(new DateTime().withMillisOfDay(0).plusDays(1).minusDays(7)))
                .isEqualTo(true);
        assertThat(intervall.contains(new DateTime().withMillisOfDay(0).plusDays(1).minusWeeks(1)
                .minusMillis(1))).isEqualTo(false);
    }

    @Test
    public void testGetIntervallGesamt() throws Exception {
        Interval intervall = ChartIntervall.Gesamt.getIntervall();
        DateTime base = new DateTime().withMillisOfDay(0).withDayOfYear(1).plusYears(1);
        assertThat(intervall.contains(base))
                .isEqualTo(false);
        assertThat(intervall.contains(base.minusMillis(1)))
                .isEqualTo(true);
    }

    @Test
    public void testGetIntervallJahr() throws Exception {
        Interval intervall = ChartIntervall.Jahr.getIntervall();
        DateTime base = new DateTime().withMillisOfDay(0).withDayOfMonth(1).plusMonths(1);
        assertThat(intervall.contains(base))
                .isEqualTo(false);
        assertThat(intervall.contains(base.minusMillis(1)))
                .isEqualTo(true);
    }

    @Test
    public void testGetBeschreibung() throws Exception {
        Interval interval = new Interval(new DateTime(2015, 1, 1, 0, 0, 0, 0), new DateTime());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(ChartIntervall.Woche.getBeschreibung(interval))
                .as("week")
                .isIn("Do 1.", "Thu 1.");
        softly.assertThat(ChartIntervall.Monat.getBeschreibung(interval))
                .as("month")
                .isEqualTo("1. Jan");
        softly.assertThat(ChartIntervall.Jahr.getBeschreibung(interval))
                .as("year")
                .isEqualTo("Jan 2015");
        softly.assertThat(ChartIntervall.Gesamt.getBeschreibung(interval))
                .as("total")
                .isEqualTo("2015");
        softly.assertAll();
    }
}