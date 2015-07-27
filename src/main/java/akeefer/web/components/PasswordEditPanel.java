package akeefer.web.components;

import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.PatternValidator;

public abstract class PasswordEditPanel extends Panel {

    private final String PASSWORD_PATTERN
            = "((?=.*\\d)(?=.*[a-zA-Z]).{6,})";

    public PasswordEditPanel(String id) {
        super(id);

        // Create feedback panel and add to page
        add(new FeedbackPanel("feedback").setFilter(new ContainerFeedbackMessageFilter(this)));

        Model<String> pwModel = Model.of("");
        Form<String> form = new Form<String>("pwEditForm", pwModel) {
            @Override
            protected void onSubmit() {
                onPasswordChange(getModel());
            }
        };
        add(form);

        PasswordTextField pw = new PasswordTextField("pw", pwModel);
        form.add(pw.add(new PatternValidator(PASSWORD_PATTERN))
                .add(PlaceholderBehavior.ofResourceKey("pwPlaceholder")));
        form.add(new FormComponentLabel("pwLabel", pw));

        PasswordTextField pwRepeat = new PasswordTextField("pwRepeat", Model.of(""));
        form.add(pwRepeat.add(PlaceholderBehavior.ofResourceKey("pwRepeatPlaceholder")));
        form.add(new FormComponentLabel("pwRepeatLabel", pwRepeat));

        form.add(new EqualPasswordInputValidator(pw, pwRepeat));
    }

    /**
     * CallBack um Logik zu implementieren, was mit dem neuen Passwort passieren soll
     *
     * @param password Password
     */
    public abstract void onPasswordChange(IModel<String> password);
}
