package akeefer.service.dto;

import akeefer.model.AktivitaetsTyp;
import akeefer.model.User;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.apache.commons.lang3.SystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertThat;

public class StatisticTest {

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
    public void testToMailString() throws Exception {
        User user = new User(KeyFactory.createKey("user", "user1"));
        user.setNickname("Hans");
        Statistic statistic = new Statistic(user)
                .add(AktivitaetsTyp.laufen, new BigDecimal("1.2345"))
                .add(AktivitaetsTyp.schwimmen, BigDecimal.ONE)
                .add(AktivitaetsTyp.radfahren, BigDecimal.ONE)
                .add(AktivitaetsTyp.spezierengehen, BigDecimal.ONE)
                .add(AktivitaetsTyp.wandern, BigDecimal.ONE)
                .add(AktivitaetsTyp.inlineskaten, BigDecimal.ONE)
                .add(AktivitaetsTyp.skateboarden, BigDecimal.ONE)
                .add(AktivitaetsTyp.sonstiges, BigDecimal.ONE);

        assertThat(statistic.toMailString(), startsWith("Hans ist ..." + SystemUtils.LINE_SEPARATOR));
        assertThat(statistic.toMailString(), containsString("... 1km rad gefahren" + SystemUtils.LINE_SEPARATOR));
        assertThat(statistic.toMailString(), containsString("... 1km spazieren gegangen" + SystemUtils.LINE_SEPARATOR));
        assertThat(statistic.toMailString(), containsString("... 1km geschwommen" + SystemUtils.LINE_SEPARATOR));
        assertThat(statistic.toMailString(), containsString("... 1km ?" + SystemUtils.LINE_SEPARATOR));
        assertThat(statistic.toMailString(), containsString("... 1km gewandert" + SystemUtils.LINE_SEPARATOR));
        assertThat(statistic.toMailString(), containsString("... 1,235km gelaufen" + SystemUtils.LINE_SEPARATOR));
        assertThat(statistic.toMailString(), containsString("... 1km inline geskatet" + SystemUtils.LINE_SEPARATOR));
        assertThat(statistic.toMailString(), containsString("... 1km skateboard gefahren" + SystemUtils.LINE_SEPARATOR));
    }
}