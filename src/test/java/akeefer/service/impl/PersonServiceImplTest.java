package akeefer.service.impl;

import akeefer.model.Aktivitaet;
import akeefer.model.User;
import akeefer.repository.UserRepository;
import akeefer.service.PersonService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static com.google.appengine.api.datastore.KeyFactory.createKey;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testApplicationContext.xml"})
public class PersonServiceImplTest {

    @Autowired
    private PersonServiceImpl personService;

    //@Autowired
    //private UserRepository userRepository;

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
        PersonService spy = spy(personService);
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
        String personScript = spy.createPersonScript(logedIn);
        System.out.println(personScript);
        assertEquals("var personen = [\n" +
                "        {id: 'user2', distance: 0},\n" +
                "        {id: 'user3', distance: 0},\n" +
                "        {id: 'foo', distance: 4711}\n" +
                "    ];", personScript);
    }
}