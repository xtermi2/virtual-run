package akeefer.web.components;

import akeefer.model.BenachrichtigunsIntervall;
import akeefer.model.User;
import akeefer.service.PersonService;
import akeefer.web.components.interplay.conditional.ConditionalModification;
import akeefer.web.components.interplay.conditional.IConditional;
import akeefer.web.components.interplay.conditional.SimpleConditional;
import akeefer.web.components.layout.Panel;
import akeefer.web.components.validation.LocalizedPropertyValidator;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class UserSettingsPanel extends Panel<User> {

    private static final Logger logger = LoggerFactory.getLogger(UserSettingsPanel.class);

    @SpringBean
    private PersonService personService;

    public UserSettingsPanel(String id, IModel<User> model) {
        super(id, model);

        // Create feedback panel and add to page
        add(new FeedbackPanel("feedback").setFilter(new ContainerFeedbackMessageFilter(this)));

        Form<User> form = new Form<User>("form", new CompoundPropertyModel<>(model)) {
            @Override
            protected void onSubmit() {
                User user = getModelObject();
                user = personService.updateUser(user);
                setModelObject(user);
                info(getString("changedSuccess"));
            }
        };
        add(form);

        TextField<String> username = new TextField<>("username");
        form.add(username.setEnabled(false));
        form.add(new FormComponentLabel("usernameLabel", username));

        TextField<String> nickname = new TextField<>("nickname");
        form.add(nickname.add(new LocalizedPropertyValidator<String>()).add(PlaceholderBehavior.ofResourceKey("nicknamePlaceholder")));
        form.add(new FormComponentLabel("nicknameLabel", nickname));

        final DropDownChoice<BenachrichtigunsIntervall> benachrichtigunsIntervall = new DropDownChoice<BenachrichtigunsIntervall>("benachrichtigunsIntervall", Arrays.asList(BenachrichtigunsIntervall.values())) {
            @Override
            protected CharSequence getDefaultChoice(String selectedValue) {
                // Dadurch kommt die "Bitte Waehlen" auswahl nicht
                return "";
            }
        };
        form.add(benachrichtigunsIntervall.add(new LocalizedPropertyValidator<BenachrichtigunsIntervall>()));
        form.add(new FormComponentLabel("benachrichtigunsIntervallLabel", benachrichtigunsIntervall));

        CheckBox includeMeInStatisticMail = new CheckBox("includeMeInStatisticMail");
        form.add(includeMeInStatisticMail);
        form.add(new FormComponentLabel("includeMeInStatisticMailLabel", includeMeInStatisticMail));

        final EmailTextField email = new EmailTextField("email");
        form.add(email.add(new LocalizedPropertyValidator<String>()).add(PlaceholderBehavior.ofResourceKey("emailPlaceholder")));
        form.add(new FormComponentLabel("emailLabel", email));
        IConditional<BenachrichtigunsIntervall> benachrichtigunsIntervallRequired = SimpleConditional.isOneOf(
                new PropertyModel<BenachrichtigunsIntervall>(form.getModel(), "benachrichtigunsIntervall"), BenachrichtigunsIntervall.taeglich, BenachrichtigunsIntervall.woechnetlich);
        email.add(ConditionalModification.requiredIf(benachrichtigunsIntervallRequired));
        email.add(new RequiredBehavior());

        benachrichtigunsIntervall.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(email);
            }
        });
        form.add(new AbstractFormValidator() {
            @Override
            public FormComponent<?>[] getDependentFormComponents() {
                return new FormComponent[]{benachrichtigunsIntervall, email};
            }

            @Override
            public void validate(Form<?> form) {
                if (!BenachrichtigunsIntervall.deaktiviert.equals(benachrichtigunsIntervall.getConvertedInput())) {
                    if (StringUtils.isBlank(email.getInput())) {
                        error(email, "emailRequiredBenachrichtigunsIntervall");
                    }
                }
            }
        });
    }
}
