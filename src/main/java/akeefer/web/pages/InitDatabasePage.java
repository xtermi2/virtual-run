package akeefer.web.pages;

import akeefer.model.SecurityRole;
import akeefer.model.User;
import akeefer.service.PersonService;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Arrays;

public class InitDatabasePage extends AbstractBasePage {

    private static final long serialVersionUID = 1L;

    @SpringBean
    private PersonService personService;

    public InitDatabasePage(final PageParameters parameters) {
        super(parameters);
        create("andi", SecurityRole.ADMIN, SecurityRole.USER);
        create("frank", SecurityRole.USER);
        create("sabine", SecurityRole.USER);
        create("roland", SecurityRole.USER);
        create("norbert", SecurityRole.USER);
        create("uli-hans", SecurityRole.USER);

        // Startseite anzeigen
        setResponsePage(MapPage.class);
    }

    private void create(String username, SecurityRole... roles) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(username);
        user.getRoles().addAll(Arrays.asList(roles));
        personService.createUserIfAbsent(user);
    }
}
