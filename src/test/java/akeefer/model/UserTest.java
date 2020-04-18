package akeefer.model;

import akeefer.model.mongo.User;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserTest {

    @Test
    public void testEquals() {
        User u1 = new User((String) null);
        assertEquals(true, u1.equals(new User((String) null)));
    }
}