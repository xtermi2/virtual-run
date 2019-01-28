package akeefer.web.pages;

import akeefer.model.Aktivitaet;
import akeefer.model.AktivitaetsAufzeichnung;
import akeefer.model.AktivitaetsTyp;
import akeefer.web.components.AktEditPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.Date;

/**
 * Erfassen neuer Aktivitaeten und editieren bestehender Aktivitaeten
 */
public class AktEditPage extends AbstractAuthenticatedBasePage {

    private static final long serialVersionUID = 1L;

    public AktEditPage(PageParameters parameters) {
        super(parameters, false, true, false, false);

        Aktivitaet akt = new Aktivitaet();
        // Default Werte einer Aktivitaet.
        akt.setTyp(AktivitaetsTyp.laufen);
        akt.setAktivitaetsDatum(new Date());
        akt.setAufzeichnungsart(AktivitaetsAufzeichnung.aufgezeichnet);
        add(new AktEditPanel("editPanel", Model.of(akt)));
    }

    public AktEditPage(PageParameters parameters, IModel<Aktivitaet> akt) {
        super(parameters, false, true, false, false);
        add(new AktEditPanel("editPanel", akt));
    }
}
