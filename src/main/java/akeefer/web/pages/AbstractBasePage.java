package akeefer.web.pages;

import akeefer.web.components.MenuPanel;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Basis Page in der alle gemeinsamen Elemente Plaziert werden, z.B. css, ...
 */
public abstract class AbstractBasePage extends WebPage {

    protected AbstractBasePage(PageParameters parameters) {
        super(parameters);

        add(new Link("impressumLink") {
            @Override
            public void onClick() {
                setResponsePage(ImpressumPage.class);
            }
        });
    }
}
