package akeefer.web.pages;

import akeefer.web.components.MenuPanel;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Basis Page in der alle gemeinsamen Elemente Plaziert werden, z.B. css, menu, ...
 */
public abstract class AbstractBasePage extends WebPage {

    protected AbstractBasePage(PageParameters parameters, final boolean mapView, final boolean aktView) {
        super(parameters);

        add(new MenuPanel("menuPanel", mapView, aktView));
    }
}
