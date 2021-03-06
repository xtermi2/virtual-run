package akeefer.web.pages;

import org.apache.wicket.authroles.authentication.pages.SignInPage;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;

public class FeaturePageTest extends AbstractWicketPageTest {
    @Test
    public void testRendersSuccessfully() {
        //start and render the test page
        tester.startPage(FeaturePage.class);

        tester.assertRenderedPage(SignInPage.class);

        FormTester formTester
                = tester.newFormTester("signInPanel:signInForm");
        formTester.setValue("username", "foo");
        formTester.setValue("password", "bar");
        formTester.submit();

        //assert rendered page class
        tester.assertRenderedPage(FeaturePage.class);
    }
}