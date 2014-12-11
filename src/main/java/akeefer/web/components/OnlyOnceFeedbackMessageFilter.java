package akeefer.web.components;

import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;

public class OnlyOnceFeedbackMessageFilter implements IFeedbackMessageFilter {

    public static final OnlyOnceFeedbackMessageFilter INSTANCE = new OnlyOnceFeedbackMessageFilter();

    @Override
    public boolean accept(FeedbackMessage message) {
        return !message.isRendered();
    }
}
