package akeefer.web.pages;

import akeefer.web.components.StackedColumnChartPanel;
import akeefer.web.components.VonBisPieChartPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class StatisticPage extends AbstractAuthenticatedBasePage {


    public StatisticPage(final PageParameters parameters) {
        super(parameters, false, false, false, true);

        add(new VonBisPieChartPanel("vonBisPieChartPanel"));
        add(new StackedColumnChartPanel("stackedColumnChartPanel"));
    }


}
