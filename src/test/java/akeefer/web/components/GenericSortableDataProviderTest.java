package akeefer.web.components;

import akeefer.model.AktivitaetsAufzeichnung;
import akeefer.model.AktivitaetsTyp;
import akeefer.model.mongo.Aktivitaet;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.Lists;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.model.Model;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class GenericSortableDataProviderTest {

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
    public void testSortierung() throws Exception {
        Aktivitaet akt1 = new Aktivitaet();
        akt1.setIdFromUUID(UUID.randomUUID());
        akt1.setDistanzInMeter(1);
        akt1.setAktivitaetsDatum(new DateTime().toDate());
        akt1.setTyp(AktivitaetsTyp.laufen);
        akt1.setAufzeichnungsart(AktivitaetsAufzeichnung.aufgezeichnet);
        akt1.setBezeichnung("a");

        Aktivitaet akt2 = new Aktivitaet();
        akt2.setIdFromUUID(UUID.randomUUID());
        akt2.setDistanzInMeter(2);
        akt2.setAktivitaetsDatum(new DateTime().plusDays(1).toDate());
        akt2.setTyp(AktivitaetsTyp.schwimmen);
        akt2.setAufzeichnungsart(AktivitaetsAufzeichnung.geschaetzt);
        akt2.setBezeichnung("b");

        GenericSortableDataProvider<Aktivitaet> sortableDataProvider =
                new GenericSortableDataProvider<Aktivitaet>(new Model((java.io.Serializable) Arrays.asList(akt1, akt2)));

        sortableDataProvider.setSort("distanzInKilometer", SortOrder.ASCENDING);
        Iterator<? extends Aktivitaet> iterator = sortableDataProvider.iterator(0, 2);
        assertOrder(iterator, akt1, akt2);

        sortableDataProvider.setSort("distanzInKilometer", SortOrder.DESCENDING);
        iterator = sortableDataProvider.iterator(0, 2);
        assertOrder(iterator, akt2, akt1);

        sortableDataProvider.setSort("bezeichnung", SortOrder.ASCENDING);
        iterator = sortableDataProvider.iterator(0, 2);
        assertOrder(iterator, akt1, akt2);

        sortableDataProvider.setSort("bezeichnung", SortOrder.DESCENDING);
        iterator = sortableDataProvider.iterator(0, 2);
        assertOrder(iterator, akt2, akt1);

        sortableDataProvider.setSort("typ", SortOrder.ASCENDING);
        iterator = sortableDataProvider.iterator(0, 2);
        assertOrder(iterator, akt1, akt2);

        sortableDataProvider.setSort("typ", SortOrder.DESCENDING);
        iterator = sortableDataProvider.iterator(0, 3);
        assertOrder(iterator, akt2, akt1);

        sortableDataProvider.setSort("aktivitaetsDatum", SortOrder.ASCENDING);
        iterator = sortableDataProvider.iterator(0, 2);
        assertOrder(iterator, akt1, akt2);

        sortableDataProvider.setSort("aktivitaetsDatum", SortOrder.DESCENDING);
        iterator = sortableDataProvider.iterator(0, 2);
        assertOrder(iterator, akt2, akt1);

        sortableDataProvider.setSort("aufzeichnungsart", SortOrder.ASCENDING);
        iterator = sortableDataProvider.iterator(0, 2);
        assertOrder(iterator, akt1, akt2);

        sortableDataProvider.setSort("aufzeichnungsart", SortOrder.DESCENDING);
        iterator = sortableDataProvider.iterator(0, 123);
        assertOrder(iterator, akt2, akt1);
    }

    private void assertOrder(Iterator<? extends Aktivitaet> actuals, Aktivitaet... expecteds) {
        ArrayList<Aktivitaet> aktualsList = Lists.newArrayList(actuals);
        assertEquals("Anzahl aktivitaeten", aktualsList.size(), expecteds.length);

        for (int i = 0; i < aktualsList.size(); i++) {
            Aktivitaet actual = aktualsList.get(i);
            Aktivitaet expected = expecteds[i];
            assertEquals("Aktivitaet[" + i + "]", expected, actual);
        }
    }
}