package akeefer.web.pages;

import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Sammlung von Features, die kommen, vielleicht
 */
public class FeaturePage extends AbstractAuthenticatedBasePage {

    public FeaturePage(PageParameters parameters) {
        super(parameters, false, false, false, false);
    }
}
