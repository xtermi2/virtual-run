package akeefer.web.pages;

import akeefer.model.*;
import akeefer.service.PersonService;
import akeefer.web.WicketApplication;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class InitDatabasePage extends AbstractBasePage {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(InitDatabasePage.class);

    @SpringBean
    private PersonService personService;

    public InitDatabasePage(final PageParameters parameters) {
        super(parameters);
        List<User> users = new ArrayList<>();
        users.add(create("andi", SecurityRole.ADMIN, SecurityRole.USER));
        users.add(create("frank", SecurityRole.USER));
        users.add(create("sabine", SecurityRole.USER));
        users.add(create("roland", SecurityRole.USER));
        users.add(create("norbert", SecurityRole.USER));
        users.add(create("uli-hans", SecurityRole.USER));

        if (WicketApplication.isLocalMode()) {
            // Aktivitaeten anlegen
            Random random = new Random();
            for (int userIndex = 0; userIndex < users.size(); userIndex++) {
                final User user = users.get(userIndex);
                final int anzahlAktivitaeten = userIndex * 5;
                logger.info("creating " + anzahlAktivitaeten + " Aktivitaeten for user " + user.getUsername());
                for (int i = 0; i < anzahlAktivitaeten; i++) {
                    Aktivitaet akt = new Aktivitaet();
                    akt.setDistanzInMeter(random.nextInt(10000));
                    akt.setTyp(AktivitaetsTyp.laufen);
                    akt.setAktivitaetsDatum(new Date());
                    akt.setBezeichnung(RandomStringUtils.randomAlphabetic(10));
                    akt.setOwner(user.getUsername());
                    akt.setAufzeichnungsart(AktivitaetsAufzeichnung.aufgezeichnet);
                    personService.createAktivitaet(akt, user);
                }
            }
        }

        // Startseite anzeigen
        setResponsePage(MapPage.class);
    }

    private User create(String username, SecurityRole... roles) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(username);
        user.getRoles().addAll(Arrays.asList(roles));
        return personService.createUserIfAbsent(user);
    }
}
