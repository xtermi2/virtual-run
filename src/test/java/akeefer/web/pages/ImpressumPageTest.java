package akeefer.web.pages;

import akeefer.web.WicketApplication;
import org.apache.wicket.Application;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Field;

public class ImpressumPageTest extends AbstractWicketPageTest {

    @Test
    public void testRendersSuccessfully() {
        //start and render the test page
        tester.startPage(ImpressumPage.class);

        tester.assertRenderedPage(ImpressumPage.class);
    }
}