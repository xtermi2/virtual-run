package akeefer;

import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authentication.pages.SignInPage;
import org.apache.wicket.authroles.authentication.pages.SignOutPage;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Application object for your web application.
 * If you want to run this application without deploying, run the Start class.
 *
 * @see akeefer.Start#main(String[])
 */
@Component
public class WicketApplication extends AuthenticatedWebApplication implements ApplicationContextAware {

    private ApplicationContext ctx;

    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    @Override
    public Class<? extends WebPage> getHomePage() {
        return HomePage.class;
    }

    /**
     * @see org.apache.wicket.Application#init()
     */
    @Override
    public void init() {
        super.init();
        if (null == ctx) {
            // im Produktiv Fall
            getComponentInstantiationListeners().add(new SpringComponentInjector(this));
        } else {
            // In Unittest Fall
            getComponentInstantiationListeners().add(new SpringComponentInjector(this, ctx, true));
        }

        mountPage("/login", SignInPage.class);
        mountPage("/logout", SignOutPage.class);
        mountPage("/create", CreateAktPage.class);
        mountPage("/newAkt", AktEditPage.class);
        mountPage("/init", InitDatabasePage.class);
    }

    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return VRSession.class;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return SignInPage.class;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}
