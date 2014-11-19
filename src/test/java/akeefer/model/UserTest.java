package akeefer.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserTest {

    @Test
    public void testEquals() throws Exception {
        User u1 = new User(null);
        assertEquals(true, u1.equals(new User(null)));
    }
}