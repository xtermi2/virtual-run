package akeefer.service.impl;

import akeefer.model.User;
import akeefer.test.service.PersonServiceMock;
import org.hamcrest.core.StringEndsWith;
import org.hamcrest.core.StringStartsWith;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testApplicationContext.xml"})
public class PersonServiceImplTest {

    @Autowired
    private PersonServiceImpl personService;

    @Test
    public void testCreatePersonScript() throws Exception {
        PersonServiceImpl spy = spy(personService);
        doReturn(new PersonServiceMock().getAll()).when(spy).getAll();

        User logedIn = new User(1L);
        logedIn.setUsername("foo");
        String personScript = spy.createPersonScript(logedIn);
        System.out.println(personScript);
        assertThat(personScript, StringStartsWith.startsWith("var personen = [\n" +
                "        {id: 'foo', distance: "));
        assertThat(personScript, StringEndsWith.endsWith("}\n" +
                "    ];"));
    }
}