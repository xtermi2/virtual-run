package akeefer;

import akeefer.model.Aktivitaet;
import akeefer.model.AktivitaetsAufzeichnung;
import akeefer.model.AktivitaetsTyp;
import akeefer.service.PersonService;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Date;

@AuthorizeInstantiation("USER")
public class AktiEditPage extends WebPage{

    private static final long serialVersionUID = 1L;

    @SpringBean
    private PersonService personService;

    public AktiEditPage(final PageParameters parameters) {
        super(parameters);


    }
}
