package akeefer.service.rest;

import akeefer.model.Assertions;
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
    String json = "{\n" + "  \"users\": [\n" + "    {\n" + "      \"id\": {\n" + "        \"parentKey\": {\n" + "    " +
            "" + "      \"kind\": \"Parent\",\n" + "          \"appId\": \"afrika-run\",\n" + "          \"id\": " +
            "5629499534213120\n" + "        },\n" + "        \"kind\": \"User\",\n" + "        \"appId\": " +
            "\"afrika-run\",\n" + "        \"id\": 4785074604081152\n" + "      },\n" + "      \"username\": " +
            "\"sabine\",\n" + "      \"password\": \"$2a$12$24zTsqUGVAlGVhpeLCD16ugkusdGNGyFm6MjhwWMUkMiKodzHrCg6\","
            + "\n" + "      \"roles\": [\n" + "        \"USER\"\n" + "      ],\n" + "      " +
            "\"benachrichtigunsIntervall\": \"deaktiviert\",\n" + "      \"includeMeInStatisticMail\": false\n" + "  " +
            "" + "  }],\n" + "  \"aktivitaeten\": [\n" + "    {\n" + "      \"id\": {\n" + "        \"parentKey\": " +
            "{\n" + "          \"parentKey\": {\n" + "            \"kind\": \"Parent\",\n" + "            \"appId\": " +
            "" + "\"afrika-run\",\n" + "            \"id\": 5629499534213120\n" + "          },\n" + "          " +
            "\"kind\": " + "\"User\",\n" + "          \"appId\": \"afrika-run\",\n" + "          \"id\": " +
            "4785074604081152\n" + "    " + "    },\n" + "        \"kind\": \"Aktivitaet\",\n" + "        \"appId\": " +
            "\"afrika-run\",\n" + "        " + "\"id\": 4556376185503744\n" + "      },\n" + "      " +
            "\"distanzInKilometer\": 7.238,\n" + "      \"typ\": " + "\"inlineskaten\",\n" + "      " +
            "\"aktivitaetsDatum\": \"Aug 1, 2017 4:05:49 PM\",\n" + "      " + "\"eingabeDatum\": \"Sep 8, 2017 " +
            "4:05:49 PM\",\n" + "      \"aufzeichnungsart\": \"aufgezeichnet\",\n" + "      \"bezeichnung\": " +
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
    Assertions.assertThat(parsed.getAktivitaeten().get(0))//
            .hasAktivitaetsDatum(new LocalDateTime(2017, 8, 1, 16,5,49).toDate());
  }

}