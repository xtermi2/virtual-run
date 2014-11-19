package akeefer;

import akeefer.model.Aktivitaet;
import akeefer.model.AktivitaetsAufzeichnung;
import akeefer.model.AktivitaetsTyp;
import akeefer.service.PersonService;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Date;

@AuthorizeInstantiation("USER")
public class AktEditPage extends WebPage {

    private static final long serialVersionUID = 1L;

    @SpringBean
    private PersonService personService;

    public AktEditPage(final PageParameters parameters) {
        super(parameters);

        Aktivitaet akt = new Aktivitaet();
        // Default Werte einer Aktivitaet.
        akt.setTyp(AktivitaetsTyp.laufen);
        akt.setAktivitaetsDatum(new Date());
        akt.setAufzeichnungsart(AktivitaetsAufzeichnung.aufgezeichnet);
        add(new AktiEditPanel("editPanel", Model.of(akt)));
    }
}
