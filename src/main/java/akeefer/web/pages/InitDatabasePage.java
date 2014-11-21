package akeefer.web.pages;

import akeefer.model.SecurityRole;
import akeefer.model.User;
import akeefer.service.PersonService;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class InitDatabasePage extends AbstractBasePage {

    private static final long serialVersionUID = 1L;

    @SpringBean
    private PersonService personService;

    public InitDatabasePage(final PageParameters parameters) {
        super(parameters);
        create("andi");
        create("sabine");
        create("roland");
        create("norbert");
        create("uli-hans");

        // Startseite anzeigen
        setResponsePage(MapPage.class);
    }

    private void create(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(username);
        user.setRole(SecurityRole.USER);
        personService.createUserIfAbsent(user);
    }
}
