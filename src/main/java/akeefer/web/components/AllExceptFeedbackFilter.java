package akeefer.web.components;

import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filter, der nur dann Messages akzeptiert, wenn kein anderer Filter sie akzeptiert.
 */
public abstract class AllExceptFeedbackFilter implements IFeedbackMessageFilter {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(AllExceptFeedbackFilter.class);

    private transient Iterable<IFeedbackMessageFilter> filters = null;

    public AllExceptFeedbackFilter() {
    }

    @Override
    public boolean accept(FeedbackMessage message) {
        Iterable<IFeedbackMessageFilter> localFilters = getFilters();
        if (null != localFilters) {
            for (IFeedbackMessageFilter filter : localFilters) {
                boolean accept = filter.accept(message);
                if (logger.isDebugEnabled()) {
                    logger.debug("Filter " + filter + " accept " + message + ": " + accept);
                }
                if (accept) {
                    return false;
                }
            }
        }
        return true;
    }

    private Iterable<IFeedbackMessageFilter> getFilters() {
        if (null == filters) {
            this.filters = loadFilters();
        }
        return filters;
    }

    protected abstract Iterable<IFeedbackMessageFilter> loadFilters();
}
