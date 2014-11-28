package akeefer.web.pages;

import akeefer.web.WicketApplication;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.apache.wicket.Application;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Field;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testApplicationContext.xml"})
public abstract class AbstractWicketPageTest {

    protected WicketTester tester;

    @Autowired
    protected WicketApplication myWebApplication;

    protected final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Before
    public void setUp() throws Exception {
        helper.setUp();

        // Workaround, da Applikation eine Spring Bean ist und der name beim erzeugen des testers gesetzt wird
        // die Applikation aber ein überschreiben des namen nicht erlaubt.
        Field nameField = Application.class.getDeclaredField("name");
        nameField.setAccessible(true);
        nameField.set(myWebApplication, null);

        tester = new WicketTester(myWebApplication);
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }
}
