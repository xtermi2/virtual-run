package akeefer.web.charts.functions;

import com.googlecode.wickedcharts.highcharts.options.Function;

/**
 * Prozent und Killometer
 */
public class PercentageAndKmFormatter extends Function {

    private static final long serialVersionUID = 1L;

    public PercentageAndKmFormatter() {
        setFunction("return ''+ this.y +'km ('+ Math.round(this.percentage) +'%)';");
    }
}
