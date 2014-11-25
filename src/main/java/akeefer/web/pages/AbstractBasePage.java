package akeefer.web.pages;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;

/**
 * Basis Page in der alle gemeinsamen Elemente Plaziert werden, z.B. css, ...
 */
public abstract class AbstractBasePage extends WebPage {

    public static final CssResourceReference CSS_BASE_REFERENCE = new CssResourceReference(AbstractBasePage.class, "AbstractBasePage.css");

    protected AbstractBasePage(PageParameters parameters) {
        super(parameters);

        add(new Link("impressumLink") {
            @Override
            public void onClick() {
                setResponsePage(ImpressumPage.class);
            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(CSS_BASE_REFERENCE));
    }
}
