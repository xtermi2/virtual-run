package akeefer.service.impl;

import akeefer.model.Aktivitaet;
import akeefer.model.User;
import akeefer.service.PersonService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static akeefer.test.util.ProxyUtil.getTargetObject;
import static com.google.appengine.api.datastore.KeyFactory.createKey;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testApplicationContext.xml"})
public class PersonServiceImplTest {

    @Autowired
    @Qualifier("personServiceImpl")
    private PersonService personService;

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testCreatePersonScript() throws Exception {
        PersonServiceImpl impl = getTargetObject(personService, PersonServiceImpl.class);
        PersonService spy = spy(impl);
        // Mocks
        User user1 = new User(createKey("User", "user1"));
        user1.setUsername("foo");
        Aktivitaet aktUser1 = new Aktivitaet();
        aktUser1.setDistanzInMeter(4711);
        user1.setAktivitaeten(Arrays.asList(aktUser1));
        //user1 = userRepository.save(user1);
        User user2 = new User(createKey("User", "user2"));
        user2.setUsername("user2");
        //user2 = userRepository.save(user2);
        User user3 = new User(createKey("User", "user3"));
        user3.setUsername("user3");
        //user3 = userRepository.save(user3);
        assertFalse("user1 is equals user2", user1.equals(user2));
        List<User> users = Arrays.asList(user3, user1, user2);
        doReturn(users).when(spy).getAllUser();

        User logedIn = new User(user1.getId());
        logedIn.setUsername("hallo");
        Aktivitaet aktivitaet = new Aktivitaet();
        aktivitaet.setDistanzInMeter(1234);
        logedIn.getAktivitaeten().add(aktivitaet);
        String personScript = spy.createPersonScript(logedIn.getId());
        System.out.println(personScript);
        assertEquals("var personen = [\n" +
                "        {id: 'user2', distance: 0},\n" +
                "        {id: 'user3', distance: 0},\n" +
                "        {id: 'foo', distance: 4711}\n" +
                "    ];", personScript);
    }

    @Test
    public void testMD5Test() throws Exception {
        //Md5PasswordEncoder encoder = new Md5PasswordEncoder();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        printHash(encoder, "andi");
        printHash(encoder, "sabine");
        printHash(encoder, "norbert");
        printHash(encoder, "roland");
        printHash(encoder, "uli-hans");
    }

    private void printHash(PasswordEncoder encoder, String user) {
        System.out.println(user + ": " + encoder.encodePassword(user, null));
    }

    private void printHash(org.springframework.security.crypto.password.PasswordEncoder encoder,
                           String user) {
        System.out.println(user + ": " + encoder.encode(user));
    }
}