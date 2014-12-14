package akeefer.service.rest;

import akeefer.model.BenachrichtigunsIntervall;
import akeefer.service.PersonService;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.annotations.ResourcePath;
import org.wicketstuff.rest.resource.gson.GsonRestResource;
import org.wicketstuff.rest.utils.http.HttpMethod;

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
            personService.sendStatisticMail(BenachrichtigunsIntervall.valueOf(intervall));
        } catch (Exception e) {
            logger.warn("Fehler bei sendStatisticMail: " + e.getMessage(), e);
        }
    }

//    @MethodMapping(value = "/taeglich", httpMethod = HttpMethod.GET)
//    public void taeglich() {
//        try {
//            personService.sendStatisticMail(BenachrichtigunsIntervall.taeglich);
//        } catch (Exception e) {
//            logger.warn("Fehler bei sendStatisticMail: " + e.getMessage(), e);
//        }
//    }
//
//    @MethodMapping(value = "/woechnetlich", httpMethod = HttpMethod.GET)
//    public void woechnetlich() {
//        try {
//            personService.sendStatisticMail(BenachrichtigunsIntervall.woechnetlich);
//        } catch (Exception e) {
//            logger.warn("Fehler bei sendStatisticMail: " + e.getMessage(), e);
//        }
//    }
}
