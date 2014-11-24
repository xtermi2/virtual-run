package akeefer.web;

import akeefer.model.User;
import akeefer.service.PersonService;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.Request;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class VRSession extends AuthenticatedWebSession {

    private IModel<User> user = new Model<User>();

    @SpringBean
    private PersonService personService;

    public static VRSession get() {
        return (VRSession) Session.get();
    }

    public VRSession(Request request) {
        super(request);
        Injector.get().inject(this);
    }

    @Override
    public boolean authenticate(String username, String password) {
        User userTmp = null;
        try {
            userTmp = personService.getUserByUsername(username);
        } catch (RuntimeException e) {
            return false;
        }
        if (null != userTmp) {
            // TODO (ak) auf HASH umstellen
            if (password.equals(userTmp.getPassword())) {
                this.user.setObject(userTmp);
                return true;
            }
        }

        return false;
    }

    @Override
    public Roles getRoles() {
        Roles roles = new Roles();
        if (isSignedIn()) {
            roles.add(user.getObject().getRole().name());
        }
        return roles;
    }

    public IModel<User> getUser() {
        return user;
    }
}
