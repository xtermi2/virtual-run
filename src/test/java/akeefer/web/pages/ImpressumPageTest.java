package akeefer.web.pages;

import org.junit.Test;

public class ImpressumPageTest extends AbstractWicketPageTest {

    @Test
    public void testRendersSuccessfully() {
        //start and render the test page
        tester.startPage(ImpressumPage.class);

        tester.assertRenderedPage(ImpressumPage.class);
    }
}