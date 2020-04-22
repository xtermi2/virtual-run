package akeefer.web.components;

import akeefer.model.AktivitaetsAufzeichnung;
import akeefer.model.AktivitaetsTyp;
import akeefer.model.mongo.Aktivitaet;
import akeefer.service.PersonService;
import akeefer.web.VRSession;
import akeefer.web.components.validation.LocalizedPropertyValidator;
import akeefer.web.pages.AktUebersichtPage;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.datetime.extensions.yui.calendar.DatePicker;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Erfassen neuer Aktivitaeten und editieren bestehender Aktivitaeten
 */
public class AktEditPanel extends Panel {

    @SpringBean
    private PersonService personService;

    public AktEditPanel(String id, IModel<Aktivitaet> model) {
        super(id, model);
        // Create feedback panel and add to page
        add(new FeedbackPanel("feedback").setFilter(new ContainerFeedbackMessageFilter(this)));

        Form<Aktivitaet> form = new Form<Aktivitaet>("form", new CompoundPropertyModel<Aktivitaet>(model)) {
            @Override
            protected void onSubmit() {
                Aktivitaet akt = getModel().getObject();
                akt = personService.createAktivitaet(akt, VRSession.get().getUser(), true);
                setModelObject(akt);
                // ueber Session, dass die Meldung auf der naechsten Seite (AktUebersichtPage) angezeigt wird
                getSession().info(getString("aktSaveSuccess"));
                setResponsePage(AktUebersichtPage.class);
            }
        };
        add(form);

        RequiredTextField<BigDecimal> distanzInKilometer = new RequiredTextField<>("distanzInKilometer");
        form.add(distanzInKilometer.add(new LocalizedPropertyValidator<BigDecimal>()).add(PlaceholderBehavior.ofResourceKey("distanzInKilometerPlaceholder")));
        form.add(new FormComponentLabel("distanzInKilometerLabel", distanzInKilometer));

        DropDownChoice<AktivitaetsTyp> typ = new DropDownChoice<AktivitaetsTyp>("typ", Arrays.asList(AktivitaetsTyp.values()),
                new EnumChoiceRenderer<AktivitaetsTyp>(form)) {
            @Override
            protected CharSequence getDefaultChoice(String selectedValue) {
                // Dadurch kommt die "Bitte Waehlen" auswahl nicht
                return "";
            }
        };
        form.add(typ.add(new LocalizedPropertyValidator<AktivitaetsTyp>()));
        form.add(new FormComponentLabel("typLabel", typ));

        TextField<String> bezeichnung = new TextField<>("bezeichnung");
        form.add(bezeichnung.add(new LocalizedPropertyValidator<String>()).add(PlaceholderBehavior.ofResourceKey("bezeichnungPlaceholder")));
        form.add(new FormComponentLabel("bezeichnungLabel", bezeichnung));

        DatePicker datePicker = new DatePicker();
        datePicker.setShowOnFieldClick(true);
        datePicker.setAutoHide(true);
        DateTextField aktivitaetsDatum = new DateTextField("aktivitaetsDatum", "dd.MM.yyyy");
        form.add(aktivitaetsDatum.add(datePicker).add(new LocalizedPropertyValidator<>()));
        form.add(new FormComponentLabel("aktivitaetsDatumLabel", aktivitaetsDatum));

        DropDownChoice<AktivitaetsAufzeichnung> aufzeichnungsart = new DropDownChoice<AktivitaetsAufzeichnung>("aufzeichnungsart",
                Arrays.asList(AktivitaetsAufzeichnung.values())) {
            @Override
            protected CharSequence getDefaultChoice(String selectedValue) {
                // Dadurch kommt die "Bitte Waehlen" auswahl nicht
                return "";
            }
        };
        form.add(aufzeichnungsart.add(new LocalizedPropertyValidator<AktivitaetsAufzeichnung>()));
        form.add(new FormComponentLabel("aufzeichnungsartLabel", aufzeichnungsart));
    }
}
