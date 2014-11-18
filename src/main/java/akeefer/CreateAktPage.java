package akeefer;

import akeefer.model.Aktivitaet;
import akeefer.model.AktivitaetsAufzeichnung;
import akeefer.model.AktivitaetsTyp;
import akeefer.model.User;
import akeefer.service.PersonService;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Date;

@AuthorizeInstantiation("USER")
public class CreateAktPage extends WebPage{

    private static final long serialVersionUID = 1L;

    @SpringBean
    private PersonService personService;

    public CreateAktPage(final PageParameters parameters) {
        super(parameters);

        User user = VRSession.get().getUser();

        Aktivitaet akt = new Aktivitaet();
        akt.setMeter(150);
        akt.setTyp(AktivitaetsTyp.laufen);
        akt.setAktivitaetsDatum(new Date());
        akt.setEingabeDatum(new Date());
        akt.setAufzeichnungsart(AktivitaetsAufzeichnung.geschaetzt);
        akt.setUser(user);

        user.getAktivitaeten().add(akt);

        personService.createAktivitaet(akt, user);
    }
}
