package akeefer.web;

import akeefer.web.pages.*;
import com.googlecode.wickedcharts.wicket6.JavaScriptResourceRegistry;
import org.apache.wicket.ConverterLocator;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authentication.pages.SignInPage;
import org.apache.wicket.authroles.authentication.pages.SignOutPage;
import org.apache.wicket.bean.validation.BeanValidationConfiguration;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.pageStore.memory.IDataStoreEvictionStrategy;
import org.apache.wicket.pageStore.memory.PageNumberEvictionStrategy;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.convert.converter.BigDecimalConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.wicketstuff.gae.GaeApplication;
import org.wicketstuff.rest.utils.mounting.PackageScanner;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

/**
 * Application object for your web application.
 * If you want to run this application without deploying, run the Start class.
 *
 * @see akeefer.Start#main(String[])
 */
@Component
public class WicketApplication extends AuthenticatedWebApplication implements ApplicationContextAware, GaeApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(WicketApplication.class);
    private final boolean isProd =
            "Production".equalsIgnoreCase(
                    System.getProperty("com.google.appengine.runtime.environment"));

    private ApplicationContext ctx;

    @Override
    public Class<? extends WebPage> getHomePage() {
        return MapPage.class;
    }

    @Override
    public void init() {
        super.init();
        if (null == ctx) {
            // im Produktiv Fall
            LOGGER.info("init Spring without Context");
            getComponentInstantiationListeners().add(new SpringComponentInjector(this));
        } else {
            // In Unittest Fall
            LOGGER.info("init Spring with Context");
            getComponentInstantiationListeners().add(new SpringComponentInjector(this, ctx, true));
        }

        // Bean Validation
        BeanValidationConfiguration beanValidationConfiguration = new BeanValidationConfiguration();
        beanValidationConfiguration.register(NotNull.class, new RequiredTagModifier());
        beanValidationConfiguration.configure(this);

        mountPage("/login", SignInPage.class);
        mountPage("/logout", SignOutPage.class);
        mountPage("/create", AktEditPage.class);
        mountPage("/uebersicht", AktUebersichtPage.class);
        mountPage("/init", InitDatabasePage.class);
        mountPage("/features", FeaturePage.class);
        mountPage("/impressum", ImpressumPage.class);
        mountPage("/user", UserDetailsPage.class);
        mountPage("/statistic", StatisticPage.class);

        getHeaderContributorListenerCollection().add(new IHeaderContributor() {
            @Override
            public void renderHead(IHeaderResponse response) {
                response.render(JavaScriptReferenceHeaderItem.forReference(
                        new com.jquery.JQueryResourceReference(com.jquery.JQueryResourceReference.Version.V1_6_3)));
            }
        });

        PackageScanner.scanPackage("akeefer.service.rest");

        JavaScriptResourceRegistry.getInstance().setHighchartsReference("highcharts-3.0.2/highcharts.js");
        JavaScriptResourceRegistry.getInstance().setHighchartsExportingReference("highcharts-3.0.2/exporting.js");
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

    @Override
    protected IConverterLocator newConverterLocator() {
        ConverterLocator converterLocator = new ConverterLocator();
        converterLocator.set(BigDecimal.class, new BigDecimalConverter() {
            @Override
            public BigDecimal convertToObject(String value, Locale locale) {
                LOGGER.info(String.format("BigDecimalConverter#convertToObject(value='%s', locale='%s')", value, locale));
                // NB: this isn't universal & your mileage problably varies!
                // (Specifically, this breaks if '.' is used as thousands separator)
                value = value.replace('.', ',');
                // die Locale wird hart auf Germany gesetzt, da andere Locales anders parsen (z.B. Englisch)
                // und dann anstann ein , einen . erwarten und dann ein seltsames ergebnis vom parser zurueck kommt
                // Beispiel: Locale=ENGLISH value 1,6 wird zu 16; Da hätte ich eigentlich einen Parsingfehler erwartet, anstatt das ,
                // einfach zu entfernen
                BigDecimal bigDecimal = super.convertToObject(value, Locale.GERMANY);
                return bigDecimal.setScale(3, RoundingMode.HALF_UP);
            }
        });
        return converterLocator;
    }

    @Override
    public RuntimeConfigurationType getConfigurationType() {
        return isProd ? RuntimeConfigurationType.DEPLOYMENT : RuntimeConfigurationType.DEVELOPMENT;
    }

    @Override
    public IDataStoreEvictionStrategy getEvictionStrategy() {
        return new PageNumberEvictionStrategy(5);
    }
}
