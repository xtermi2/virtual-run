package akeefer.repository.mongo;

import akeefer.model.SecurityRole;
import akeefer.model.mongo.User;
import com.google.common.collect.Sets;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testApplicationContext.xml"})
public class MongoUserRepositoryTest {

    @Autowired
    private MongoUserRepository userRepository;

    @Before
    public void setUp() {
        userRepository.deleteAll();
    }

    @After
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void testMongo() {
        List<User> res = userRepository.findAll();
        assertThat(res)
                .as("all Users")
                .isEmpty();

        String id = String.valueOf(5099850341285888L);
        User andi = userRepository.save(User.builder()
                .id(id)
                .username("andi")
                .password("andi")
                .role(SecurityRole.ADMIN)
                .role(SecurityRole.USER)
                .build());

        assertThat(andi.getId())
                .as("id")
                .isNotNull();

        Optional<User> one = userRepository.findById(id);
        assertThat(one)
                .isNotEmpty()
                .as("userById")
                .contains(andi)
                .hasValueSatisfying(user -> assertThat(user.getId())
                        .as("id")
                        .isEqualTo(id));

        User findByUsername = userRepository.findByUsername("andi");
        assertThat(findByUsername.getId())
                .as("id")
                .isEqualTo(id);
    }

    @Test
    public void findByUsernameIn_returns_only_users_with_givern_username() {
        User andi = userRepository.save(User.builder()
                .username("andi")
                .password("andi")
                .role(SecurityRole.ADMIN)
                .role(SecurityRole.USER)
                .build());
        User foo = userRepository.save(User.builder()
                .username("foo")
                .password("foo")
                .role(SecurityRole.USER)
                .build());
        User bar = userRepository.save(User.builder()
                .username("bar")
                .password("bar")
                .role(SecurityRole.USER)
                .build());

        List<User> res = userRepository.findByUsernameIn(Sets.newHashSet(foo.getUsername(), andi.getUsername()));

        assertThat(res)
                .containsExactlyInAnyOrder(foo, andi)
                .doesNotContain(bar);
    }
}