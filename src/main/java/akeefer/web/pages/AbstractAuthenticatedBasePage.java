package akeefer.web.pages;

import akeefer.web.components.MenuPanel;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Basis Page in der alle gemeinsamen Elemente Plaziert werden menu, ...
 */
@AuthorizeInstantiation("USER")
public abstract class AbstractAuthenticatedBasePage extends AbstractBasePage {

    protected AbstractAuthenticatedBasePage(PageParameters parameters, final boolean mapView, final boolean aktView) {
        super(parameters);

        add(new MenuPanel("menuPanel", mapView, aktView));
    }
}
