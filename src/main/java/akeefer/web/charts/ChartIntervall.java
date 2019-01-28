package akeefer.web.charts;

import org.joda.time.*;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

/**
 * Created by akeefer on 06.04.15.
 */
public enum ChartIntervall {
    Woche(Weeks.ONE, Days.ONE, new DateTimeFormatterBuilder().
            appendDayOfWeekShortText()
            .appendLiteral(' ')
            .appendDayOfMonth(1)
            .appendLiteral('.')
            .toFormatter()),
    Monat(Months.ONE, Days.ONE, new DateTimeFormatterBuilder()
            .appendDayOfMonth(1)
            .appendLiteral(". ")
            .appendMonthOfYearShortText()
            .toFormatter()),
    Jahr(Years.ONE, Months.ONE, new DateTimeFormatterBuilder()
            .appendMonthOfYearShortText()
            .appendLiteral(' ')
            .appendYear(2, 2)
            .toFormatter()),
    Gesamt(Years.years(10), Years.ONE, new DateTimeFormatterBuilder()
            .appendYear(4, 4)
            .toFormatter());

    private final ReadablePeriod dauer;
    private final ReadablePeriod iteratorResolution;
    private final DateTimeFormatter formatter;

    ChartIntervall(ReadablePeriod dauer, ReadablePeriod iteratorResolution, DateTimeFormatter formatter) {
        this.dauer = dauer;
        this.iteratorResolution = iteratorResolution;
        this.formatter = formatter;
    }

    public Interval getIntervall() {
        if (Gesamt.equals(this)) {
            return new Interval(dauer, DateTime.now().withMillisOfDay(0).withDayOfYear(1).plusYears(1));
        } else if (Jahr.equals(this)) {
            return new Interval(dauer, DateTime.now().withMillisOfDay(0).withDayOfMonth(1).plusMonths(1));
        } else {
            return new Interval(dauer, DateTime.now().withMillisOfDay(0).plusDays(1));
        }
    }

    public ReadablePeriod getIteratorResolution() {
        return iteratorResolution;
    }

    public String getBeschreibung(Interval interval) {
        return formatter.print(interval.getStart());
    }
}
