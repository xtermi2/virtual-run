package akeefer.web.components;

import akeefer.model.AktivitaetsTyp;
import akeefer.service.PersonService;
import akeefer.web.VRSession;
import akeefer.web.charts.ChartIntervall;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
                series.getData().set(i, entryResolution.getValue().setScale(3, RoundingMode.HALF_UP));
            }
            i++;
        }

        final Options options = new Options()
                .setChartOptions(new ChartOptions().setType(SeriesType.COLUMN))
                .setTitle(new Title(new StringResourceModel("statTitel", this, null).getString()))
                .setTooltip(new Tooltip()
                                .setFormatter(new StackTotalKmFormatter())
                )
                .setPlotOptions(new PlotOptionsChoice()
                        .setColumn(new PlotOptions()
                                .setStacking(Stacking.NORMAL)
                                .setDataLabels(new DataLabels()
                                                .setEnabled(true)
                                )))
                .setxAxis(new Axis()
                        .setCategories(xCategories))
                .setyAxis(new Axis()
                        .setMin(new BigDecimal("0.001"))
                        .setTitle(new Title(new StringResourceModel("statGesamtKm", this, null).getString()))
                        .setStackLabels(new StackLabels()
                                        .setEnabled(true)
                        ))
                .setSeries(new ArrayList<Series<?>>(seriesMap.values()));
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

}
