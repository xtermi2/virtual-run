package akeefer.web.charts.functions;

import com.googlecode.wickedcharts.highcharts.options.Function;

/**
 * StackTotalFormatter mit Killometern
 */
public class StackTotalKmFormatter extends Function {

    private static final long serialVersionUID = 1L;

    public StackTotalKmFormatter() {
        setFunction("return '<b>'+ this.x +'</b><br/>'+this.series.name +': '+ this.y +'km<br/>'" +
                "+'Total: '+ this.point.stackTotal + 'km';");
    }
}
