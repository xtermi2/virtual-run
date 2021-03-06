package akeefer.web.pages;

import akeefer.web.components.ForecastPanel;
import akeefer.web.components.StackedColumnChartPanel;
import akeefer.web.components.VonBisPieChartPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class StatisticPage extends AbstractAuthenticatedBasePage {


    public StatisticPage(PageParameters parameters) {
        super(parameters, false, false, false, true);

        add(new ForecastPanel("forecast"));
        add(new VonBisPieChartPanel("vonBisPieChartPanel"));
        add(new StackedColumnChartPanel("stackedColumnChartPanel"));
    }


}
