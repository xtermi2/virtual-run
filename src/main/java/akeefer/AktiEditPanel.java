package akeefer;

import akeefer.model.Aktivitaet;
import akeefer.model.AktivitaetsAufzeichnung;
import akeefer.model.AktivitaetsTyp;
import akeefer.service.PersonService;
import org.apache.wicket.bean.validation.PropertyValidator;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Arrays;
import java.util.Date;

public class AktiEditPanel extends Panel {

    @SpringBean
    private PersonService personService;

    public AktiEditPanel(String id, IModel<Aktivitaet> model) {
        super(id, model);
        // Create feedback panel and add to page
        add(new FeedbackPanel("feedback"));

        Form<Aktivitaet> form = new Form<Aktivitaet>("form", new CompoundPropertyModel<Aktivitaet>(model)) {
            @Override
            protected void onSubmit() {
                Aktivitaet akt = getModel().getObject();
                akt.setEingabeDatum(new Date());
                personService.createAktivitaet(akt, VRSession.get().getUser());
                setResponsePage(HomePage.class);
            }
        };
        add(form);

        form.add(new TextField<String>("meter").add(new PropertyValidator()));

        form.add(new DropDownChoice<AktivitaetsTyp>("typ", Arrays.asList(AktivitaetsTyp.values())) {
            @Override
            protected CharSequence getDefaultChoice(String selectedValue) {
                // Dadurch kommt die "Bitte Waehlen" auswahl nicht
                return "";
            }
        }.add(new PropertyValidator()));

        form.add(new TextField<String>("bezeichnung").add(new PropertyValidator()));

        DatePicker datePicker = new DatePicker();
        datePicker.setShowOnFieldClick(true);
        datePicker.setAutoHide(true);
        form.add(new DateTextField("aktivitaetsDatum", "dd.MM.yyyy").add(datePicker).add(new PropertyValidator()));

        form.add(new DropDownChoice<AktivitaetsAufzeichnung>("aufzeichnungsart",
                Arrays.asList(AktivitaetsAufzeichnung.values())) {
            @Override
            protected CharSequence getDefaultChoice(String selectedValue) {
                // Dadurch kommt die "Bitte Waehlen" auswahl nicht
                return "";
            }
        }.add(new PropertyValidator()));
    }
}
