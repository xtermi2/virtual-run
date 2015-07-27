package akeefer.web.components;

import akeefer.model.User;
import akeefer.service.PersonService;
import akeefer.web.VRSession;
import akeefer.web.components.layout.Panel;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.common.collect.Sets;
import com.googlecode.wickedcharts.highcharts.options.*;
import com.googlecode.wickedcharts.highcharts.options.series.Coordinate;
import com.googlecode.wickedcharts.highcharts.options.series.CustomCoordinatesSeries;
import com.googlecode.wickedcharts.highcharts.options.series.Series;
import com.googlecode.wickedcharts.wicket6.highcharts.Chart;
import com.googlecode.wickedcharts.wicket6.highcharts.JsonRendererFactory;
import org.apache.commons.collections.MapUtils;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Andreas Keefer
 */
public class ForecastPanel extends Panel {

    static {
        JsonRendererFactory.getInstance().getRenderer().addSerializer(Zone.class, new JsonSerializer<Zone>() {
            @Override
            public void serialize(Zone value, JsonGenerator jgen, SerializerProvider serializers) throws IOException, JsonProcessingException {
                jgen.writeStartObject();

                if (null != value.getValue()) {
                    jgen.writeFieldName("value");
                    jgen.writeRawValue(value.getValue().toString());
                }

                if (null != value.getDashStyle()) {
                    jgen.writeStringField("dashStyle", value.getDashStyle());
                }

                jgen.writeEndObject();

            }
        });
    }

    private final static DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .appendLiteral("Date.UTC(")
            .appendYear(4, 4)
            .appendLiteral(", ")
            .appendMonthOfYear(1)
            .appendLiteral("-1, ")
            .appendDayOfMonth(1)
            .appendLiteral(')')
            .toFormatter();

    @SpringBean
    private PersonService personService;

    private final Chart chart;

    public ForecastPanel(String id) {
        super(id);

        // Create feedback panel and add to page
        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
        add(feedbackPanel.setFilter(new ContainerFeedbackMessageFilter(this))
                .setOutputMarkupId(true));

        // charts
        chart = new Chart("chart", new Options());
        add(chart);
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        chart.setOptions(createChartOptions());
    }

    private Options createChartOptions() {
        Set<User> users = Sets.newHashSet(VRSession.get().getUser());

        List<Series<?>> series = new ArrayList<>(users.size());
        BigDecimal totalDistanceInKm = VRSession.get().getTotalDistanceInKm();
        for (User user : users) {
            final Map<LocalDate, BigDecimal> forecastData = personService.createForecastData(
                    user.getUsername(), totalDistanceInKm);
            if (MapUtils.isNotEmpty(forecastData)) {
                final ZoneSeries<String, BigDecimal> serie = new ZoneSeries<>();
                series.add(serie);
                serie.setName(user.getAnzeigename());
                serie.setZoneAxis("x");
                serie.addZone(new Zone<String>()
                        .setValue(FORMATTER.print(LocalDate.now())));
                serie.addZone(new Zone<String>()
                        .setDashStyle("dot"));
                for (Map.Entry<LocalDate, BigDecimal> entry : forecastData.entrySet()) {
                    serie.addPoint(new Coordinate<>(FORMATTER.print(entry.getKey()), entry.getValue()));
                }
            }
        }

        // Build Options
        final Options options = new Options()
                .setChartOptions(new ChartOptions()
                        .setType(SeriesType.SPLINE)
                        .setZoomType(ZoomType.X))
                .setTitle(new Title(new StringResourceModel("statTitel", this, null).getString()))
                .setTooltip(new Tooltip()
                                .setHeaderFormat("<b>{series.name}</b><br>")
                                .setPointFormat("{point.x:%d.%m.%Y}: {point.y}km")
                )
                .setPlotOptions(new PlotOptionsChoice()
                                .setSpline(new PlotOptions()
                                        .setMarker(new Marker()
                                                .setEnabled(false)))
//                        .setArea(new PlotOptions()
//                                .setThreshold(null))
//                        .setAreaspline(new PlotOptions()
//                                .setThreshold(null))
                )
                .setxAxis(new Axis()
                        .setType(AxisType.DATETIME)
                        .setTitle(new Title(new StringResourceModel("xAxisTitle", this, null).getString())))
                .setyAxis(new Axis()
                                .setMin(0)
                                .setTitle(new Title(new StringResourceModel("yAxisTitle", this, null).getString()))
                )
                .setSeries(series);

        return options;
    }

    public static final class ZoneSeries<T, U> extends CustomCoordinatesSeries<T, U> {
        private static final long serialVersionUID = 1L;

        private String zoneAxis;
        private List<Zone<T>> zones;

        public String getZoneAxis() {
            return zoneAxis;
        }

        public ZoneSeries<T, U> setZoneAxis(String zoneAxis) {
            this.zoneAxis = zoneAxis;
            return this;
        }

        public List<Zone<T>> getZones() {
            return zones;
        }

        public ZoneSeries<T, U> setZones(List<Zone<T>> zones) {
            this.zones = zones;
            return this;
        }

        public ZoneSeries<T, U> addZone(Zone<T> zone) {
            if (null == zones) {
                zones = new ArrayList<>();
            }
            zones.add(zone);
            return this;
        }
    }

    public static final class Zone<D> implements Serializable {
        private static final long serialVersionUID = 1L;

        private D value;
        private String dashStyle;

        public D getValue() {
            return value;
        }

        public Zone<D> setValue(D value) {
            this.value = value;
            return this;
        }

        public String getDashStyle() {
            return dashStyle;
        }

        public Zone<D> setDashStyle(String dashStyle) {
            this.dashStyle = dashStyle;
            return this;
        }
    }
}
