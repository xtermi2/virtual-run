package akeefer.service.rest;

import akeefer.service.dto.DbBackup;
import org.joda.time.LocalDateTime;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Andreas Keefer
 */
public class StatisticRestServiceTest {
    @Test
    public void parse() throws Exception {
        String json = "{\n" + "  \"users\": [\n" + "    {\n" + "      \"id\": \"agR0ZXN0chELEgRVc2VyGICAgICXyYcJDA\",\n" + "      \"username\": " +
                "\"sabine\",\n" + "      \"password\": \"$2a$12$24zTsqUGVAlGVhpeLCD16ugkusdGNGyFm6MjhwWMUkMiKodzHrCg6\","
                + "\n" + "      \"roles\": [\n" + "        \"USER\"\n" + "      ],\n" + "      " +
                "\"benachrichtigunsIntervall\": \"deaktiviert\",\n" + "      \"includeMeInStatisticMail\": false\n" + "  " +
                "" + "  }],\n" + "  \"aktivitaeten\": [\n" + "    {\n" + "      \"id\": \"agR0ZXN0chELEgRVc2VyGICAgICXyYcJDA\",\n" + "      " +
                "\"distanzInKilometer\": 7.238,\n" + "      \"typ\": " + "\"inlineskaten\",\n" + "      " +
                "\"aktivitaetsDatum\": \"2017-08-01T16:05:49.000+0200\",\n\"eingabeDatum\": \"2017-09-08T16:05:49.000+0200\",\n" + "      \"aufzeichnungsart\": \"aufgezeichnet\",\n" + "      \"bezeichnung\": " +
                "\"UkEyjFGgyN\",\n" + "      \"owner\": \"sabine\"\n" + "    }]\n" + "}";

        DbBackup parsed = StatisticRestService.parse(json);

        assertThat(parsed.getUsers())//
                .hasSize(1)  //
                .extracting("username")//
                .containsExactly("sabine");

        assertThat(parsed.getAktivitaeten())//
                .hasSize(1)  //
                .extracting("owner")//
                .containsExactly("sabine");
        assertThat(parsed.getAktivitaeten().get(0).getAktivitaetsDatum())//
                .isCloseTo(new LocalDateTime(2017, 8, 1, 16, 5, 49).toDate(),
                        1000 * 60 * 60 * 2);
    }

}