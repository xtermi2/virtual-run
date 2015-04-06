package akeefer.web.components;

import akeefer.model.AktivitaetsTyp;
import akeefer.service.PersonService;
import akeefer.web.VRSession;
import akeefer.web.charts.ChartIntervall;
import akeefer.web.charts.functions.PercentageAndKmFormatter;
import akeefer.web.components.layout.Panel;
import akeefer.web.components.validation.LocalizedPropertyValidator;
import com.googlecode.wickedcharts.highcharts.options.*;
import com.googlecode.wickedcharts.highcharts.options.color.HighchartsColor;
import com.googlecode.wickedcharts.highcharts.options.color.NullColor;
import com.googlecode.wickedcharts.highcharts.options.color.RadialGradient;
import com.googlecode.wickedcharts.highcharts.options.series.Point;
import com.googlecode.wickedcharts.highcharts.options.series.PointSeries;
import com.googlecode.wickedcharts.wicket6.highcharts.Chart;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

public class VonBisPieChartPanel extends Panel {

    @SpringBean
    private PersonService personService;

    private ChartIntervall chartIntervall = ChartIntervall.Woche;
    private Date von = chartIntervall.getIntervall().getStart().toDate();
    private Date bis = chartIntervall.getIntervall().getEnd().toDate();
    private final Chart chart;

    public VonBisPieChartPanel(String id) {
        super(id);

        // Create feedback panel and add to page
        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
        add(feedbackPanel.setFilter(new ContainerFeedbackMessageFilter(this))
                .setOutputMarkupId(true));

        Form<VonBisPieChartPanel> form = new Form<VonBisPieChartPanel>("form", new CompoundPropertyModel<VonBisPieChartPanel>(this)) {
            @Override
            protected void onSubmit() {
            }
        };
        add(form);

        DatePicker datePickerVon = new DatePicker();
        datePickerVon.setShowOnFieldClick(true);
        datePickerVon.setAutoHide(true);
        final DateTextField vonDatum = new DateTextField("von", "dd.MM.yyyy");
        form.add(vonDatum.add(datePickerVon).add(new LocalizedPropertyValidator<>()));
        form.add(new FormComponentLabel("vonDatumLabel", vonDatum));

        DatePicker datePickerBis = new DatePicker();
        datePickerBis.setShowOnFieldClick(true);
        datePickerBis.setAutoHide(true);
        final DateTextField bisDatum = new DateTextField("bis", "dd.MM.yyyy");
        form.add(bisDatum.add(datePickerBis).add(new LocalizedPropertyValidator<>()));
        form.add(new FormComponentLabel("bisDatumLabel", bisDatum));

        final DropDownChoice<ChartIntervall> chartIntervallDropDown = new DropDownChoice<ChartIntervall>("chartIntervall",
                Arrays.asList(ChartIntervall.values())) {
            @Override
            protected CharSequence getDefaultChoice(String selectedValue) {
                // Dadurch kommt die "Bitte Waehlen" auswahl nicht
                return "";
            }
        };
        chartIntervallDropDown.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                Interval interval = chartIntervall.getIntervall();
                von = interval.getStart().toDate();
                bis = interval.getEnd().toDate();

                chart.setOptions(createChartOptions());

                target.add(vonDatum);
                target.add(bisDatum);
                target.add(chart);
                target.add(feedbackPanel);
            }
        });
        form.add(chartIntervallDropDown.add(new LocalizedPropertyValidator<ChartIntervall>()));
        form.add(new FormComponentLabel("chartIntervallLabel", chartIntervallDropDown));

        // charts
        chart = new Chart("chart", new Options());
        add(chart);

        form.add(new AjaxFallbackButton("submit", form) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (null != target) {
                    chart.setOptions(createChartOptions());
                    target.add(chart);
                    target.add(feedbackPanel);
                }
            }
        });
    }

    private Options createChartOptions() {
        Map<AktivitaetsTyp, BigDecimal> data = personService.createPieChartData(VRSession.get().getUser().getId(),
                new LocalDate(von), new LocalDate(bis));
        PointSeries pointSeries = new PointSeries();
        for (Map.Entry<AktivitaetsTyp, BigDecimal> dataEntry : data.entrySet()) {
            pointSeries.addPoint(new Point(dataEntry.getKey().toVergangenheit(), dataEntry.getValue().setScale(3, RoundingMode.HALF_UP))
                    .setColor(new RadialGradient()
                            .setCx(0.5)
                            .setCy(0.3)
                            .setR(0.7)
                            .addStop(0, new HighchartsColor(dataEntry.getKey().ordinal()))
                            .addStop(1, new HighchartsColor(dataEntry.getKey().ordinal())
                                    .brighten(-0.3f))));
        }

        final Options options = new Options()
                .setChartOptions(new ChartOptions().setType(SeriesType.PIE))
                .setTitle(new Title(new StringResourceModel("statPieTitel", this, null).getString()))
                .addSeries(pointSeries
                                .setType(SeriesType.PIE)
                                .setName(new StringResourceModel("statPieSeriesTitle", this, null).getString())
                )
                .setChartOptions(new ChartOptions()
                        .setPlotBackgroundColor(new NullColor())
                        .setPlotBorderWidth(null)
                        .setPlotShadow(false))
                .setTooltip(new Tooltip()
                        .setFormatter(new PercentageAndKmFormatter())
                        .setPercentageDecimals(2))
                .setPlotOptions(new PlotOptionsChoice()
                        .setPie(new PlotOptions()
                                .setAllowPointSelect(true)
                                .setCursor(Cursor.POINTER)));
        return options;
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        chart.setOptions(createChartOptions());
    }
}
