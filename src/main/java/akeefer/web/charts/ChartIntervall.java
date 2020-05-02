package akeefer.web.charts;

import org.joda.time.*;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.time.LocalDate;

import static java.time.temporal.ChronoField.*;

/**
 * Created by akeefer on 06.04.15.
 */
public enum ChartIntervall {
    Woche(Weeks.ONE, Days.ONE, new DateTimeFormatterBuilder().
            appendDayOfWeekShortText()
            .appendLiteral(' ')
            .appendDayOfMonth(1)
            .appendLiteral('.')
            .toFormatter(),
            "%Y-%m-%d",
            java.time.format.DateTimeFormatter.ISO_LOCAL_DATE),
    Monat(Months.ONE, Days.ONE, new DateTimeFormatterBuilder()
            .appendDayOfMonth(1)
            .appendLiteral(". ")
            .appendMonthOfYearShortText()
            .toFormatter(),
            "%Y-%m-%d",
            java.time.format.DateTimeFormatter.ISO_LOCAL_DATE),
    Jahr(Years.ONE, Months.ONE, new DateTimeFormatterBuilder()
            .appendMonthOfYearShortText()
            .appendLiteral(' ')
            .appendYear(2, 2)
            .toFormatter(),
            "%Y-%m",
            new java.time.format.DateTimeFormatterBuilder()
                    .appendValue(YEAR, 4)
                    .appendLiteral('-')
                    .appendValue(MONTH_OF_YEAR, 2)
                    .parseDefaulting(DAY_OF_MONTH, 1)
                    .toFormatter()),
    Gesamt(Years.years(10), Years.ONE, new DateTimeFormatterBuilder()
            .appendYear(4, 4)
            .toFormatter(),
            "%Y",
            new java.time.format.DateTimeFormatterBuilder()
                    .appendValue(YEAR, 4)
                    .parseDefaulting(MONTH_OF_YEAR, 1)
                    .parseDefaulting(DAY_OF_MONTH, 1)
                    .toFormatter());

    private final ReadablePeriod dauer;
    private final ReadablePeriod iteratorResolution;
    private final DateTimeFormatter formatter;
    private final String mongoAggregationPattern;
    private final java.time.format.DateTimeFormatter mongoAggregationPatternFormatter;

    ChartIntervall(ReadablePeriod dauer, ReadablePeriod iteratorResolution, DateTimeFormatter formatter,
                   String mongoAggregationPattern, java.time.format.DateTimeFormatter mongoAggregationPatternFormatter) {
        this.dauer = dauer;
        this.iteratorResolution = iteratorResolution;
        this.formatter = formatter;
        this.mongoAggregationPattern = mongoAggregationPattern;
        this.mongoAggregationPatternFormatter = mongoAggregationPatternFormatter;
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

    public String getMongoAggregationPattern() {
        return mongoAggregationPattern;
    }

    public LocalDate parseMongoAggregationDate(String date) {
        return LocalDate.parse(date, mongoAggregationPatternFormatter);
    }
}
