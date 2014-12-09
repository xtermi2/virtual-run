package akeefer.web.pages;

import akeefer.service.PersonService;
import akeefer.web.VRSession;
import akeefer.web.components.PasswordEditPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class UserDetailsPage extends AbstractAuthenticatedBasePage {

    @SpringBean
    private PersonService personService;

    public UserDetailsPage(final PageParameters parameters) {
        super(parameters, false, false, true);

        add(new PasswordEditPanel("passwordEditPanel") {
            @Override
            public void onPasswordChange(IModel<String> password) {
                personService.changePassword(VRSession.get().getUser().getId(), password.getObject());
                getSession().info(getString("passwordChangedSuccess"));
            }
        });
    }
}
