package akeefer.web.pages;

import akeefer.service.PersonService;
import akeefer.web.VRSession;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Andreas Keefer
 */
public class MapPageTotalDistanceAjaxBehavior extends AbstractDefaultAjaxBehavior {

    private static final Logger logger = LoggerFactory.getLogger(MapPageTotalDistanceAjaxBehavior.class);

    @SpringBean
    private PersonService personService;

    @Override
    protected void respond(AjaxRequestTarget target) {
        RequestCycle cycle = RequestCycle.get();
        WebRequest webRequest = (WebRequest) cycle.getRequest();
        StringValue distanceInMeter = webRequest.getQueryParameters().getParameterValue("distance");
        logger.info("TotalDistance={}m", distanceInMeter);
        BigDecimal totalDistanceInKm = BigDecimal.valueOf(distanceInMeter.toLong(0))
                .divide(BigDecimal.valueOf(1000), 3, RoundingMode.HALF_UP);
        VRSession.get().setTotalDistanceInKm(totalDistanceInKm);
        personService.updateTotalDistance(totalDistanceInKm);
    }

    @Override
    public CharSequence getCallbackScript() {
        String script = super.getCallbackScript().toString();
        script = script.replace("\"PLACEHOLDER1\"", "distanceValue");
        return script;
    }

    @Override
    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
        super.updateAjaxAttributes(attributes);
        attributes.getExtraParameters().put("distance", "PLACEHOLDER1");
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        StringBuilder script = new StringBuilder("function sendDistance(distanceInMeter) { ")
                .append("var distanceValue = distanceInMeter; ")
                .append(getCallbackScript())
                .append(" }");

        response.render(JavaScriptHeaderItem.forScript(script
                , "distanceCalculatedId"));
    }
}
