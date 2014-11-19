package akeefer;

import akeefer.model.User;
import akeefer.service.PersonService;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.Request;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class VRSession extends AuthenticatedWebSession {

    private User user;

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
        try {
            user = personService.getUserByUsername(username);
        } catch (RuntimeException e) {
            return false;
        }
        if (null != user) {
            // TODO (ak) auf HASH umstellen
            if (password.equals(user.getPassword())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Roles getRoles() {
        Roles roles = new Roles();
        if (isSignedIn()) {
            roles.add(user.getRole().name());
        }
        return roles;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
