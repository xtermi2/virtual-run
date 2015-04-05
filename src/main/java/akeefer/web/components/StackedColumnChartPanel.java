package akeefer.web.components;

import akeefer.model.AktivitaetsTyp;
import akeefer.service.PersonService;
import akeefer.web.VRSession;
import akeefer.web.charts.functions.StackTotalKmFormatter;
import akeefer.web.components.layout.Panel;
import akeefer.web.components.validation.LocalizedPropertyValidator;
import com.googlecode.wickedcharts.highcharts.options.*;
import com.googlecode.wickedcharts.highcharts.options.series.Series;
import com.googlecode.wickedcharts.highcharts.options.series.SimpleSeries;
import com.googlecode.wickedcharts.wicket6.highcharts.Chart;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

public class StackedColumnChartPanel extends Panel {

    private static final Logger logger = LoggerFactory.getLogger(StackedColumnChartPanel.class);

    @SpringBean
    private PersonService personService;

    private ChartIntervall chartIntervall = ChartIntervall.Woche;

    private final Chart chart;

    public StackedColumnChartPanel(String id) {
        super(id);

        // Create feedback panel and add to page
        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
        add(feedbackPanel.setFilter(new ContainerFeedbackMessageFilter(this))
                .setOutputMarkupId(true));

        Form<StackedColumnChartPanel> form = new Form<StackedColumnChartPanel>("form", new CompoundPropertyModel<StackedColumnChartPanel>(this)) {
            @Override
            protected void onSubmit() {
            }
        };
        add(form);

        final DropDownChoice<ChartIntervall> chartIntervall = new DropDownChoice<ChartIntervall>("chartIntervall",
                Arrays.asList(ChartIntervall.values())) {
            @Override
            protected CharSequence getDefaultChoice(String selectedValue) {
                // Dadurch kommt die "Bitte Waehlen" auswahl nicht
                return "";
            }
        };
        chartIntervall.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                chart.setOptions(createChartOptions());
                target.add(chart);
                target.add(feedbackPanel);
            }
        });
        form.add(chartIntervall.add(new LocalizedPropertyValidator<ChartIntervall>()));
        form.add(new FormComponentLabel("chartIntervallLabel", chartIntervall));

        // charts
        chart = new Chart("chart", new Options());
        add(chart);
    }

    private Options createChartOptions() {
        Map<Interval, Map<AktivitaetsTyp, BigDecimal>> data = personService.createStackedColumsChartData(
                VRSession.get().getUser().getId(), chartIntervall);
        List<String> xCategories = new ArrayList<>();
        final Options options = new Options();
        Map<AktivitaetsTyp, SimpleSeries> seriesMap = new HashMap<>();
        int i = 0;
        for (Map.Entry<Interval, Map<AktivitaetsTyp, BigDecimal>> entry : data.entrySet()) {
            String beschreibungZeit = chartIntervall.getBeschreibung(entry.getKey());
            xCategories.add(beschreibungZeit);
            for (Map.Entry<AktivitaetsTyp, BigDecimal> entryResolution : entry.getValue().entrySet()) {
                SimpleSeries series = seriesMap.get(entryResolution.getKey());
                if (null == series) {
                    series = new SimpleSeries();
                    series.setName(entryResolution.getKey().toVergangenheit());
                    seriesMap.put(entryResolution.getKey(), series);
                    initSeries(series, data.size());
                }
                series.getData().set(i, entryResolution.getValue());
            }
            i++;
        }

        options.setSeries(new ArrayList<Series<?>>(seriesMap.values()));
        options.setChartOptions(new ChartOptions().setType(SeriesType.COLUMN))
                .setTitle(new Title(new StringResourceModel("statTitel", this, null).getString()))
                .setTooltip(new Tooltip()
                                .setFormatter(new StackTotalKmFormatter())
                )
        ;
        options.setxAxis(new Axis()
                .setCategories(xCategories));
        options.setyAxis(new Axis()
                .setMin(0)
                .setTitle(new Title(new StringResourceModel("statGesamtKm", this, null).getString()))
                .setStackLabels(new StackLabels()
                                .setEnabled(Boolean.TRUE)
                ));
        options.setLegend(new Legend()
                .setAlign(HorizontalAlignment.RIGHT)
                .setX(-100)
                .setVerticalAlign(VerticalAlignment.TOP)
                .setY(20)
                .setFloating(Boolean.TRUE)
                .setBorderWidth(1)
                .setShadow(Boolean.FALSE));
        options.setPlotOptions(new PlotOptionsChoice()
                .setColumn(new PlotOptions()
                        .setStacking(Stacking.NORMAL)
                        .setDataLabels(new DataLabels()
                                        .setEnabled(Boolean.TRUE)
                        )));
        return options;
    }

    private void initSeries(SimpleSeries pointSeries, int size) {
        ArrayList<Number> data = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            data.add(BigDecimal.ZERO);
        }
        pointSeries.setData(data);
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        chart.setOptions(createChartOptions());
    }

    public static enum ChartIntervall {
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

        public ReadablePeriod getDauer() {
            return dauer;
        }

        public ReadablePeriod getIteratorResolution() {
            return iteratorResolution;
        }

        public String getBeschreibung(Interval interval) {
            return formatter.print(interval.getStart());
        }
    }
}
