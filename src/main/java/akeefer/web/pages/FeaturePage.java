package akeefer.web.pages;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Sammlung von Features, die kommen, vielleicht
 */
@AuthorizeInstantiation("USER")
public class FeaturePage extends WebPage {

    public FeaturePage(final PageParameters parameters) {

    }
}
