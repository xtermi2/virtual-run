package akeefer.web.pages;

import akeefer.model.Aktivitaet;
import akeefer.model.AktivitaetsAufzeichnung;
import akeefer.model.AktivitaetsTyp;
import akeefer.service.PersonService;
import akeefer.web.components.AktiEditPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Date;

/**
 * Erfassen neuer Aktivitaeten und editieren bestehender Aktivitaeten
 */
public class AktEditPage extends AbstractAuthenticatedBasePage {

    private static final long serialVersionUID = 1L;

    @SpringBean
    private PersonService personService;

    public AktEditPage(final PageParameters parameters) {
        super(parameters, false, true);

        Aktivitaet akt = new Aktivitaet();
        // Default Werte einer Aktivitaet.
        akt.setTyp(AktivitaetsTyp.laufen);
        akt.setAktivitaetsDatum(new Date());
        akt.setAufzeichnungsart(AktivitaetsAufzeichnung.aufgezeichnet);
        add(new AktiEditPanel("editPanel", Model.of(akt)));
    }

    public AktEditPage(final PageParameters parameters, final IModel<Aktivitaet> akt) {
        super(parameters, false, true);
        add(new AktiEditPanel("editPanel", akt));
    }
}
