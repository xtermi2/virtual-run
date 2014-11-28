package akeefer.web.pages;

import org.apache.wicket.authroles.authentication.pages.SignInPage;
import org.junit.Test;

public class InitDatabasePageTest extends AbstractWicketPageTest {

    @Test
    public void testRendersSuccessfully() {
        //start and render the test page
        tester.startPage(InitDatabasePage.class);

        // Es wird gleich auf die MapPage weitergeleitet nachdem die User angelegt wurden
        tester.assertRenderedPage(SignInPage.class);
    }
}