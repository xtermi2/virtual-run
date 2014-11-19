package akeefer;

import org.apache.wicket.Application;
import org.apache.wicket.authroles.authentication.pages.SignInPage;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Field;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testApplicationContext.xml"})
public class AktEditPageTest {

    private WicketTester tester;

    @Autowired
    private WicketApplication myWebApplication;

    @Before
    public void setUp() throws Exception{
        // Workaround, da Applikation eine Spring Bean ist und der name beim erzeugen des testers gesetzt wird
        // die Applikation aber ein überschreiben des namen nicht erlaubt.
        Field nameField = Application.class.getDeclaredField("name");
        nameField.setAccessible(true);
        nameField.set(myWebApplication, null);

        tester = new WicketTester(myWebApplication);
    }

    @Test
    public void testRendersSuccessfully() {
        //start and render the test page
        tester.startPage(AktEditPage.class);

        tester.assertRenderedPage(SignInPage.class);

        FormTester formTester
                = tester.newFormTester("signInPanel:signInForm");
        formTester.setValue("username", "foo");
        formTester.setValue("password", "bar");
        formTester.submit();

        //assert rendered page class
        tester.assertRenderedPage(AktEditPage.class);
    }
}