package akeefer.web.pages;

import akeefer.web.components.AllExceptFeedbackFilter;
import akeefer.web.components.MenuPanel;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Basis Page in der alle gemeinsamen Elemente Plaziert werden menu, ...
 */
@AuthorizeInstantiation("USER")
public abstract class AbstractAuthenticatedBasePage extends AbstractBasePage {

    private static final Logger logger = LoggerFactory.getLogger(AbstractAuthenticatedBasePage.class);
    private final FeedbackPanel pageFeedbackPanel;

    protected AbstractAuthenticatedBasePage(PageParameters parameters,
                                            final boolean mapView,
                                            final boolean aktView,
                                            final boolean userView,
                                            final boolean statisticView) {
        super(parameters);

        add(new MenuPanel("menuPanel", mapView, aktView, userView, statisticView));
        pageFeedbackPanel = new FeedbackPanel("feedback");
        pageFeedbackPanel.setOutputMarkupId(true);
        // "Fallback"-Filter, der nur reportet, wenn kein anderer Filter eines FeedbackPanel zustaendig ist
        add(pageFeedbackPanel.setFilter(new AllExceptFeedbackFilter() {

            @Override
            protected Iterable<IFeedbackMessageFilter> loadFilters() {
                if (logger.isDebugEnabled()) {
                    logger.debug("loadFilters...");
                }
                final List<IFeedbackMessageFilter> filters = new ArrayList<>();
                // suche nach allen FeedbackPanel und deren Filter
                getPage().visitChildren(FeedbackPanel.class, new IVisitor<FeedbackPanel, Void>() {

                    @Override
                    public void component(FeedbackPanel component, IVisit<Void> visit) {
                        if (!pageFeedbackPanel.equals(component)) {
                            IFeedbackMessageFilter filter = component.getFilter();
                            if (null != filter) {
                                if (logger.isDebugEnabled()) {
                                    logger.debug("add filter " + filter + " of component " + component + "(parent=" + component.getParent() + ")");
                                }
                                filters.add(filter);
                            } else {
                                // wenn kein Filter vorhanden ist, dass gehen wir von einem "AcceptAll" aus
                                if (logger.isDebugEnabled()) {
                                    logger.debug("component " + component + "(parent=" + component.getParent() + ") hat keinen Filter, wir nehmen den ALL-Filter");
                                }
                                filters.add(IFeedbackMessageFilter.ALL);
                            }
                        }
                    }
                });
                if (logger.isDebugEnabled()) {
                    logger.debug(filters.size() + " filter gefunden");
                }
                return filters;
            }
        }));
    }

    protected FeedbackPanel getPageFeedbackPanel() {
        return pageFeedbackPanel;
    }
}
