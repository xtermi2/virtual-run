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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class VRSession extends AuthenticatedWebSession {

    private static final Logger logger = LoggerFactory.getLogger(VRSession.class);

    private IModel<User> user = new Model<User>();

    private transient HttpSession httpSession;

    @SpringBean
    private PersonService personService;

    @SpringBean(name = "authenticationManager")
    private AuthenticationManager authenticationManager;

    public static VRSession get() {
        return (VRSession) Session.get();
    }

    public VRSession(Request request) {
        super(request);
        this.httpSession = ((HttpServletRequest) request.getContainerRequest()).getSession();
        Injector.get().inject(this);
    }

    @Override
    public boolean authenticate(String username, String password) {
        boolean authenticated = false;
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            authenticated = authentication.isAuthenticated();
            if (authenticated) {
                httpSession.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                        SecurityContextHolder.getContext());
                user.setObject(personService.getUserByUsername(username));
            }
        } catch (RuntimeException e) {
            logger.warn(String.format("User '%s' failed to login. Reason: %s", username, e.getMessage()));
            authenticated = false;
        }

        return authenticated;
    }

    @Override
    public Roles getRoles() {
        Roles roles = new Roles();
        getRolesIfSignedIn(roles);
        return roles;
    }

    private void getRolesIfSignedIn(Roles roles) {
        if (isSignedIn()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            addRolesFromAuthentication(roles, authentication);
        }
    }

    private void addRolesFromAuthentication(Roles roles, Authentication authentication) {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            roles.add(authority.getAuthority());
        }
    }

    public IModel<User> getUserModel() {
        return user;
    }

    public User getUser() {
        return user.getObject();
    }
}
