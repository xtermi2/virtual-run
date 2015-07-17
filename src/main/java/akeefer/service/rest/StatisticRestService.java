package akeefer.service.rest;

import akeefer.model.BenachrichtigunsIntervall;
import akeefer.service.PersonService;
import akeefer.web.WicketApplication;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.annotations.ResourcePath;
import org.wicketstuff.rest.resource.gson.GsonRestResource;
import org.wicketstuff.rest.utils.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;

@ResourcePath("/rest")
public class StatisticRestService extends GsonRestResource {

    private static final Logger logger = LoggerFactory.getLogger(StatisticRestService.class);

    @SpringBean
    private PersonService personService;

    public StatisticRestService() {
        Injector.get().inject(this);
    }

    @MethodMapping(value = "/statistic/{intervall}", httpMethod = HttpMethod.GET)
    public void get(String intervall) {
        try {
            boolean isLocalMode = WicketApplication.isLocalMode();
            HttpServletRequest request = ((HttpServletRequest) RequestCycle.get().getRequest().getContainerRequest());

            String appEngineCronFlag = request.getHeader("X-AppEngine-Cron");
            logger.info("X-AppEngine-Cron=" + appEngineCronFlag);

            if (isLocalMode || "true".equalsIgnoreCase(appEngineCronFlag)) {
                personService.sendStatisticMail(BenachrichtigunsIntervall.valueOf(intervall));
            } else {
                logger.warn("Request wird nicht ausgefuehrt, da er nicht lokal oder vom GAE-Cron kommt");
            }
        } catch (Exception e) {
            logger.warn("Fehler bei sendStatisticMail: " + e.getMessage(), e);
        }
    }
}
