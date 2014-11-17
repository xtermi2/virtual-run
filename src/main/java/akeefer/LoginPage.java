package akeefer;

import org.apache.wicket.authroles.authentication.panel.SignInPanel;
import org.apache.wicket.markup.html.WebPage;

/**
 * Created by akeefer on 17.11.14.
 */
public class LoginPage extends WebPage {
    public LoginPage() {
        add(new SignInPanel("signInPanel"));
    }
}
