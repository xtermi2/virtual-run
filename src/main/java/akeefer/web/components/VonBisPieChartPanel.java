package akeefer.web.components;

import akeefer.model.AktivitaetsTyp;
import akeefer.service.PersonService;
import akeefer.web.VRSession;
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
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

public class VonBisPieChartPanel extends Panel {

    @SpringBean
    private PersonService personService;

    private Date von = new LocalDate().minusWeeks(1).toDate();
    private Date bis = new LocalDate().toDate();
    private final Chart chartPie;

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
        DateTextField vonDatum = new DateTextField("von", "dd.MM.yyyy");
        form.add(vonDatum.add(datePickerVon).add(new LocalizedPropertyValidator<>()));
        form.add(new FormComponentLabel("vonDatumLabel", vonDatum));

        DatePicker datePickerBis = new DatePicker();
        datePickerBis.setShowOnFieldClick(true);
        datePickerBis.setAutoHide(true);
        DateTextField bisDatum = new DateTextField("bis", "dd.MM.yyyy");
        form.add(bisDatum.add(datePickerBis).add(new LocalizedPropertyValidator<>()));
        form.add(new FormComponentLabel("bisDatumLabel", bisDatum));

        // charts
        chartPie = new Chart("chartPie", new Options());
        add(chartPie);

        form.add(new AjaxFallbackButton("submit", form) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (null != target) {
                    chartPie.setOptions(createChartPieOptions());
                    target.add(chartPie);
                    target.add(feedbackPanel);
                }
            }
        });
    }

    private Options createChartPieOptions() {
        Map<AktivitaetsTyp, BigDecimal> data = personService.createPieChartData(VRSession.get().getUser().getId(),
                new LocalDate(von), new LocalDate(bis));
        PointSeries pointSeries = new PointSeries();
        for (Map.Entry<AktivitaetsTyp, BigDecimal> dataEntry : data.entrySet()) {
            pointSeries.addPoint(new Point(dataEntry.getKey().toVergangenheit(), dataEntry.getValue())
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
                        .setName(new StringResourceModel("statPieSeriesTitle", this, null).getString()))
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
        chartPie.setOptions(createChartPieOptions());
    }
}
