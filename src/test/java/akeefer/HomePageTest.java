package akeefer;

import org.apache.wicket.authroles.authentication.pages.SignInPage;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Simple test using the WicketTester
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testApplicationContext.xml"})
public class HomePageTest {

    private WicketTester tester;

    @Autowired
    private WicketApplication myWebApplication;

    @Before
    public void setUp() {
        tester = new WicketTester(myWebApplication);
    }

    @Test
    public void homepageRendersSuccessfully() {
        //start and render the test page
        tester.startPage(HomePage.class);

        tester.assertRenderedPage(SignInPage.class);

        FormTester formTester
                = tester.newFormTester("signInPanel:signInForm");
        formTester.setValue("username", "foo");
        formTester.setValue("password", "bar");
        formTester.submit();

        //assert rendered page class
        tester.assertRenderedPage(HomePage.class);
    }
}
